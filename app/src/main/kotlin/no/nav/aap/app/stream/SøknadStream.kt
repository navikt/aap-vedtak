package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.sendBehov
import no.nav.aap.app.kafka.toJson
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.SøknadKafkaDto
import no.nav.aap.kafka.streams.extension.*
import no.nav.aap.modellapi.SøknadModellApi
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

private val opprettSøker = { ident: String, jsonSøknad: SøknadKafkaDto ->
    val søknad = SøknadModellApi(ident, jsonSøknad.fødselsdato)
    val (endretSøker, dtoBehov) = søknad.håndter()
    endretSøker.toJson(0) to dtoBehov
}
