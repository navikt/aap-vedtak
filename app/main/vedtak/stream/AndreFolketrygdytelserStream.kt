package vedtak.stream

import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.app.kafka.toSøkereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.AndreFolketrygdytelserKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.kafka.streams.v2.stream.ConsumedKStream
import vedtak.kafka.Topics
import vedtak.kafka.buffer

internal fun Topology.andreFolketrygdytelserStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    consume(Topics.andreFolketrygdsytelser)
        .branch({ ytelser -> ytelser.response != null }) { stream ->
            stream
                .joinWith(søkere, søkere.buffer)
                .map(håndterAndreFolketrygdytelser)
                .produce(Topics.søkere, søkere.buffer) { it }
        }
        .branch({ ytelser -> ytelser.response == null }, ::mockResponse)
}

private val håndterAndreFolketrygdytelser =
    { andreFolketrygdytelser: AndreFolketrygdytelserKafkaDto, (søkereKafkaDto, _, gammeltSekvensnummer): SøkereKafkaDtoHistorikk ->
        val søker = søkereKafkaDto.toModellApi()
        val (endretSøker) = andreFolketrygdytelser.toModellApi().håndter(søker)
        endretSøker.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer)
    }

private fun mockResponse(stream: ConsumedKStream<AndreFolketrygdytelserKafkaDto>) {
    stream
        .filterKey { ident -> ident != "123" } // 123 brukes i testene som mocker selv
        .filter { ytelser -> ytelser.response == null }
        .map { ytelser ->
            ytelser.copy(
                response = AndreFolketrygdytelserKafkaDto.Response(
                    svangerskapspenger = AndreFolketrygdytelserKafkaDto.Response.Svangerskapspenger(
                        fom = null,
                        tom = null,
                        grad = null,
                        vedtaksdato = null,
                    )
                )
            )
        }
        .produce(Topics.andreFolketrygdsytelser)
}
