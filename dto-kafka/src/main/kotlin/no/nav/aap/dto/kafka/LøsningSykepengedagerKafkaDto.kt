package no.nav.aap.dto.kafka

import java.time.LocalDate

data class LøsningSykepengedagerKafkaDto(
    val gjenståendeSykedager: Int,
    val foreløpigBeregnetSluttPåSykepenger: LocalDate,
    val kilde: String,
)