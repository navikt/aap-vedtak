package no.nav.aap.app.streams

import no.nav.aap.app.log
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Produced
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker

fun StreamsBuilder.medlemStream(søkere: KTable<String, AvroSøker>) {
    stream<String, no.nav.aap.avro.medlem.v1.Medlem>("aap.medlem.v1", Consumed.`as`("medlem-mottatt"))
        .peek { _, _ -> log.info("consumed aap.medlem.v1") }
        .filter { _, medlem -> medlem.response != null }
        .selectKey({ _, medlem -> medlem.personident }, Named.`as`("keyed_personident"))
        .join(søkere, ::medlemLøsning)
        .peek { _, _ -> log.info("produced aap.sokere.v1") }
        .to("aap.sokere.v1", Produced.`as`("produced-soker-med-medlem"))
}

private fun medlemLøsning(avroMedlem: AvroMedlem, avroSøker: AvroSøker): AvroSøker {
    val dtoSøker = avroSøker.toDto()
    val søker = no.nav.aap.domene.Søker.create(dtoSøker).apply {
        val medlem = avroMedlem.toDto()
        håndterLøsning(medlem)
    }

    return søker.toDto().toAvro()
}
