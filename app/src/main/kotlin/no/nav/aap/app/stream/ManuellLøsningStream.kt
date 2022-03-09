package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoManuell
import no.nav.aap.dto.DtoSøker
import no.nav.aap.hendelse.DtoBehov
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.manuell.v1.Manuell as AvroManuell
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.manuellStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søkerOgBehov = stream(topics.manuell.name, topics.manuell.consumed("losning-mottatt"))
        .logConsumed()
        .join(søkere, LøsningAndSøker::create, topics.manuell.joined(topics.søkere))
        .mapValues(::håndterManuellLøsning)

    søkerOgBehov
        .mapValues(named("manuell-hent-ut-soker")) { (søker) -> søker }
        .to(topics.søkere, topics.søkere.produced("produced-soker-med-handtert-losning"))

    søkerOgBehov
        .flatMapValues(named("manuell-hent-ut-behov")) { (_, dtoBehov) -> dtoBehov }
        .sendBehov("manuell", topics)
}

private fun håndterManuellLøsning(løsningAndSøker: LøsningAndSøker): Pair<AvroSøker, List<DtoBehov>> {
    val søker = Søker.gjenopprett(løsningAndSøker.dtoSøker)

    val dtoBehov = mutableListOf<DtoBehov>()

    løsningAndSøker.løsning.løsning_11_2_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsning_11_3_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsning_11_4_ledd2_ledd3_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsning_11_5_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsning_11_6_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsning_11_12_ledd1_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsning_11_29_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }
    løsningAndSøker.løsning.løsningVurderingAvBeregningsdato?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { it.toDto(løsningAndSøker.dtoSøker.personident) }) }

    return søker.toDto().toAvro() to dtoBehov
}

private data class LøsningAndSøker(val løsning: DtoManuell, val dtoSøker: DtoSøker) {
    companion object {
        fun create(løsning: AvroManuell, søker: AvroSøker): LøsningAndSøker =
            LøsningAndSøker(løsning.toDto(), søker.toDto())
    }
}
