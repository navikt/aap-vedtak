package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.util.collections.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.config.Config
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.*
import no.nav.aap.app.stream.inntekterStream
import no.nav.aap.app.stream.manuellStream
import no.nav.aap.app.stream.medlemStream
import no.nav.aap.app.stream.mock.inntekterResponseStream
import no.nav.aap.app.stream.søknadStream
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.slf4j.LoggerFactory
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

private const val SØKERE_STORE_NAME = "soker-state-store"
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
    inntekterResponseStream(topics)
}.build()

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: Kafka) {
    route("/actuator") {
        get("/metrics") { call.respond(prometheus.scrape()) }
        get("/live") { call.respond("vedtak") }
        get("/ready") {
            when (kafka.healthy() && kafka.started()) {
                true -> call.respond(HttpStatusCode.OK, "vedtak")
                false -> call.respond(HttpStatusCode.InternalServerError, "vedtak")
            }
        }
    }
}

val søkereToDelete: ConcurrentList<String> = ConcurrentList()

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
