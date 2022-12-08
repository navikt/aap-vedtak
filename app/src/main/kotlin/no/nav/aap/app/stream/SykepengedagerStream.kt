package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.extension.*
import no.nav.aap.modellapi.BehovModellApi
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.sykepengedagerStream(søkere: KTable<String, SøkereKafkaDtoHistorikk>) {
    val søkerOgBehov = consume(Topics.sykepengedager)
        .filterNotNullBy("filter-sykepengedager-response") { kafkaDto -> kafkaDto.response }
        .join(Topics.sykepengedager with Topics.søkere, søkere, ::håndter)

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
    søkereKafkaDtoHistorikk: SøkereKafkaDtoHistorikk
): Pair<SøkereKafkaDtoHistorikk, List<BehovModellApi>> {
    val (søkereKafkaDto) = søkereKafkaDtoHistorikk
    val søker = søkereKafkaDto.toModellApi()
    val response = requireNotNull(sykepengedagerKafkaDto.response) { "response==null skal være filtrert vekk her." }
    val (endretSøker, dtoBehov) = response.håndter(søker)
    val endretSøkereKafkaDto = endretSøker.toJson(søkereKafkaDto.sekvensnummer)
    val forrigeSøkereKafkaDto = endretSøkereKafkaDto.toForrigeDto()
    return SøkereKafkaDtoHistorikk(endretSøkereKafkaDto, forrigeSøkereKafkaDto) to dtoBehov
}
