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
import kotlinx.coroutines.delay
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.stream.inntekterStream
import no.nav.aap.app.stream.manuellStream
import no.nav.aap.app.stream.medlemStream
import no.nav.aap.app.stream.søknadStream
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.streams.*
import no.nav.aap.kafka.streams.store.scheduleCleanup
import no.nav.aap.kafka.streams.store.scheduleMetrics
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ValueAndTimestamp
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

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

    kafka.connect(config.kafka, prometheus, topology(prometheus))

    routing {
        devTools(kafka, config.kafka)
        actuator(prometheus, kafka)
    }
}

internal fun topology(registry: MeterRegistry): Topology {
    val streams = StreamsBuilder()
    val søkerKTable = streams
        .consume(Topics.søkere)
        .filterNotNull("filter-soker-tombstones")
        .produce(Tables.søkere)

    søkerKTable.scheduleCleanup(Tables.søkere, 1.seconds, søkereToDelete)
    søkerKTable.purge(Tables.søkere, 1.seconds, purgeFlag, purgeListeners)
    søkerKTable.scheduleMetrics(Tables.søkere, 2.minutes, registry)

    streams.søknadStream(søkerKTable)
    streams.medlemStream(søkerKTable)
    streams.manuellStream(søkerKTable)
    streams.inntekterStream(søkerKTable)

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

val søkereToDelete: MutableList<String> = mutableListOf()
val purgeListeners: MutableList<(String) -> Unit> = mutableListOf()
val purgeFlag: AtomicBoolean = AtomicBoolean(false)

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
            søkereToDelete.add(personident)
            secureLog.info("produced [${Topics.søkere}] [$personident] [tombstone]")
        }

        call.respondText("Søknad og søker med ident $personident slettes!")
    }

    get("/deleteAll}") {
        purgeListeners.add { personident ->
            søknadProducer.produce(Topics.søknad, personident, null).also {
                secureLog.info("produced [${Topics.søknad}] [$personident] [tombstone]")
            }

            søkerProducer.produce(Topics.søkere, personident, null).also {
                søkereToDelete.add(personident)
                secureLog.info("produced [${Topics.søkere}] [$personident] [tombstone]")
            }
        }
        purgeFlag.set(true)

        call.respondText("Sletter alle søknader og søkere!")
    }

    get("/søknad/{personident}") {
        val personident = call.parameters.getOrFail("personident")

        søknadProducer.produce(Topics.søknad, personident, null).also {
            secureLog.info("produced [${Topics.søknad}] [$personident] [tombstone]")
        }

        søkerProducer.produce(Topics.søkere, personident, null).also {
            søkereToDelete.add(personident)
            secureLog.info("produced [${Topics.søkere}] [$personident] [tombstone]")
            delay(2000L) // vent på delete i state store
        }

        val søknad = JsonSøknad()

        søknadProducer.produce(Topics.søknad, personident, søknad).also {
            secureLog.info("produced [${Topics.søkere}] [$personident] [$søknad]")
        }

        call.respondText("Søknad $søknad mottatt!")
    }
}

private typealias PurgeListener<K> = (K) -> Unit

private class StateStorePurger<K, V>(
    private val table: Table<V>,
    private val interval: Duration,
    private val purgeFlag: AtomicBoolean,
    private val purgeListeners: MutableList<PurgeListener<K>>,
) : Processor<K, V, Void, Void> {
    override fun process(record: Record<K, V>) {}

    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<K, ValueAndTimestamp<V>>>(table.stateStoreName)
        context.schedule(interval.toJavaDuration(), PunctuationType.WALL_CLOCK_TIME) {
            if (purgeFlag.getAndSet(false)) {
                val keyValueIterator = store.all()
                while (keyValueIterator.hasNext()) {
                    val next: KeyValue<K, ValueAndTimestamp<V>> = keyValueIterator.next()
                    purgeListeners.forEach { listener -> listener(next.key) }
                    keyValueIterator.remove()
                }
                purgeListeners.clear()
            }
        }
    }

    private companion object {
        private val secureLog = LoggerFactory.getLogger("secureLog")
    }
}

fun <K, V> KTable<K, V>.purge(
    table: Table<V>,
    interval: Duration,
    purgeFlag: AtomicBoolean,
    purgeListeners: MutableList<PurgeListener<K>>,
) = toStream().process(
    ProcessorSupplier { StateStorePurger(table, interval, purgeFlag, purgeListeners) },
    named("purge-${table.stateStoreName}"),
    table.stateStoreName
)
