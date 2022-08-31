package no.nav.aap.dto.kafka

import java.time.LocalDate

data class SykepengedagerKafkaDto(val response: Response?) {
    data class Response(
        val gjenståendeSykedager: Int,
        val foreløpigBeregnetSluttPåSykepenger: LocalDate,
        val kilde: String,
    )
}
