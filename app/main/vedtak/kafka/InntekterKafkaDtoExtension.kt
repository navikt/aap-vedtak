package vedtak.kafka

import no.nav.aap.dto.kafka.InntekterKafkaDto
import no.nav.aap.modellapi.InntektModellApi
import no.nav.aap.modellapi.InntekterModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun InntekterKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun InntekterKafkaDto.Response.Inntekt.toModellApi(): InntektModellApi =
    InntektModellApi(
        arbeidsgiver = arbeidsgiver,
        inntekstmåned = inntekstmåned,
        beløp = beløp
    )

private fun InntekterKafkaDto.toModellApi(): InntekterModellApi {
    val res = requireNotNull(response) { "kan ikke kalle toDto uten response" }
    return InntekterModellApi(
        inntekter = res.inntekter.map(InntekterKafkaDto.Response.Inntekt::toModellApi)
    )
}
