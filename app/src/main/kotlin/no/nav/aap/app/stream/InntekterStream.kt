package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toJson
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.dto.kafka.InntekterKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.filterNotNullBy
import no.nav.aap.kafka.streams.extension.join
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.inntekterStream(søkere: KTable<String, SøkereKafkaDto>) {
    consume(Topics.inntekter)
        .filterNotNullBy("inntekter-filter-tombstones-og-responses") { inntekter -> inntekter.response }
        .join(Topics.inntekter with Topics.søkere, søkere, håndterInntekter)
        .produce(Topics.søkere, "produced-soker-med-handtert-inntekter")
}

private val håndterInntekter = { inntekterKafkaDto: InntekterKafkaDto, søkereKafkaDto: SøkereKafkaDto ->
    val søker = søkereKafkaDto.toModellApi()
    val (endretSøker) = inntekterKafkaDto.toModellApi().håndter(søker)
    endretSøker.toJson()
}
