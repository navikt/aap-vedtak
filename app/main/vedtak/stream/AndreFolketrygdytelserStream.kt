package vedtak.stream

import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.app.kafka.toSøkereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.AndreFolketrygdytelserKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import vedtak.kafka.Topics
import vedtak.kafka.buffer

internal fun Topology.andreFolketrygdytelserStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    consume(Topics.andreFolketrygdsytelser)
        .filter { value -> value.response != null }
        .joinWith(søkere, søkere.buffer)
        .map(håndterAndreFolketrygdytelser)
        .produce(Topics.søkere, søkere.buffer) { it }
}

private val håndterAndreFolketrygdytelser =
    { andreFolketrygdytelser: AndreFolketrygdytelserKafkaDto, (søkereKafkaDto, _, gammeltSekvensnummer): SøkereKafkaDtoHistorikk ->
        val søker = søkereKafkaDto.toModellApi()
        val (endretSøker) = andreFolketrygdytelser.toModellApi().håndter(søker)
        endretSøker.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer)
    }