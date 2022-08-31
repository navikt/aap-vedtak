package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSykepengedager
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto

internal fun SykepengedagerKafkaDto.Response.håndter(søker: Søker) = toDto().håndter(søker)

private fun SykepengedagerKafkaDto.Response.toDto() = DtoSykepengedager(
    gjenståendeSykedager = gjenståendeSykedager,
    foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
    kilde = kilde
)
