package no.nav.aap.app

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.KafkaConfig
import no.nav.aap.app.kafka.KafkaFactory
import no.nav.aap.app.modell.*
import no.nav.aap.app.security.AapAuth
import no.nav.aap.app.security.AzureADProvider
import no.nav.aap.app.security.IssuerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
private val oppgaver = mutableListOf<Oppgave>()

fun Application.server() {
    val config = loadConfig<Config>()

    install(ContentNegotiation) { jackson() }
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
                .map { kafkaSøknad ->
                    Oppgave(
                        oppgaveId = kafkaSøknad.hashCode(),
                        personident = Personident(kafkaSøknad.ident.verdi),
                        alder = kafkaSøknad.fødselsdato.until(LocalDate.now(), ChronoUnit.YEARS).toInt()
                    )
                }.toList()
                .forEach(oppgaver::add)
        }
    }
}

fun Routing.api() {
    authenticate {
        route("/api") {
            get("/oppgaver") {
                call.respond(Oppgaver(oppgaver))
            }

            post("/vurderAlder") {
                val aldersvurdering = call.receive<Aldersvurdering>()
                when (aldersvurdering.erMellom18og67) {
                    true -> log.info("hen for oppgaveId ${aldersvurdering.oppgaveId} er mellom 18 og 67")
                    false -> log.info("hen for oppgaveId ${aldersvurdering.oppgaveId} er IKKE mellom 18 og 67")
                }
                call.respond(HttpStatusCode.Accepted)
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
