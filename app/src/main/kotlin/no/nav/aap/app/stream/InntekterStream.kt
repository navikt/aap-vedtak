package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoInntekter
import no.nav.aap.dto.DtoSøker
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.inntekterStream(søkere: KTable<String, SøkereKafkaDto>, topics: Topics) {
    consume(topics.inntekter)
        .filterNotNull("remove-inntekter-tombstones")
        .filter("inntekter-filter-responses") { _, inntekter -> inntekter.response != null }
        .join(topics.inntekter with topics.søkere, søkere, InntekterAndSøker::create)
        .mapValues(::håndterInntekter)
        .produce(topics.søkere, "produced-soker-med-handtert-inntekter")
}

private fun håndterInntekter(inntekterAndSøker: InntekterAndSøker): SøkereKafkaDto {
    val søker = Søker.gjenopprett(inntekterAndSøker.dtoSøker)

    inntekterAndSøker.inntekter.håndter(søker)

    return søker.toDto().toJson()
}

private data class InntekterAndSøker(val inntekter: DtoInntekter, val dtoSøker: DtoSøker) {
    companion object {
        fun create(løsning: InntekterKafkaDto, søker: SøkereKafkaDto): InntekterAndSøker =
            InntekterAndSøker(løsning.toDto(), søker.toDto())
    }
}
