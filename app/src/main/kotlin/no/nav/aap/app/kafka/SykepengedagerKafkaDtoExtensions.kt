package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSykepengedager
import no.nav.aap.dto.kafka.SykepengedakerKafkaDto

internal fun SykepengedakerKafkaDto.Response.håndter(søker: Søker) = toDto().håndter(søker)

private fun SykepengedakerKafkaDto.Response.toDto() = DtoSykepengedager(
    gjenståendeSykedager = gjenståendeSykedager,
    foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
    kilde = kilde
)
