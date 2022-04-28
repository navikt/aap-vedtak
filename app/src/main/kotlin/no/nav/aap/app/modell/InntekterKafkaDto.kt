package no.nav.aap.app.modell

import no.nav.aap.dto.DtoInntekt
import no.nav.aap.dto.DtoInntekter
import java.time.YearMonth

data class InntekterKafkaDto(
    val personident: String,
    val request: Request,
    val response: Response?,
) {
    data class Request(
        val fom: YearMonth,
        val tom: YearMonth,
    )

    data class Response(
        val inntekter: List<Inntekt>,
    ) {
        data class Inntekt(
            val arbeidsgiver: String,
            val inntekstmåned: YearMonth,
            val beløp: Double
        ) {
            fun toDto(): DtoInntekt = DtoInntekt(
                arbeidsgiver = arbeidsgiver,
                inntekstmåned = inntekstmåned,
                beløp = beløp
            )
        }
    }

    fun toDto(): DtoInntekter {
        requireNotNull(response) { "kan ikke kalle toDto uten response" }
        return DtoInntekter(response.inntekter.map(Response.Inntekt::toDto))
    }
}
