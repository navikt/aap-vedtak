package no.nav.aap.app

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker

fun StreamsBuilder.medlemStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    stream(topics.medlem.name, topics.medlem.consumed("medlem-mottatt"))
        .peek { k, v -> log.info("consumed [aap.medlem.v1] [$k] [$v]") }
        .filter({ _, medlem -> medlem.response != null }, named("filter-responses"))
        .selectKey({ _, medlem -> medlem.personident }, named("keyed_personident"))
        .join(søkere, ::medlemLøsning, topics.medlem.joined(topics.søkere))
        .peek { k, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to(topics.søkere.name, topics.søkere.produced("produced-soker-med-medlem"))
}

private fun medlemLøsning(avroMedlem: AvroMedlem, avroSøker: AvroSøker): AvroSøker {
    val dtoSøker = avroSøker.toDto()
    val søker = no.nav.aap.domene.Søker.create(dtoSøker).apply {
        val medlem = avroMedlem.toDto()
        håndterLøsning(medlem)
    }

    return søker.toDto().toAvro()
}
