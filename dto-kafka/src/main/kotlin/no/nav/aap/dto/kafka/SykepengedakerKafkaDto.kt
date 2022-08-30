package no.nav.aap.dto.kafka

import java.time.LocalDate

data class SykepengedakerKafkaDto(val response: Response?) {
    data class Response(
        val gjenståendeSykedager: Int,
        val foreløpigBeregnetSluttPåSykepenger: LocalDate,
        val kilde: String,
    )
}
