package no.nav.aap.app.modell

import no.nav.aap.avro.inntekter.v1.Inntekt
import no.nav.aap.avro.inntekter.v1.Inntekter
import no.nav.aap.dto.DtoInntekt
import no.nav.aap.dto.DtoInntekter
import java.time.YearMonth

fun Inntekter.toDto(): DtoInntekter = DtoInntekter(
    inntekter = response.inntekter.map { it.toDto() }
)

fun Inntekt.toDto(): DtoInntekt = DtoInntekt(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = YearMonth.from(inntektsmaned),
    beløp = belop
)
