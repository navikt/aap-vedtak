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
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.concurrent.thread

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(val oauth: OAuthConfig, val kafka: KafkaConfig)
data class OAuthConfig(val azure: IssuerConfig)

private val søknader = mutableListOf<KafkaSøknad>()
private val oppgaver = mutableListOf<Oppgave>()

fun Application.server(
    config: Config = loadConfig(),
    kafkaConsumer: Consumer<String, KafkaSøknad> = KafkaFactory.createConsumer(config.kafka),
) {

    install(ContentNegotiation) { jackson() }
    install(AapAuth) { providers += AzureADProvider(config.oauth.azure) }

    kafkaConsumer.subscribe(listOf(config.kafka.topic)).also {
        log.info("subscribed to topic ${config.kafka.topic}")
        log.info("broker: ${config.kafka.brokers}")
    }

    søknadKafkaListener(kafkaConsumer)
    environment.monitor.subscribe(ApplicationStopping) { kafkaConsumer.close() }

    routing {
        api()
        actuator()
    }
}

fun Application.søknadKafkaListener(kafkaConsumer: Consumer<String, KafkaSøknad>) {
    val timeout = Duration.ofMillis(10L)

    fun toOppgave(søknad: KafkaSøknad): Oppgave = Oppgave(
        oppgaveId = søknad.hashCode(),
        personident = Personident(søknad.ident.verdi),
        alder = søknad.fødselsdato.until(LocalDate.now(), ChronoUnit.YEARS).toInt()
    )

    thread {
        while (true) {
            with(kafkaConsumer.poll(timeout)) {
                asSequence()
                    .logConsumed(log)
                    .filterNotNull()
                    .map(ConsumerRecord<String, KafkaSøknad>::value)
                    .onEach(søknader::add)
                    .map(::toOppgave)
                    .toList()
                    .forEach(oppgaver::add)
            }
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

private fun <K, V> Sequence<ConsumerRecord<K, V>?>.logConsumed(log: Logger): Sequence<ConsumerRecord<K, V>?> =
    onEach { record ->
        record?.let {
            log.info("Consumed=${it.topic()} key=${it.key()} offset=${it.offset()} partition=${it.partition()}")
        }
    }