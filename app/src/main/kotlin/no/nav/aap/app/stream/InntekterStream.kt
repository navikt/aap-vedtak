package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.Søker
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
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto())

    inntekterKafkaDto.toDto().håndter(søker)

    søker.toDto().toJson()
}
