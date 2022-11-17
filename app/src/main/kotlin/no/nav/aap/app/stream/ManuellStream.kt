package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.dto.kafka.*
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.extension.*
import no.nav.aap.modellapi.BehovModellApi
import no.nav.aap.modellapi.SøkerModellApi
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.manuellInnstillingStream(søkere: KTable<String, SøkereKafkaDto>) {
    stream(søkere, Topics.innstilling_11_6, "innstilling-11-6", Innstilling_11_6KafkaDto::håndter)
}

internal fun StreamsBuilder.manuellLøsningStream(søkere: KTable<String, SøkereKafkaDto>) {
    stream(søkere, Topics.manuell_11_2, "manuell-11-2", Løsning_11_2_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_11_3, "manuell-11-3", Løsning_11_3_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_11_4, "manuell-11-4", Løsning_11_4_ledd2_ledd3_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_11_5, "manuell-11-5", Løsning_11_5_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_11_6, "manuell-11-6", Løsning_11_6_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_22_13, "manuell-11-12", Løsning_22_13_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_11_19, "manuell-11-19", Løsning_11_19_manuellKafkaDto::håndter)
    stream(søkere, Topics.manuell_11_29, "manuell-11-29", Løsning_11_29_manuellKafkaDto::håndter)
}

internal fun StreamsBuilder.manuellKvalitetssikringStream(søkere: KTable<String, SøkereKafkaDto>) {
    stream(søkere, Topics.kvalitetssikring_11_2, "kvalitetssikring-11-2", Kvalitetssikring_11_2KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_11_3, "kvalitetssikring-11-3", Kvalitetssikring_11_3KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_11_4, "kvalitetssikring-11-4", Kvalitetssikring_11_4_ledd2_ledd3KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_11_5, "kvalitetssikring-11-5", Kvalitetssikring_11_5KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_11_6, "kvalitetssikring-11-6", Kvalitetssikring_11_6KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_22_13, "kvalitetssikring-11-12", Kvalitetssikring_22_13KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_11_19, "kvalitetssikring-11-19", Kvalitetssikring_11_19KafkaDto::håndter)
    stream(søkere, Topics.kvalitetssikring_11_29, "kvalitetssikring-11-29", Kvalitetssikring_11_29KafkaDto::håndter)
}

private fun <T> StreamsBuilder.stream(
    søkere: KTable<String, SøkereKafkaDto>,
    topic: Topic<T & Any>,
    kafkaNameFilter: String,
    håndter: (T & Any).(SøkerModellApi) -> Pair<SøkerModellApi, List<BehovModellApi>>,
) {
    val søkerOgBehov =
        consume(topic)
            .filterNotNull("filter-$kafkaNameFilter-tombstones")
            .join(topic with Topics.søkere, søkere) { løsning, søker -> håndter(løsning, søker, håndter) }

    søkerOgBehov
        .firstPairValue("$kafkaNameFilter-hent-ut-soker")
        .produce(Topics.søkere, "produced-soker-med-$kafkaNameFilter", true)

    søkerOgBehov
        .secondPairValue("$kafkaNameFilter-hent-ut-behov")
        .flatten("$kafkaNameFilter-flatten-behov")
        .sendBehov(kafkaNameFilter)
}

private fun <T> håndter(
    manuellKafkaDto: T,
    søkereKafkaDto: SøkereKafkaDto,
    håndter: T.(SøkerModellApi) -> Pair<SøkerModellApi, List<BehovModellApi>>,
): Pair<SøkereKafkaDto, List<BehovModellApi>> {
    val søker = søkereKafkaDto.toModellApi()
    val (endretSøker, dtoBehov) = manuellKafkaDto.håndter(søker)
    return endretSøker.toJson(søkereKafkaDto.sekvensnummer) to dtoBehov
}
