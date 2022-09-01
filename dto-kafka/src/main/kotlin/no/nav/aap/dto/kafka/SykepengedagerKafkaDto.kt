package no.nav.aap.dto.kafka

import java.time.LocalDate

// TODO: flytt til aap-sykepengedager
data class SykepengedagerKafkaDto(
    val response: Response?,
    val version: Int = 1,
) {
    data class Response(
        val gjenståendeSykedager: Int,
        val foreløpigBeregnetSluttPåSykepenger: LocalDate,
        val kilde: String,
    )
}
