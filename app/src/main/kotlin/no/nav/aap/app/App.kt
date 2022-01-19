package no.nav.aap.app

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import no.nav.aap.app.modell.Aldersvurdering
import no.nav.aap.app.modell.Oppgave
import no.nav.aap.app.modell.Oppgaver
import no.nav.aap.app.modell.Personident
import no.nav.aap.app.security.AapAuth
import no.nav.aap.app.security.AzureADProvider
import no.nav.aap.app.security.IssuerConfig
import java.net.URL

fun podAzureEnv(fileName: String, subPath: String = "azure/", basePath: String): String =
    object {}.javaClass.getResource("$basePath$subPath$fileName")!!.readText()

fun main() {
    embeddedServer(Netty, port = 8083, module = Application::server).start(wait = true)
}

//fun Application.server(podEnvBasePath: String = "/var/run/secrets/nais.io/") {
fun Application.server(podEnvBasePath: String = "/") {
    install(ContentNegotiation) {
        jackson()
    }

    install(AapAuth) {
        val azureMockIssuer = IssuerConfig(
            name = podAzureEnv(fileName = "AZURE_OPENID_CONFIG_ISSUER", basePath = podEnvBasePath),
            discoveryUrl = URL(podAzureEnv("AZURE_APP_WELL_KNOWN_URL", basePath = podEnvBasePath)),
            audience = podAzureEnv("AZURE_APP_CLIENT_ID", basePath = podEnvBasePath),
            optionalClaims = null // kan brukes til å sjekke AD-grupper
        )

        // with only one realm, default will be azure in `route.authenticate()`
        providers += AzureADProvider(azureMockIssuer)
    }

    val søknadClient = HttpClient(CIO) {
        install(JsonFeature) { serializer = JacksonSerializer() }
    }

    val oppgaver = Oppgaver(
        listOf(
            Oppgave(1, Personident("11111111111"), 68),
            Oppgave(2, Personident("12345678910"), 58),
            Oppgave(3, Personident("01987654321"), 17),
        )
    )

    routing {
        api(oppgaver)
        actuator()
    }
}

fun Routing.api(oppgaver: Oppgaver) {
    authenticate {
        route("/api") {
            get("/oppgaver") {
                call.respond(oppgaver)
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
