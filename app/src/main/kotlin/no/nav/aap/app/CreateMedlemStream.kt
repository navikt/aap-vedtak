package no.nav.aap.app

import no.nav.aap.app.kafka.Topics
import no.nav.aap.avro.medlem.v1.ErMedlem
import no.nav.aap.avro.medlem.v1.Response
import org.apache.kafka.streams.StreamsBuilder
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem

internal fun StreamsBuilder.medlemResponseStream(topics: Topics) {
    stream(topics.medlem.name, topics.medlem.consumed("medlem-behov-mottatt"))
        .peek { k, v -> log.info("consumed [aap.medlem.v1] [$k] [$v]") }
        .filter { _, medlem -> medlem.response == null }
        .mapValues(::addMedlemResponse)
        .filter { _, medlem -> medlem.personident != "11111111111" } // test person
        .peek { k, v -> log.info("produced [aap.medlem.v1] [$k] [$v]") }
        .to(topics.medlem.name, topics.medlem.produced("produced--medlem"))
}

private fun addMedlemResponse(medlem: AvroMedlem): AvroMedlem =
    medlem.apply {
        response = Response.newBuilder()
            .setErMedlem(ErMedlem.JA)
            .setBegrunnelse("flotters")
            .build()
    }
