package no.nav.aap.dto.kafka

import java.time.LocalDate

data class AndreFolketrygdytelserKafkaDto(
    val response: Response?
) {
    data class Response(
        val svangerskapspenger: Svangerskapspenger
    ) {
        data class Svangerskapspenger(
            val fom: LocalDate?,
            val tom: LocalDate?,
            val grad: Double?,
            val vedtaksdato: LocalDate?
        )
    }
}