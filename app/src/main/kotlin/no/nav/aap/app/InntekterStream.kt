package no.nav.aap.app

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.logConsumed
import no.nav.aap.app.kafka.named
import no.nav.aap.app.kafka.to
import no.nav.aap.app.modell.toAvro
import no.nav.aap.app.modell.toDto
import no.nav.aap.domene.Søker
import no.nav.aap.dto.DtoInntekter
import no.nav.aap.dto.DtoSøker
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.inntekter.v1.Inntekter as AvroInntekter
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.inntekterStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    stream(topics.inntekter.name, topics.inntekter.consumed("inntekter-mottatt"))
        .logConsumed()
        .filter({ _, inntekter -> inntekter.response != null }, named("inntekter-filter-responses"))
        .join(søkere, InntekterAndSøker::create, topics.inntekter.joined(topics.søkere))
        .mapValues(::håndterInntekter)
        .to(topics.søkere, topics.søkere.produced("produced-soker-med-handtert-inntekter"))
}

private fun håndterInntekter(inntekterAndSøker: InntekterAndSøker): AvroSøker {
    val søker = Søker.gjenopprett(inntekterAndSøker.dtoSøker)

    inntekterAndSøker.inntekter.håndter(søker)

    return søker.toDto().toAvro()
}

private data class InntekterAndSøker(val inntekter: DtoInntekter, val dtoSøker: DtoSøker) {
    companion object {
        fun create(løsning: AvroInntekter, søker: AvroSøker): InntekterAndSøker =
            InntekterAndSøker(løsning.toDto(), søker.toDto())
    }
}
