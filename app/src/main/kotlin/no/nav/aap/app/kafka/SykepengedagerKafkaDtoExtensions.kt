package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.modellapi.SykepengedagerModellApi
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto

internal fun SykepengedagerKafkaDto.Response.håndter(søker: Søker) = toModellApi().håndter(søker)

private fun SykepengedagerKafkaDto.Response.toModellApi() = SykepengedagerModellApi(
    gjenståendeSykedager = gjenståendeSykedager,
    foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
    kilde = kilde.name
)
