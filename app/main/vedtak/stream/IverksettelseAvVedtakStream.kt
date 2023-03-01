package vedtak.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.dto.kafka.IverksettelseAvVedtakKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.modellapi.BehovModellApi
import vedtak.kafka.*

internal fun Topology.iverksettelseAvVedtakStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    val søkerOgBehov = consume(Topics.iverksettelseAvVedtak)
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
    iverksettelseAvVedtakKafkaDto: IverksettelseAvVedtakKafkaDto,
    søkereKafkaDtoHistorikk: SøkereKafkaDtoHistorikk
): Pair<SøkereKafkaDtoHistorikk, List<BehovModellApi>> {
    val (søkereKafkaDto, _, gammeltSekvensnummer) = søkereKafkaDtoHistorikk
    val søker = søkereKafkaDto.toModellApi()
    val (endretSøker, dtoBehov) = iverksettelseAvVedtakKafkaDto.håndter(søker)
    return endretSøker.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer) to dtoBehov
}
