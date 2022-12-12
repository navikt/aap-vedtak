package no.nav.aap.app.stream

import io.micrometer.core.instrument.MeterRegistry
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toForrigeDto
import no.nav.aap.app.kafka.toJson
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.dto.kafka.AndreFolketrygdytelserKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.filterNotNullBy
import no.nav.aap.kafka.streams.extension.join
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.andreFolketrygdytelserStream(søkere: KTable<String, SøkereKafkaDtoHistorikk>, registry: MeterRegistry) {
    consume(Topics.andreFolketrygdsytelser)
        .filterNotNullBy("andre-folketrygdytelser-filter-tombstones-og-responses") { ytelser -> ytelser.response }
        .peek{_, value -> registry.counter("mottar_andre_ytelser").increment()}
        .join(Topics.andreFolketrygdsytelser with Topics.søkere, søkere, håndterAndreFolketrygdytelser)
        .produce(Topics.søkere, "produced-soker-med-handtert-andre-folketrygdytelser")
}

private val håndterAndreFolketrygdytelser =
    { andreFolketrygdytelser: AndreFolketrygdytelserKafkaDto, (søkereKafkaDto): SøkereKafkaDtoHistorikk ->
        val søker = søkereKafkaDto.toModellApi()
        val (endretSøker) = andreFolketrygdytelser.toModellApi().håndter(søker)
        val endretSøkereKafkaDto = endretSøker.toJson(søkereKafkaDto.sekvensnummer)
        val forrigeSøkereKafkaDto = endretSøkereKafkaDto.toForrigeDto()
        SøkereKafkaDtoHistorikk(endretSøkereKafkaDto, forrigeSøkereKafkaDto)
    }

internal fun StreamsBuilder.andreFolketrygdsytelserResponseMockStream() = this
    .stream(Topics.andreFolketrygdsytelser.name, consumedAndreFolketrygdytelser())
    .filter { ident, ytelser -> ytelser?.response == null && ident != "123" } // 123 brukes i testene som mocker selv
    .mapValues { ytelser ->
        ytelser!!.copy(
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
    .produce(Topics.andreFolketrygdsytelser, "produce-andre-folketrygdytelser-mock-response")

private fun consumedAndreFolketrygdytelser(): Consumed<String, AndreFolketrygdytelserKafkaDto?> = Consumed
    .with(Topics.andreFolketrygdsytelser.keySerde, Topics.andreFolketrygdsytelser.valueSerde)
    .withName("consumed-andre-folketrygdytelser-again")

