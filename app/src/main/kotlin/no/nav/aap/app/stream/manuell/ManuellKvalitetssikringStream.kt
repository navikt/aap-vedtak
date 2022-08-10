package no.nav.aap.app.stream.manuell

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.*
import no.nav.aap.domene.Søker
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.filterNotNull
import no.nav.aap.kafka.streams.extension.join
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.manuellKvalitetssikringStream(søkere: KTable<String, SøkereKafkaDto>) {
    stream(søkere, Topics.kvalitetssikring_11_2, "kvalitetssikring-11-2", Kvalitetssikring_11_2::håndter)
    stream(søkere, Topics.kvalitetssikring_11_3, "kvalitetssikring-11-3", Kvalitetssikring_11_3::håndter)
    stream(søkere, Topics.kvalitetssikring_11_4, "kvalitetssikring-11-4", Kvalitetssikring_11_4_ledd2_ledd3::håndter)
    stream(søkere, Topics.kvalitetssikring_11_5, "kvalitetssikring-11-5", Kvalitetssikring_11_5::håndter)
    stream(søkere, Topics.kvalitetssikring_11_6, "kvalitetssikring-11-6", Kvalitetssikring_11_6::håndter)
    stream(søkere, Topics.kvalitetssikring_11_12, "kvalitetssikring-11-12", Kvalitetssikring_11_12_ledd1::håndter)
    stream(søkere, Topics.kvalitetssikring_11_19, "kvalitetssikring-11-19", Kvalitetssikring_11_19::håndter)
    stream(søkere, Topics.kvalitetssikring_11_29, "kvalitetssikring-11-29", Kvalitetssikring_11_29::håndter)
}

private fun <T> StreamsBuilder.stream(
    søkere: KTable<String, SøkereKafkaDto>,
    topic: Topic<T & Any>,
    kafkaNameFilter: String,
    håndter: (T & Any).(Søker) -> Unit,
) {
    consume(topic)
        .filterNotNull("filter-$kafkaNameFilter-tombstones")
        .join(topic with Topics.søkere, søkere) { løsning, søker -> håndter(løsning, søker, håndter) }
        .produce(Topics.søkere, "produced-soker-med-$kafkaNameFilter")
}

private fun <T> håndter(
    manuellKafkaDto: T,
    søkereKafkaDto: SøkereKafkaDto,
    håndter: T.(Søker) -> Unit,
): SøkereKafkaDto {
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto())

    manuellKafkaDto.håndter(søker)

    return søker.toDto().toJson()
}
