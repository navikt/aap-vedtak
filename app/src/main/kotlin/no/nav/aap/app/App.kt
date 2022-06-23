package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.stream.inntekterStream
import no.nav.aap.app.stream.manuell.manuellStream
import no.nav.aap.app.stream.medlemStream
import no.nav.aap.app.stream.søknadStream
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.streams.*
import no.nav.aap.kafka.streams.store.scheduleMetrics
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ValueAndTimestamp
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private val secureLog = LoggerFactory.getLogger("secureLog")

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(val kafka: KafkaConfig)

internal fun Application.server(kafka: KStreams = KafkaStreams) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }
    install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    val migrationProducer = kafka.createProducer(config.kafka, Topics.søkere)
    val topology = topology(prometheus, migrationProducer)

    kafka.connect(
        config = config.kafka,
        registry = prometheus,
        topology = topology,
    )

    routing {
        devTools(kafka, config.kafka)
        actuator(prometheus, kafka)
    }
}

internal fun topology(registry: MeterRegistry, migrationProducer: Producer<String, SøkereKafkaDto>): Topology {
    val streams = StreamsBuilder()
    val søkerKTable = streams
        .consume(Topics.søkere)
        .filterNotNull("filter-soker-tombstones")
        .produce(Tables.søkere)

    søkerKTable.scheduleMetrics(Tables.søkere, 2.minutes, registry)

    søkerKTable.toStream().process(
        ProcessorSupplier { StateStoreMigrator(Tables.søkere, migrationProducer) },
        named("migrate-${Tables.søkere.stateStoreName}"),
        Tables.søkere.stateStoreName
    )

    streams.søknadStream(søkerKTable)
    streams.medlemStream(søkerKTable)
    streams.inntekterStream(søkerKTable)
    streams.manuellStream(søkerKTable)

    return streams.build()
}

private class StateStoreMigrator<K, V>(
    private val table: Table<V>,
    private val producer: Producer<K, V>,
) : Processor<K, V, Void, Void> {
    override fun process(record: Record<K, V>) {
        secureLog.info("state store migrator prosesserer: $record")
    }

    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<K, ValueAndTimestamp<V>>>(table.stateStoreName)
        store.all().use {
            it.asSequence().forEach { keyvalue ->
                producer.send(ProducerRecord(table.source.name, keyvalue.key, keyvalue.value.value())) { meta, error ->
                    if (error == null) secureLog.info("Migrated [$meta] ${keyvalue.key} : ${keyvalue.value.value()}")
                    else secureLog.error("klarte ikke sende migrert dto", error)
                }
            }
        }
    }

    private companion object {
        private val secureLog = LoggerFactory.getLogger("secureLog")
    }
}

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: KStreams) {
    route("/actuator") {
        get("/metrics") {
            call.respondText(prometheus.scrape())
        }
        get("/live") {
            val status = if (kafka.isLive()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("vedtak", status = status)
        }
        get("/ready") {
            val status = if (kafka.isReady()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("vedtak", status = status)
        }
    }
}

private fun Routing.devTools(kafka: KStreams, config: KafkaConfig) {
    val søkerProducer = kafka.createProducer(config, Topics.søkere)
    val søknadProducer = kafka.createProducer(config, Topics.søknad)

    fun <V> Producer<String, V>.produce(topic: Topic<V>, key: String, value: V?) =
        send(ProducerRecord(topic.name, key, value)).get()

    get("/delete/{personident}") {
        val personident = call.parameters.getOrFail("personident")

        søknadProducer.produce(Topics.søknad, personident, null).also {
            secureLog.info("produced [${Topics.søknad}] [$personident] [tombstone]")
        }

        søkerProducer.produce(Topics.søkere, personident, null).also {
            secureLog.info("produced [${Topics.søkere}] [$personident] [tombstone]")
        }

        call.respondText("Søknad og søker med ident $personident slettes!")
    }

    get("/søknad/{personident}") {
        val personident = call.parameters.getOrFail("personident")

        val søknad = JsonSøknad()

        søknadProducer.produce(Topics.søknad, personident, søknad).also {
            secureLog.info("produced [${Topics.søknad}] [$personident] [$søknad]")
        }

        call.respondText("Søknad $søknad mottatt!")
    }
}
