package no.nav.aap.app.modell

import no.nav.aap.dto.DtoLøsningMaskinellParagraf_11_2
import java.time.LocalDate
import java.util.*

data class MedlemKafkaDto(
    val personident: String,
    val id: UUID,
    val request: Request?,
    val response: Response?
) {
    fun toDto(): DtoLøsningMaskinellParagraf_11_2 = DtoLøsningMaskinellParagraf_11_2(
        erMedlem = response?.erMedlem?.name ?: error("response fra medlemsskap mangler.")
    )

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
