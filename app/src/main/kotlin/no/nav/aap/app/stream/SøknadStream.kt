package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.sendBehov
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.Søknad
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

internal fun StreamsBuilder.søknadStream(søkere: KTable<String, SøkereKafkaDto>) {
    val søkerOgBehov = consume(Topics.søknad)
        .filterNotNull("filter-soknad-tombstone")
        .leftJoin(Topics.søknad with Topics.søkere, søkere)
        .filterValues("filter-soknad-ny") { (_, søkereKafkaDto) ->
            if (søkereKafkaDto != null) secureLog.warn("oppretter ikke ny søker pga eksisterende: $søkereKafkaDto")

            søkereKafkaDto == null
        }
        .firstPairValue("soknad-hent-ut-soknad-fra-join")
        .mapValues("soknad-opprett-soker-og-handter", opprettSøker)

    søkerOgBehov
        .firstPairValue("soknad-hent-ut-soker")
        .produce(Topics.søkere, "produced-ny-soker")

    søkerOgBehov
        .secondPairValue("soknad-hent-ut-behov")
        .flatten("soknad-flatten-behov")
        .sendBehov("soknad")
}

private val opprettSøker = { ident: String, jsonSøknad: JsonSøknad ->
    val søknad = Søknad(Personident(ident), Fødselsdato(jsonSøknad.fødselsdato))
    val søker = søknad.opprettSøker()
    søker.håndterSøknad(søknad)

    søker.toDto().toJson() to søknad.behov().map { it.toDto(ident) }
}
