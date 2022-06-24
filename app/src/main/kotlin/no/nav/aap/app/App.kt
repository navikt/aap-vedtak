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
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.migrateStateStore
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.minutes

private val secureLog: Logger = LoggerFactory.getLogger("secureLog")

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
        .produce(Tables.søkere)

    søkerKTable.scheduleMetrics(Tables.søkere, 2.minutes, registry)
    søkerKTable.migrateStateStore(Tables.søkere, migrationProducer)

    streams.søknadStream(søkerKTable)
    streams.medlemStream(søkerKTable)
    streams.inntekterStream(søkerKTable)
    streams.manuellStream(søkerKTable)

    return streams.build()
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

    fun <V> Producer<String, V>.produce(topic: Topic<V>, key: String, value: V) {
        send(ProducerRecord(topic.name, key, value)) { meta, error ->
            if (error != null) secureLog.error("Produserer til Topic feilet", error)
            else secureLog.trace(
                "Produserer til Topic",
                kv("key", key),
                kv("topic", topic.name),
                kv("partition", meta.partition()),
                kv("offset", meta.offset()),
            )
        }
    }

    fun <V> Producer<String, V>.tombstone(topic: Topic<V>, key: String) {
        send(ProducerRecord(topic.name, key, null)) { meta, error ->
            if (error != null) secureLog.error("Tombstoner Topic feilet", error)
            else secureLog.trace(
                "Tombstoner Topic",
                kv("key", key),
                kv("topic", topic.name),
                kv("partition", meta.partition()),
                kv("offset", meta.offset()),
            )
        }
    }

    get("/delete/{personident}") {
        val personident = call.parameters.getOrFail("personident")
        søknadProducer.tombstone(Topics.søknad, personident)
        søkerProducer.tombstone(Topics.søkere, personident)
        call.respondText("Søknad og søker med ident $personident slettes!")
    }

    get("/søknad/{personident}") {
        val personident = call.parameters.getOrFail("personident")
        val søknad = JsonSøknad()
        søknadProducer.produce(Topics.søknad, personident, søknad)
        call.respondText("Søknad $søknad mottatt!")
    }
}
