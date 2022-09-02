package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.SykepengedagerModellApi
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto

internal fun SykepengedagerKafkaDto.Response.håndter(søker: Søker) = toDto().håndter(søker)

private fun SykepengedagerKafkaDto.Response.toDto() = SykepengedagerModellApi(
    gjenståendeSykedager = gjenståendeSykedager,
    foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
    kilde = kilde.name
)
