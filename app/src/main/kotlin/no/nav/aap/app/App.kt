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
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.config.Config
import no.nav.aap.app.kafka.*
import no.nav.aap.app.stream.inntekterStream
import no.nav.aap.app.stream.manuellStream
import no.nav.aap.app.stream.medlemStream
import no.nav.aap.app.stream.mock.soknadProducer
import no.nav.aap.app.stream.søknadStream
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.KafkaStreams.State
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.slf4j.LoggerFactory
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal const val SØKERE_STORE_NAME = "soker-state-store-v1"
private val secureLog = LoggerFactory.getLogger("secureLog")

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

internal fun Application.server(kafka: Kafka = KStreams()) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }
    install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    val topics = Topics(config.kafka)
    val topology = createTopology(topics)
    kafka.start(topology, config.kafka, prometheus)

    soknadProducer(kafka, topics)

    routing {
        devTools(kafka, topics)
        actuator(prometheus, kafka)
    }
}

internal fun createTopology(topics: Topics): Topology = StreamsBuilder().apply {
    val søkerKTable = stream(topics.søkere.name, topics.søkere.consumed("soker-consumed"))
        .logConsumed()
        .filter { _, value -> value != null }
        .peek { key, value -> secureLog.info("produced [$SØKERE_STORE_NAME] K:$key V:$value") }
        .toTable(named("sokere-as-ktable"), materialized<AvroSøker>(SØKERE_STORE_NAME, topics.søkere))

    søkerKTable.scheduleCleanup(SØKERE_STORE_NAME) { record ->
        søkereToDelete.removeIf { it == record.value().personident }
    }

    søknadStream(søkerKTable, topics)
    medlemStream(søkerKTable, topics)
    manuellStream(søkerKTable, topics)
    inntekterStream(søkerKTable, topics)
}.build()

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: Kafka) {
    route("/actuator") {
        get("/metrics") { call.respond(prometheus.scrape()) }
        get("/live") {
            val status = if (kafka.state() == State.ERROR) HttpStatusCode.InternalServerError else HttpStatusCode.OK
            call.respond(status, "vedtak")
        }
        get("/ready") {
            val healthy = kafka.state() in listOf(State.CREATED, State.RUNNING, State.REBALANCING)
            when (healthy && kafka.started()) {
                true -> call.respond(HttpStatusCode.OK, "vedtak")
                false -> call.respond(HttpStatusCode.InternalServerError, "vedtak")
            }
        }
    }
}

val søkereToDelete: MutableList<String> = mutableListOf()

private fun Routing.devTools(kafka: Kafka, topics: Topics) {
    val søkerProducer = kafka.createProducer(topics.søkere)

    fun <V> Producer<String, V>.tombstone(key: String) {
        send(ProducerRecord(topics.søkere.name, key, null)).get()
    }

    route("/delete/{personident}") {
        get {
            val personident = call.parameters.getOrFail("personident")
            søkerProducer.tombstone(personident).also {
                søkereToDelete.add(personident)
                secureLog.info("produced [${topics.søkere.name}] [$personident] [tombstone]")
            }
            call.respondText("Deleted $personident")
        }
    }
}
