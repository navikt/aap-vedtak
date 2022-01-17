package no.nav.aap.app

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.aap.app.modell.Aldersvurdering
import no.nav.aap.app.modell.Oppgave
import no.nav.aap.app.modell.Oppgaver
import no.nav.aap.app.modell.Personident

fun main() {
    println("Hello world")
    server()
}

private fun server() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        val oppgaver = Oppgaver(
            listOf(
                Oppgave(1, Personident("11111111111"), 68),
                Oppgave(2, Personident("12345678910"), 58),
                Oppgave(3, Personident("01987654321"), 17),
            )
        )

        routing {
            get("/healthy") {
                call.respondText("Hello, world!")
            }

            get("/api/oppgaver") {
                call.respond(oppgaver.oppgaver)
            }

            post("/api/vurderAlder") {
                val aldersvurdering = call.receive<Aldersvurdering>()
                when (aldersvurdering.erMellom18og67) {
                    true -> log.info("hen for oppgaveId ${aldersvurdering.oppgaveId} er mellom 18 og 67")
                    false -> log.info("hen for oppgaveId ${aldersvurdering.oppgaveId} er IKKE mellom 18 og 67")
                }
                call.respond(HttpStatusCode.Accepted)
            }
        }
    }.start(wait = true)
}
