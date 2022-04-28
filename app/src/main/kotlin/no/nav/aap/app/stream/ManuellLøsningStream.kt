package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.sendBehov
import no.nav.aap.app.modell.ManuellKafkaDto
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoManuell
import no.nav.aap.dto.DtoSøker
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.manuellStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søkerOgBehov =
        consume(topics.manuell)
            .filterNotNull { "filter-manuell-tombstones" }
            .join(topics.manuell with topics.søkere, søkere, LøsningAndSøker::create)
            .mapValues(::håndterManuellLøsning)

    søkerOgBehov
        .mapValues(named("manuell-hent-ut-soker")) { (søker) -> søker }
        .produce(topics.søkere) { "produced-soker-med-handtert-losning" }

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
        fun create(løsning: ManuellKafkaDto, søker: AvroSøker): LøsningAndSøker =
            LøsningAndSøker(løsning.toDto(), søker.toDto())
    }
}
