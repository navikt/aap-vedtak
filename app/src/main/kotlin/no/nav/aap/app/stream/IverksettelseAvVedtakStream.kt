package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.dto.kafka.IverksettelseAvVedtakKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.kafka.streams.extension.*
import no.nav.aap.modellapi.BehovModellApi
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.iverksettelseAvVedtakStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov = consume(Topics.iverksettelseAvVedtak)
        .filterNotNull("filter-iverksettelse-av-vedtak-tombstone")
        .join(Topics.iverksettelseAvVedtak with Topics.søkere, søkere) { løsning, søker ->
            håndter(løsning, søker)
        }

    søkerOgBehov
        .firstPairValue("iverksettelse-av-vedtak-hent-ut-soker")
        .produce(Topics.søkere, "produced-soker-med-iverksettelse-av-vedtak")

    søkerOgBehov
        .secondPairValue("iverksettelse-av-vedtak-hent-ut-behov")
        .flatten("iverksettelse-av-vedtak-flatten-behov")
        .sendBehov("iverksettelse-av-vedtak")
}

private fun håndter(
    iverksettelseAvVedtakKafkaDto: IverksettelseAvVedtakKafkaDto,
    søkereKafkaDto: SøkereKafkaDto
): Pair<SøkereKafkaDto, List<BehovModellApi>> {
    val søker = søkereKafkaDto.toModellApi()
    val (endretSøker, dtoBehov) = iverksettelseAvVedtakKafkaDto.håndter(søker)
    return endretSøker.toJson() to dtoBehov
}
