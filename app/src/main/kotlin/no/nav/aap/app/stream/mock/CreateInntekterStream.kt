package no.nav.aap.app.stream.mock

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.logConsumed
import no.nav.aap.app.kafka.to
import no.nav.aap.avro.inntekter.v1.Response
import org.apache.kafka.streams.StreamsBuilder
import no.nav.aap.avro.inntekter.v1.Inntekt as AvroInntekt
import no.nav.aap.avro.inntekter.v1.Inntekter as AvroInntekter

internal fun StreamsBuilder.inntekterResponseStream(topics: Topics) {
    stream(topics.inntekter.name, topics.inntekter.consumed("inntekter-behov-mottatt"))
        .logConsumed()
        .filter { _, inntekter -> inntekter.response == null }
        .mapValues(::addInntekterResponse)
        .filter { _, inntekter -> inntekter.personident != "11111111111" } // test person
        .to(topics.inntekter, topics.inntekter.produced("produced--inntekter"))
}

private fun addInntekterResponse(inntekter: AvroInntekter): AvroInntekter =
    inntekter.apply {
        response = Response.newBuilder()
            .setInntekter(
                listOf(
                    AvroInntekt("321", request.fom.plusYears(2), 400000.0),
                    AvroInntekt("321", request.fom.plusYears(1), 400000.0),
                    AvroInntekt("321", request.fom, 400000.0)
                )
            )
            .build()
    }
