package no.nav.aap.app

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.consumed
import no.nav.aap.app.kafka.joined
import no.nav.aap.app.kafka.produced
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker
import no.nav.aap.avro.vedtak.v1.Vilkarsvurdering as AvroVilkårsvurdering

fun StreamsBuilder.løsningStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    stream(topics.løsning.name, topics.løsning.consumed("losning-mottatt"))
        .peek { k: String, v -> log.info("consumed [aap.losning.v1] [$k] [$v]") }
//        .filter({ _, medlem -> medlem.response != null }, named("filter-responses"))
//        .selectKey({ _, medlem -> medlem.personident }, named("keyed_personident"))
        .join(søkere, LøsningAndSøker::create, topics.løsning.joined(topics.søkere))
//        .filter(::idempotentMedlemLøsning, named("filter-idempotent-medlem-losning"))
        .mapValues(::håndterManuellLøsning)
        .peek { k: String, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to(topics.søkere.name, topics.søkere.produced("produced-soker-med-handtert-losning"))
}

/**
 * Returnerer true dersom medlem ikke er idempotent
 */
private fun idempotentMedlemLøsning(key: String, løsningAndSøker: LøsningAndSøker): Boolean {
    return løsningAndSøker.dtoSøker.saker
        .mapNotNull { sak -> sak.vilkårsvurderinger.firstOrNull { it.løsning_11_2_maskinell != null } }
        .singleOrNull() == null
}

private fun håndterManuellLøsning(løsningAndSøker: LøsningAndSøker): AvroSøker {
    val søker = Søker.create(løsningAndSøker.dtoSøker)

    løsningAndSøker.løsning.løsning_11_2_manuell?.håndter(søker)
    løsningAndSøker.løsning.løsning_11_3_manuell?.håndter(søker)
    løsningAndSøker.løsning.løsning_11_4_ledd2_ledd3_manuell?.håndter(søker)
    løsningAndSøker.løsning.løsning_11_5_manuell?.håndter(søker)
    løsningAndSøker.løsning.løsning_11_6_manuell?.håndter(søker)
    løsningAndSøker.løsning.løsning_11_12_ledd1_manuell?.håndter(søker)
    løsningAndSøker.løsning.løsning_11_29_manuell?.håndter(søker)

    return søker.toDto().toAvro()
}

private data class LøsningAndSøker(val løsning: DtoVilkårsvurdering, val dtoSøker: DtoSøker) {
    companion object {
        fun create(løsning: AvroVilkårsvurdering, søker: AvroSøker): LøsningAndSøker =
            LøsningAndSøker(løsning.toDto(), søker.toDto())
    }
}
