package no.nav.aap.app

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.consumed
import no.nav.aap.app.kafka.joined
import no.nav.aap.app.kafka.produced
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVurderingAvBeregningsdato
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker
import no.nav.aap.avro.vedtak.v1.VurderingAvBeregningsdato as AvroVurderingAvBeregningsdato

fun StreamsBuilder.vurderingAvBeregningsdatoStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    stream(topics.vurderingAvBeregningsdato.name, topics.vurderingAvBeregningsdato.consumed("vurdering-av-beregningsdato-mottatt"))
        .peek { k: String, v -> log.info("consumed [aap.losning.v1] [$k] [$v]") }
//        .filter({ _, medlem -> medlem.response != null }, named("filter-responses"))
//        .selectKey({ _, medlem -> medlem.personident }, named("keyed_personident"))
        .join(søkere, VurderingAvBeregningsdatoAndSøker::create, topics.vurderingAvBeregningsdato.joined(topics.søkere))
//        .filter(::idempotentMedlemLøsning, named("filter-idempotent-medlem-losning"))
        .mapValues(::håndterManuellLøsning)
        .peek { k: String, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to(topics.søkere.name, topics.søkere.produced("produced-soker-med-handtert-vurdering-av-beregningsdato"))
}

/**
 * Returnerer true dersom medlem ikke er idempotent
 */
private fun idempotentMedlemLøsning(key: String, løsningAndSøker: VurderingAvBeregningsdatoAndSøker): Boolean {
    return løsningAndSøker.dtoSøker.saker
        .mapNotNull { sak -> sak.vilkårsvurderinger.firstOrNull { it.løsning_11_2_maskinell != null } }
        .singleOrNull() == null
}

private fun håndterManuellLøsning(vurderingAvBeregningsdatoAndSøker: VurderingAvBeregningsdatoAndSøker): AvroSøker {
    val søker = Søker.create(vurderingAvBeregningsdatoAndSøker.dtoSøker)

    vurderingAvBeregningsdatoAndSøker.vurderingAvBeregningsdato.løsningVurderingAvBeregningsdato?.håndter(søker)

    return søker.toDto().toAvro()
}

private data class VurderingAvBeregningsdatoAndSøker(val vurderingAvBeregningsdato: DtoVurderingAvBeregningsdato, val dtoSøker: DtoSøker) {
    companion object {
        fun create(løsning: AvroVurderingAvBeregningsdato, søker: AvroSøker): VurderingAvBeregningsdatoAndSøker =
            VurderingAvBeregningsdatoAndSøker(løsning.toDto(), søker.toDto())
    }
}
