package vedtak.stream

import vedtak.kafka.Topics
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.app.kafka.toSøkereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.MedlemKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import vedtak.kafka.buffer

internal fun Topology.medlemStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    consume(Topics.medlem)
        .filter { medlem -> medlem.response != null }
        .rekey { medlem -> medlem.personident }
        .joinWith(søkere, søkere.buffer)
        .map(håndterMedlem)
        .produce(Topics.søkere, søkere.buffer) { it }
}

private val håndterMedlem =
    { medlemKafkaDto: MedlemKafkaDto, (søkereKafkaDto, _, gammeltSekvensnummer): SøkereKafkaDtoHistorikk ->
        val søker = søkereKafkaDto.toModellApi()
        val (endretSøker) = medlemKafkaDto.toModellApi().håndter(søker)
        endretSøker.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer)
    }
