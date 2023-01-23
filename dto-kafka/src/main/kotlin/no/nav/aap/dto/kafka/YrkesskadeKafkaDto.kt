package no.nav.aap.dto.kafka

import java.time.LocalDate

data class YrkesskadeKafkaDto(
    val response: Response?,
) {
    data class Response(
        val yrkesskade: List<Yrkesskade>,
    ) {
        data class Yrkesskade(
            val datoForSkade: LocalDate
        )
    }
}
