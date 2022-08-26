package no.nav.aap.app.kafka

import no.nav.aap.dto.DtoInntekt
import no.nav.aap.dto.DtoInntekter
import no.nav.aap.dto.kafka.InntekterKafkaDto

internal fun InntekterKafkaDto.Response.Inntekt.toDto(): DtoInntekt =
    DtoInntekt(
        arbeidsgiver = arbeidsgiver,
        inntekstmåned = inntekstmåned,
        beløp = beløp
    )

internal fun InntekterKafkaDto.toDto(): DtoInntekter {
    requireNotNull(response) { "kan ikke kalle toDto uten response" }
    // TODO: smart-cast funker ikke fordi dette er en annen modul?
    return DtoInntekter(
        inntekter = response!!.inntekter.map(InntekterKafkaDto.Response.Inntekt::toDto)
    )
}
