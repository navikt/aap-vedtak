package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.IverksettelseAvVedtakKafkaDto
import no.nav.aap.modellapi.IverksettelseAvVedtakModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun IverksettelseAvVedtakKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun IverksettelseAvVedtakKafkaDto.toModellApi() = IverksettelseAvVedtakModellApi(iverksattAv)
