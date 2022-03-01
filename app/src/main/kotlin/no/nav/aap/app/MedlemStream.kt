package no.nav.aap.app

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSøker
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.medlemStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    stream(topics.medlem.name, topics.medlem.consumed("medlem-mottatt"))
        .logConsumed()
        .filter({ _, medlem -> medlem.response != null }, named("filter-responses"))
        .selectKey({ _, medlem -> medlem.personident }, named("keyed_personident"))
        .join(søkere, MedlemAndSøker::create, topics.medlem.joined(topics.søkere))
        .mapValues(::medlemLøsning)
        .to(topics.søkere, topics.søkere.produced("produced-soker-med-medlem"))
}

private fun medlemLøsning(medlemAndSøker: MedlemAndSøker): AvroSøker {
    val søker = Søker.gjenopprett(medlemAndSøker.dtoSøker).apply {
        val medlem = medlemAndSøker.avroMedlem.toDto()
        håndterLøsning(medlem)
    }

    return søker.toDto().toAvro()
}

private data class MedlemAndSøker(val avroMedlem: AvroMedlem, val dtoSøker: DtoSøker) {
    companion object {
        fun create(medlem: AvroMedlem, søker: AvroSøker): MedlemAndSøker = MedlemAndSøker(medlem, søker.toDto())
    }
}
