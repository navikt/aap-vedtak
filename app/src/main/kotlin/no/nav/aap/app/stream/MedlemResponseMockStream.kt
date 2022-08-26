package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.dto.kafka.MedlemKafkaDto
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream

internal fun StreamsBuilder.medlemResponseMockStream() {
    consumeMedlemAndAddMockResponse().produce(Topics.medlem, "produce-medlem-mock-response")
}

private fun StreamsBuilder.consumeMedlemAndAddMockResponse(): KStream<String, MedlemKafkaDto> = this
    .stream(Topics.medlem.name, consumedMedlem())
    .filter { ident, medlem -> medlem?.response == null && ident != "123" } // 123 brukes i testene som mocker selv
    .mapValues { medlem ->
        medlem!!.copy(response = MedlemKafkaDto.Response(erMedlem = MedlemKafkaDto.ErMedlem.JA, null))
    }

private fun consumedMedlem(): Consumed<String, MedlemKafkaDto?> = Consumed
    .with(Topics.medlem.keySerde, Topics.medlem.valueSerde)
    .withName("consumed-medlem-again")
