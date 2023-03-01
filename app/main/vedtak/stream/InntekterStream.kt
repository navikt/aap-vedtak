package vedtak.stream

import vedtak.kafka.Topics
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.app.kafka.toSøkereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.InntekterKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import vedtak.kafka.buffer

internal fun Topology.inntekterStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    consume(Topics.inntekter)
        .filter { inntekter -> inntekter.response != null }
        .joinWith(søkere, søkere.buffer)
        .map(håndterInntekter)
        .produce(Topics.søkere, søkere.buffer) { it }
}

private val håndterInntekter =
    { inntekterKafkaDto: InntekterKafkaDto, (søkereKafkaDto, _, gammeltSekvensnummer): SøkereKafkaDtoHistorikk ->
        val søker = søkereKafkaDto.toModellApi()
        val (endretSøker) = inntekterKafkaDto.toModellApi().håndter(søker)
        endretSøker.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer)
    }
