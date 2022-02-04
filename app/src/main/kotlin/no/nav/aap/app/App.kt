package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.KafkaConfig
import no.nav.aap.app.kafka.KafkaFactory
import no.nav.aap.app.modell.KafkaSøknad
import no.nav.aap.app.security.AapAuth
import no.nav.aap.app.security.AzureADProvider
import no.nav.aap.app.security.IssuerConfig
import no.nav.aap.domene.*
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.hendelse.Søknad
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import java.time.Duration
import kotlin.concurrent.thread

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(val oauth: OAuthConfig, val kafka: KafkaConfig)
data class OAuthConfig(val azure: IssuerConfig)

private val søknader = mutableListOf<KafkaSøknad>()
private val søkere = mutableListOf<Søker>()

private val oppgaver = mutableListOf<FrontendSak>()

//Test hack
internal fun tømLister() {
    søknader.clear()
    søkere.clear()
    oppgaver.clear()
}

fun Application.server(
    config: Config = loadConfig(),
    kafkaConsumer: Consumer<String, KafkaSøknad> = KafkaFactory.createConsumer(config.kafka),
) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) { registry = prometheus }
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }
    install(AapAuth) { providers += AzureADProvider(config.oauth.azure) }

    kafkaConsumer.subscribe(listOf(config.kafka.topic)).also {
        log.info("subscribed to topic ${config.kafka.topic}")
        log.info("broker: ${config.kafka.brokers}")
    }

    søknadKafkaListener(kafkaConsumer)
    environment.monitor.subscribe(ApplicationStopping) { kafkaConsumer.close() }

    routing {
        api()
        actuator(prometheus)
    }
}

class SøkerLytter : Lytter {
    private lateinit var søker: Søker
    private var oppgaveOpprettet = false

    override fun oppdaterSøker(søker: Søker) {
        this.søker = søker
    }

    override fun sendOppgave(behov: Behov) {
        oppgaveOpprettet = true
    }

    override fun finalize() {
        if (oppgaveOpprettet) oppgaver.add(søker.toFrontendSaker().last())
    }
}

fun Application.søknadKafkaListener(kafkaConsumer: Consumer<String, KafkaSøknad>) {
    val timeout = Duration.ofMillis(10L)

    thread {
        while (true) {
            val records = kafkaConsumer.poll(timeout)
            records.asSequence()
                .logConsumed(log)
                .filterNotNull()
                .map { it.value() }
                .onEach(søknader::add)
                .map { søknad -> Søknad(Personident(søknad.ident.verdi), Fødselsdato(søknad.fødselsdato)) }
                .map { søknad -> søknad to SøkerLytter() }
                .map { (søknad, lytter) -> Triple(søknad.opprettSøker(lytter), søknad, lytter) }
                .onEach { (søker) -> søkere.add(søker) }
                .onEach { (søker, søknad) -> søker.håndterSøknad(søknad) }
                .forEach { (_, _, lytter) -> lytter.finalize() }
        }
    }
}

fun Routing.api() {
    authenticate {
        route("/api") {
            get("/sak") {
                call.respond(søkere.toFrontendSaker())
            }

            get("/sak/neste") {
                call.respond(oppgaver.last())
            }

            get("/sak/{personident}") {
                val personident = Personident(call.parameters.getOrFail("personident"))
                val frontendSaker = søkere.toFrontendSaker(personident)
                call.respond(frontendSaker)
            }
        }
    }
}

private fun Routing.actuator(prometheus: PrometheusMeterRegistry) {
    route("/actuator") {
        get("/healthy") {
            call.respondText("Hello, world!")
        }
        get("/metrics") {
            call.respond(prometheus.scrape())
        }
    }
}

private val PipelineContext<Unit, ApplicationCall>.log get() = application.log

private fun <K, V> Sequence<ConsumerRecord<K, V>?>.logConsumed(log: Logger): Sequence<ConsumerRecord<K, V>?> =
    onEach { record ->
        record?.let {
            log.info("Consumed=${it.topic()} key=${it.key()} offset=${it.offset()} partition=${it.partition()}")
        }
    }