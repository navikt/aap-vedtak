package no.nav.aap.app

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    println("Hello world")
    server()
}


private fun server(){
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/healthy") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}
