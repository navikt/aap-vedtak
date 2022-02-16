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
import kotlinx.coroutines.runBlocking
import no.nav.aap.app.config.Config
import no.nav.aap.app.config.KafkaConfig
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.toDto
import no.nav.aap.app.security.AapAuth
import no.nav.aap.app.security.AzureADProvider
import no.nav.aap.domene.Søker
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.entitet.Personident
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.LoggerFactory
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

internal val log = LoggerFactory.getLogger("app")

fun Application.server(kafka: Kafka = KafkaStreamsFactory()) {
    val config = loadConfig<Config>()
    val topology = createTopology(config.kafka)
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) { registry = prometheus }
    install(AapAuth) { providers += AzureADProvider(config.oauth.azure) }
    install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }

    kafka.createKafkaStream(topology, config.kafka)

    runBlocking {
        kafka.waitForStore<String, AvroSøker>("soker-store")
    }

    kafka.start()

    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    routing {
        api(kafka)
        actuator(prometheus, kafka)
    }
}

fun createTopology(config: KafkaConfig): Topology = StreamsBuilder().apply {
    val topics = Topics(config)
    val søkere = table(topics.søkere.name, topics.søkere.consumed("soker-consumed"), Materialized.`as`("soker-store"))
    søknadStream(søkere, topics)
    medlemStream(søkere, topics)
}.build()

fun Routing.api(kafka: Kafka) {
    val stateStore: ReadOnlyKeyValueStore<String, AvroSøker> = kafka.stateStore("soker-store")

    authenticate {
        route("/api") {
            get("/sak") {
                val søkere = stateStore.getAllValues().map(AvroSøker::toDto).map(Søker::create)
                call.respond(søkere.toFrontendSaker())
            }

            get("/sak/neste") {
                val søker = stateStore.getAllValues().map(AvroSøker::toDto).map(Søker::create)
                call.respond(søker.toFrontendSaker().first())
            }

            get("/sak/{personident}") {
                val personident = Personident(call.parameters.getOrFail("personident"))
                val søkere = stateStore.getAllValues().map(AvroSøker::toDto).map(Søker::create)
                val frontendSaker = søkere.toFrontendSaker(personident)
                call.respond(frontendSaker)
            }
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