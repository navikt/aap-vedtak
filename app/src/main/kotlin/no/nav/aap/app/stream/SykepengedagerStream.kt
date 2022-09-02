package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.domene.Søker
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.sykepengedagerStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov = consume(Topics.sykepengedager)
        .filterNotNullBy("filter-sykepengedager-response") { kafkaDto -> kafkaDto.response }
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
    sykepengedagerKafkaDto: SykepengedagerKafkaDto,
    søkereKafkaDto: SøkereKafkaDto
): Pair<SøkereKafkaDto, List<DtoBehov>> {
    val søker = Søker.gjenopprett(søkereKafkaDto.toModellApi())

    val response = requireNotNull(sykepengedagerKafkaDto.response) { "response==null skal være filtrert vekk her." }
    val dtoBehov = response.håndter(søker)

    return søker.toDto().toJson() to dtoBehov.map { it.toDto(søkereKafkaDto.personident) }
}
