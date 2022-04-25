package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toAvro
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Søknad
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.søknadStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søkerOgBehov = consume(topics.søknad)
        .filterNotNull { "filter-soknad-tombstone" }
        .leftJoin(topics.søknad with topics.søkere, søkere, SøknadAndSøker::join)
        .mapValues(::opprettSøker)

    søkerOgBehov
        .mapValues(named("soknad-hent-ut-soker")) { (søker) -> søker }
        .produce(topics.søkere) {"produced-ny-soker"}

    søkerOgBehov
        .flatMapValues(named("soknad-hent-ut-behov")) { (_, dtoBehov) -> dtoBehov }
        .sendBehov("soknad", topics)
}

private fun opprettSøker(wrapper: SøknadAndSøker): Pair<AvroSøker, List<DtoBehov>> {
    val ident = wrapper.søknad.ident.verdi
    val søknad = Søknad(Personident(ident), Fødselsdato(wrapper.søknad.fødselsdato))
    val søker = søknad.opprettSøker()
    søker.håndterSøknad(søknad)

    return søker.toDto().toAvro() to søknad.behov().map { it.toDto(ident) }
}

private data class SøknadAndSøker(val søknad: JsonSøknad, val søker: AvroSøker?) {
    companion object {
        fun join(søknad: JsonSøknad, søker: AvroSøker?) = SøknadAndSøker(søknad, søker)
    }
}
