package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.app.kafka.toDto
import no.nav.aap.app.kafka.toJson
import no.nav.aap.domene.Søker
import no.nav.aap.dto.kafka.LøsningSykepengedagerKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.sykepengedagerStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov = consume(Topics.sykepengedager)
        .filterNotNull("filter-sykepengedager-tombstone")
        .join(Topics.sykepengedager with Topics.søkere, søkere) { løsning, søker ->
            håndter(løsning, søker)
        }

    søkerOgBehov
        .firstPairValue("sykepengedager-hent-ut-soker")
        .produce(Topics.søkere, "produced-soker-med-sykepengedager")

    søkerOgBehov
        .secondPairValue("sykepengedager-hent-ut-behov")
        .flatten("sykepengedager-flatten-behov")
        .sendBehov("sykepengedager")
}

private fun håndter(
    sykepengedagerKafkaDto: LøsningSykepengedagerKafkaDto,
    søkereKafkaDto: SøkereKafkaDto
): Pair<SøkereKafkaDto, List<DtoBehov>> {
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto())

    val dtoBehov = sykepengedagerKafkaDto.håndter(søker)

    return søker.toDto().toJson() to dtoBehov.map { it.toDto(søkereKafkaDto.personident) }
}