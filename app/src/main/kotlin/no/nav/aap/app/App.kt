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
import kotlinx.coroutines.delay
import no.nav.aap.app.kafka.SØKERE_STORE_NAME
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.stream.inntekterStream
import no.nav.aap.app.stream.manuellStream
import no.nav.aap.app.stream.medlemStream
import no.nav.aap.app.stream.søknadStream
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.streams.*
import no.nav.aap.kafka.streams.store.scheduleCleanup
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.StreamsBuilder
import org.slf4j.LoggerFactory

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

    kafka.start(config.kafka, prometheus) {
        streamsBuilder()
    }

    routing {
        devTools(kafka, config.kafka)
        actuator(prometheus, kafka)
    }
}

internal fun StreamsBuilder.streamsBuilder() {
    val søkerKTable = consume(Topics.søkere)
        .filterNotNull("filter-soker-tombstones")
        .produce(Tables.søkere)

    søkerKTable.scheduleCleanup(SØKERE_STORE_NAME) { record ->
        søkereToDelete.removeIf { it == record.value().personident }
    }

    søknadStream(søkerKTable)
    medlemStream(søkerKTable)
    manuellStream(søkerKTable)
    inntekterStream(søkerKTable)
}

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: KStreams) {
    route("/actuator") {
        get("/metrics") {
            call.respond(prometheus.scrape())
        }
        get("/live") {
            val status = if (kafka.isLive()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(status, "vedtak")
        }
        get("/ready") {
            val status = if (kafka.isReady()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(status, "vedtak")
        }
    }
}

val søkereToDelete: MutableList<String> = mutableListOf()

private fun Routing.devTools(kafka: KStreams, config: KafkaConfig) {
    val søkerProducer = kafka.createProducer(config, Topics.søkere)
    val søknadProducer = kafka.createProducer(config, Topics.søknad)

    fun <V> Producer<String, V>.produce(topic: Topic<V>, key: String, value: V?) =
        send(ProducerRecord(topic.name, key, value)).get()

    route("/søknad/{personident}") {
        get {
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
}
