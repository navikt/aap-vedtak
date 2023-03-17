package vedtak

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.kafka.streams.v2.Streams

internal fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: Streams) {
    route("/actuator") {

        get("/metrics") {
            call.respondText(prometheus.scrape())
        }

        get("/live") {
            val status = if (kafka.live()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("vedtak", status = status)
        }

        get("/ready") {
            val status = if (kafka.ready()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("vedtak", status = status)
        }
    }
}
