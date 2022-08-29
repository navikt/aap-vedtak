package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSykepengedager
import no.nav.aap.dto.kafka.LøsningSykepengedagerKafkaDto

fun LøsningSykepengedagerKafkaDto.håndter(søker: Søker) = toDto().håndter(søker)

fun LøsningSykepengedagerKafkaDto.toDto() = DtoSykepengedager(
    gjenståendeSykedager = gjenståendeSykedager,
    foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
    kilde = kilde
)