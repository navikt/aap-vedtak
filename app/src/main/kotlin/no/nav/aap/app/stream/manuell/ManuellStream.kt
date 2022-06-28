package no.nav.aap.app.stream.manuell

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.sendBehov
import no.nav.aap.app.modell.*
import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.manuellStream(søkere: KTable<String, SøkereKafkaDto>) {
    manuellStream(søkere, Topics.manuell_11_2, "manuell-11-2", Løsning_11_2_manuell::håndter)
    manuellStream(søkere, Topics.manuell_11_3, "manuell-11-3", Løsning_11_3_manuell::håndter)
    manuellStream(søkere, Topics.manuell_11_4, "manuell-11-4", Løsning_11_4_ledd2_ledd3_manuell::håndter)
    manuellStream(søkere, Topics.manuell_11_5, "manuell-11-5", Løsning_11_5_manuell::håndter)
    manuellStream(søkere, Topics.manuell_11_6, "manuell-11-6", Løsning_11_6_manuell::håndter)
    manuellStream(søkere, Topics.manuell_11_12, "manuell-11-12", Løsning_11_12_ledd1_manuell::håndter)
    manuellStream(søkere, Topics.manuell_11_29, "manuell-11-29", Løsning_11_29_manuell::håndter)
    manuellStream(
        søkere,
        Topics.manuell_beregningsdato,
        "manuell-beregningsdato",
        LøsningVurderingAvBeregningsdato::håndter
    )
}

private fun <T> StreamsBuilder.manuellStream(
    søkere: KTable<String, SøkereKafkaDto>,
    topic: Topic<T>,
    kafkaNameFilter: String,
    håndter: T.(Søker) -> List<Behov>,
) {
    val søkerOgBehov =
        consume(topic)
            .filterNotNull("filter-$kafkaNameFilter-tombstones")
            .join(topic with Topics.søkere, søkere, ::Pair)
            .mapValues("$kafkaNameFilter-handter-losning") { løsningAndSøker ->
                håndterManuellLøsning(løsningAndSøker, håndter)
            }

    søkerOgBehov
        .mapValues("$kafkaNameFilter-hent-ut-soker") { (søker) -> søker }
        .produce(Topics.søkere, "produced-soker-med-$kafkaNameFilter")

    søkerOgBehov
        .flatMapValues("$kafkaNameFilter-hent-ut-behov") { (_, dtoBehov) -> dtoBehov }
        .sendBehov(kafkaNameFilter)
}

private fun <T> håndterManuellLøsning(
    løsningAndSøker: Pair<T, SøkereKafkaDto>,
    håndter: T.(Søker) -> List<Behov>,
): Pair<SøkereKafkaDto, List<DtoBehov>> {
    val (manuellKafkaDto, søkereKafkaDto) = løsningAndSøker
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto())

    val dtoBehov = manuellKafkaDto.håndter(søker)

    return søker.toDto().toJson() to dtoBehov.map { it.toDto(søkereKafkaDto.personident) }
}
