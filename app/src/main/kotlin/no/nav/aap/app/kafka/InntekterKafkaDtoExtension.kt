package no.nav.aap.app.kafka

import no.nav.aap.modellapi.InntektModellApi
import no.nav.aap.modellapi.InntekterModellApi
import no.nav.aap.dto.kafka.InntekterKafkaDto

internal fun InntekterKafkaDto.Response.Inntekt.toModellApi(): InntektModellApi =
    InntektModellApi(
        arbeidsgiver = arbeidsgiver,
        inntekstmåned = inntekstmåned,
        beløp = beløp
    )

internal fun InntekterKafkaDto.toModellApi(): InntekterModellApi {
    requireNotNull(response) { "kan ikke kalle toDto uten response" }
    // TODO: smart-cast funker ikke fordi dette er en annen modul?
    return InntekterModellApi(
        inntekter = response!!.inntekter.map(InntekterKafkaDto.Response.Inntekt::toModellApi)
    )
}
