package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.KafkaConfig
import no.nav.aap.app.kafka.KafkaFactory
import no.nav.aap.app.modell.KafkaSøknad
import no.nav.aap.app.security.AapAuth
import no.nav.aap.app.security.AzureADProvider
import no.nav.aap.app.security.IssuerConfig
import no.nav.aap.domene.Fødselsdato
import no.nav.aap.domene.Personident
import no.nav.aap.domene.Søker
import no.nav.aap.domene.Søknad
import no.nav.aap.domene.frontendView.FrontendVisitor
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import kotlin.concurrent.thread
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(val oauth: OAuthConfig, val kafka: KafkaConfig)
data class OAuthConfig(val azure: IssuerConfig)

private val søknader = mutableListOf<KafkaSøknad>()

private val søkere = mutableListOf<Søker>()

fun Application.server() {
    val config = loadConfig<Config>()

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }
    install(AapAuth) { providers += AzureADProvider(config.oauth.azure) }

    val kafkaConsumer = KafkaFactory.createConsumer<KafkaSøknad>(config.kafka)
    kafkaConsumer.subscribe(listOf(config.kafka.topic)).also {
        log.info("subscribed to topic ${config.kafka.topic}")
        log.info("broker: ${config.kafka.brokers}")
    }

    søknadKafkaListener(kafkaConsumer, log)
    environment.monitor.subscribe(ApplicationStopping) { kafkaConsumer.close() }

    routing {
        api()
        actuator()
    }
}

private fun søknadKafkaListener(kafkaConsumer: KafkaConsumer<String, KafkaSøknad>, log: Logger) {
    thread {
        while (true) {
            val records = kafkaConsumer.poll(10.toDuration(DurationUnit.MILLISECONDS).toJavaDuration())
            records.asSequence()
                .onEach { log.info("consumed $it") }
                .filterNotNull()
                .map { it.value() }
                .onEach(søknader::add)
                .map { søknad -> Søknad(Personident(søknad.ident.verdi), Fødselsdato(søknad.fødselsdato)) }
                .map { søknad -> søknad.opprettSøker() to søknad }
                .onEach { (søker, _) -> søkere.add(søker) }
                .forEach { (søker, søknad) -> søker.håndterSøknad(søknad) }
        }
    }
}

fun Routing.api() {
    authenticate {
        route("/api") {
            get("/saker") {
                val visitor = FrontendVisitor()
                søkere.forEach { it.accept(visitor) }
                call.respond(visitor.saker())
            }
        }
    }
}

private fun Routing.actuator() {
    get("/healthy") {
        call.respondText("Hello, world!")
    }
}

private val PipelineContext<Unit, ApplicationCall>.log get() = application.log
