package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.util.*

data class MedlemKafkaDto(
    val personident: String,
    val id: UUID,
    val request: Request?,
    val response: Response?
) {
    data class Request(
        val mottattDato: LocalDate,
        val ytelse: String = "AAP",
        val arbeidetUtenlands: Boolean,
    )

    data class Response(
        val erMedlem: ErMedlem,
        val begrunnelse: String?
    )

    enum class ErMedlem {
        JA,
        NEI,
        UAVKLART
    }
}
