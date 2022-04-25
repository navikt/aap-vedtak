package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSøker
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.medlemStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    consume(topics.medlem)
        .filterNotNull { "filter-medlem-tombstones" }
        .filter { _, value -> value.response != null }
        .selectKey("keyed_personident") { _, value -> value.personident }
        .join(topics.medlem with topics.søkere, søkere, MedlemAndSøker::create)
        .mapValues(::medlemLøsning)
        .produce(topics.søkere) { "produced-soker-med-medlem" }
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
