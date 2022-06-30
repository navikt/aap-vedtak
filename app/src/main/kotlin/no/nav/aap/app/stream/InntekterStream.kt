package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.Søker
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.inntekterStream(søkere: KTable<String, SøkereKafkaDto>) {
    consume(Topics.inntekter)
        .filterNotNull("remove-inntekter-tombstones")
        .filter("inntekter-filter-responses") { _, inntekter -> inntekter.response != null }
        .join(Topics.inntekter with Topics.søkere, søkere, ::Pair)
        .mapValues("inntekter-handter-losning", ::håndterInntekter)
        .produce(Topics.søkere, "produced-soker-med-handtert-inntekter")
}

private fun håndterInntekter(inntekterAndSøker: Pair<InntekterKafkaDto, SøkereKafkaDto>): SøkereKafkaDto {
    val (inntekterKafkaDto, søkereKafkaDto) = inntekterAndSøker
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto())

    inntekterKafkaDto.toDto().håndter(søker)

    return søker.toDto().toJson()
}
