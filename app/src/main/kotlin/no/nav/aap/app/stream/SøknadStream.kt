package no.nav.aap.app.stream

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toAvro
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Søknad
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.søknadStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søkerOgBehov = stream(topics.søknad.name, topics.søknad.consumed("soknad-mottatt"))
        .logConsumed()
        .leftJoin(søkere, SøknadAndSøker::join, topics.søknad.joined(topics.søkere))
        .filter(named("skip-eksisterende-soker")) { _, (_, søker) -> søker == null }
        .mapValues(::opprettSøker, named("opprett-soknad"))

    søkerOgBehov
        .mapValues(named("soknad-hent-ut-soker")) { (søker) -> søker }
        .to(topics.søkere, topics.søkere.produced("produced-ny-soker"))

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
