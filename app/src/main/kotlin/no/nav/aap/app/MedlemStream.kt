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
        .peek { k: String, v -> log.info("consumed [aap.medlem.v1] [$k] [$v]") }
        .filter({ _, medlem -> medlem.response != null }, named("filter-responses"))
        .selectKey({ _, medlem -> medlem.personident }, named("keyed_personident"))
        .join(søkere, MedlemAndSøker::create, topics.medlem.joined(topics.søkere))
        .filter(::idempotentMedlemLøsning, named("filter-idempotent-medlem-losning"))
        .mapValues(::medlemLøsning)
        .peek { k: String, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to(topics.søkere.name, topics.søkere.produced("produced-soker-med-medlem"))
}

/**
 * Returnerer true dersom medlem ikke er idempotent
 */
private fun idempotentMedlemLøsning(key: String, medlemAndSøker: MedlemAndSøker): Boolean {
    return medlemAndSøker.dtoSøker.saker
        .mapNotNull { sak -> sak.vilkårsvurderinger.firstOrNull { it.løsning_11_2_maskinell != null } }
        .singleOrNull() == null
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
