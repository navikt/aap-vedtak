package no.nav.aap.app

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoInntekter
import no.nav.aap.dto.DtoSøker
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.sokere.v1.Inntekter as AvroInntekter
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

fun StreamsBuilder.inntekterStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    stream(topics.inntekter.name, topics.inntekter.consumed("inntekter-mottatt"))
        .peek { k: String, v -> log.info("consumed [aap.losning.v1] [$k] [$v]") }
//        .filter({ _, medlem -> medlem.response != null }, named("filter-responses"))
//        .selectKey({ _, medlem -> medlem.personident }, named("keyed_personident"))
        .join(søkere, InntekterAndSøker::create, topics.inntekter.joined(topics.søkere))
//        .filter(::idempotentMedlemLøsning, named("filter-idempotent-medlem-losning"))
        .mapValues(::håndterManuellLøsning)
        .peek { k: String, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to(topics.søkere.name, topics.søkere.produced("produced-soker-med-handtert-inntekter"))
}

/**
 * Returnerer true dersom medlem ikke er idempotent
 */
private fun idempotentMedlemLøsning(key: String, løsningAndSøker: InntekterAndSøker): Boolean {
    return løsningAndSøker.dtoSøker.saker
        .mapNotNull { sak -> sak.vilkårsvurderinger.firstOrNull { it.løsning_11_2_maskinell != null } }
        .singleOrNull() == null
}

private fun håndterManuellLøsning(inntekterAndSøker: InntekterAndSøker): AvroSøker {
    val søker = Søker.create(inntekterAndSøker.dtoSøker)

    inntekterAndSøker.inntekter.håndter(søker)

    return søker.toDto().toAvro()
}

private data class InntekterAndSøker(val inntekter: DtoInntekter, val dtoSøker: DtoSøker) {
    companion object {
        fun create(løsning: AvroInntekter, søker: AvroSøker): InntekterAndSøker =
            InntekterAndSøker(løsning.toDto(), søker.toDto())
    }
}
