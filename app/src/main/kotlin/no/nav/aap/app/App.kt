package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.toDto
import no.nav.aap.app.security.AapAuth
import no.nav.aap.app.security.AzureADProvider
import no.nav.aap.app.security.OAuthConfig
import no.nav.aap.domene.Søker
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.entitet.Personident
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.slf4j.LoggerFactory
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(val oauth: OAuthConfig, val kafka: KafkaConfig)

internal val log = LoggerFactory.getLogger("app")

fun Application.server(kafka: Kafka = KStreams()) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }
    install(AapAuth) { providers += AzureADProvider(config.oauth.azure) }
    install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }

    val topics = Topics(config.kafka)
    val topology = createTopology(topics)
    kafka.init(topology, config.kafka)

    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    routing {
        api(kafka)
        devTools(kafka, topics)
        actuator(prometheus, kafka)
    }
}

fun createTopology(topics: Topics): Topology = StreamsBuilder().apply {
    val søkere = table(topics.søkere.name, topics.søkere.consumed("soker-consumed"), materialized("soker-store"))
    søknadStream(søkere, topics)
    medlemStream(søkere, topics)
    medlemResponseStream(topics)
}.build()

fun Routing.api(kafka: Kafka) {
    val søkerStore = kafka.getStore<AvroSøker>("soker-store")

    authenticate {
        route("/api") {
            get("/sak") {
                val søkere = søkerStore.allValues().map(AvroSøker::toDto).map(Søker::create)
                call.respond(søkere.toFrontendSaker())
            }

            get("/sak/neste") {
                val søker = søkerStore.allValues().map(AvroSøker::toDto).map(Søker::create)
                call.respond(søker.toFrontendSaker().first())
            }

            get("/sak/{personident}") {
                val personident = Personident(call.parameters.getOrFail("personident"))
                val søkere = søkerStore.allValues().map(AvroSøker::toDto).map(Søker::create)
                val frontendSaker = søkere.toFrontendSaker(personident)
                call.respond(frontendSaker)
            }
        }
    }
}

fun Routing.devTools(kafka: Kafka, topics: Topics) {
    val søkerProducer = kafka.createProducer(topics.søkere)

    fun <V> Producer<String, V>.tombstone(key: String) =
        send(ProducerRecord(topics.søkere.name, key, null)).get()

    route("/delete/{personident}") {
        get {
            val personident = call.parameters.getOrFail("personident")
            søkerProducer.tombstone(personident).also {
                log.info("produced tombstone [${topics.søkere.name}] [$personident] [null]")
            }
            call.respondText("Deleted $personident")
        }
    }
}

fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: Kafka) {
    route("/actuator") {
        get("/metrics") { call.respond(prometheus.scrape()) }
        get("/live") { call.respond("vedtak") }
        get("/ready") {
            val status = if (kafka.healthy()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(status, "vedtak")
        }
    }
}
