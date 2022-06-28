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
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

internal fun StreamsBuilder.søknadStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov = consume(Topics.søknad)
        .filterNotNull("filter-soknad-tombstone")
        .leftJoin(Topics.søknad with Topics.søkere, søkere, ::Pair)
        .filter("filter-soknad-ny") { _, (_, søkereKafkaDto) ->
            if (søkereKafkaDto != null) secureLog.warn("oppretter ikke ny søker pga eksisterende: $søkereKafkaDto")

            søkereKafkaDto == null
        }
        .mapValues("soknad-opprett-soker-og-handter") { personident, (jsonSøknad, _) ->
            opprettSøker(personident, jsonSøknad)
        }

    søkerOgBehov
        .mapValues("soknad-hent-ut-soker") { (søker) -> søker }
        .produce(Topics.søkere, "produced-ny-soker")

    søkerOgBehov
        .flatMapValues("soknad-hent-ut-behov") { (_, dtoBehov) -> dtoBehov }
        .sendBehov("soknad")
}

private fun opprettSøker(ident: String, jsonSøknad: JsonSøknad): Pair<SøkereKafkaDto, List<DtoBehov>> {
    val søknad = Søknad(Personident(ident), Fødselsdato(jsonSøknad.fødselsdato))
    val søker = søknad.opprettSøker()
    søker.håndterSøknad(søknad)

    return søker.toDto().toJson() to søknad.behov().map { it.toDto(ident) }
}
