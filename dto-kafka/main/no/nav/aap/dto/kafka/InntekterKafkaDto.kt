package no.nav.aap.dto.kafka

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
        )
    }
}
