package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.sendBehov
import no.nav.aap.app.modell.ManuellKafkaDto
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.manuellStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov =
        consume(Topics.manuell)
            .filterNotNull("filter-manuell-tombstones")
            .join(Topics.manuell with Topics.søkere, søkere, ::Pair)
            .mapValues("manuell-handter-losning", ::håndterManuellLøsning)

    søkerOgBehov
        .mapValues("manuell-hent-ut-soker") { (søker) -> søker }
        .produce(Topics.søkere, "produced-soker-med-handtert-losning")

    søkerOgBehov
        .flatMapValues("manuell-hent-ut-behov") { (_, dtoBehov) -> dtoBehov }
        .sendBehov("manuell")
}

private fun håndterManuellLøsning(løsningAndSøker: Pair<ManuellKafkaDto, SøkereKafkaDto>): Pair<SøkereKafkaDto, List<DtoBehov>> {
    val (manuellKafkaDto, søkereKafkaDto) = løsningAndSøker
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto())
    val løsning = manuellKafkaDto.toDto()

    val dtoBehov = mutableListOf<DtoBehov>()

    løsning.løsning_11_2_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsning_11_3_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsning_11_4_ledd2_ledd3_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsning_11_5_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsning_11_6_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsning_11_12_ledd1_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsning_11_29_manuell?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }
    løsning.løsningVurderingAvBeregningsdato?.håndter(søker)
        ?.also { dtoBehov.addAll(it.map { behov -> behov.toDto(søkereKafkaDto.personident) }) }

    return søker.toDto().toJson() to dtoBehov
}
