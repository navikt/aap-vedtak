package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.sendBehov
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Søknad
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun StreamsBuilder.søknadStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov = consume(Topics.søknad)
        .filterNotNull("filter-soknad-tombstone")
        .leftJoin(Topics.søknad with Topics.søkere, søkere) { jsonSøknad, søker -> jsonSøknad to søker }
        .filter("filter-soknad-ny") { _, (_, søker) -> søker == null }
        .mapValues { personident, (søknad, _) -> opprettSøker(personident, søknad) }

    søkerOgBehov
        .mapValues("soknad-hent-ut-soker") { (søker) -> søker }
        .produce(Topics.søkere, "produced-ny-soker")

    søkerOgBehov
        .flatMapValues("soknad-hent-ut-behov") { (_, dtoBehov) -> dtoBehov }
        .sendBehov("soknad")
}

private fun opprettSøker(ident: String, jsonSøknad: JsonSøknad): Pair<SøkereKafkaDto, List<DtoBehov>> {
    val fødselsdato = LocalDate.parse(ident.take(6), DateTimeFormatter.ofPattern("ddMMyy")).minusYears(100)
    val søknad = Søknad(Personident(ident), Fødselsdato(fødselsdato))
    val søker = søknad.opprettSøker()
    søker.håndterSøknad(søknad)

    return søker.toDto().toJson() to søknad.behov().map { it.toDto(ident) }
}
