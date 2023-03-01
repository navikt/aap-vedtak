package vedtak.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.modellapi.BehovModellApi
import vedtak.kafka.BehovModellApiWrapper
import vedtak.kafka.Topics
import vedtak.kafka.buffer
import vedtak.kafka.sendBehov

internal fun Topology.sykepengedagerStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    val søkerOgBehov = consume(Topics.sykepengedager)
        .filter { kafkaDto -> kafkaDto.response != null }
        .joinWith(søkere, søkere.buffer)
        .map(::håndter)

    søkerOgBehov
        .map(Pair<SøkereKafkaDtoHistorikk, List<BehovModellApi>>::first)
        .produce(Topics.søkere, søkere.buffer) { it }

    søkerOgBehov
        .flatMap { (_, behov) -> behov.map(::BehovModellApiWrapper) }
        .sendBehov()
}

private fun håndter(
    sykepengedagerKafkaDto: SykepengedagerKafkaDto,
    søkereKafkaDtoHistorikk: SøkereKafkaDtoHistorikk
): Pair<SøkereKafkaDtoHistorikk, List<BehovModellApi>> {
    val (søkereKafkaDto, _, gammeltSekvensnummer) = søkereKafkaDtoHistorikk
    val søker = søkereKafkaDto.toModellApi()
    val response = requireNotNull(sykepengedagerKafkaDto.response) { "response==null skal være filtrert vekk her." }
    val (endretSøker, dtoBehov) = response.håndter(søker)
    return endretSøker.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer) to dtoBehov
}
