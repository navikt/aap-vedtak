package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun StreamsBuilder.endredePersonidenterStream(søkere: KTable<String, SøkereKafkaDtoHistorikk>) {
    consume(Topics.endredePersonidenter)
        .filterNotNull("filter-endrede-personidenter-tombstone")
        .join(Topics.endredePersonidenter with Topics.søkere, søkere, ::Pair)
        .flatMap { forrigePersonident, (endretPersonidenter, søker) ->
            listOf(
                KeyValue(endretPersonidenter, søker),
                KeyValue(forrigePersonident, null)
            )
        }
        .peek { key, value -> log.trace("Bytter key på søker fra ${value?.søkereKafkaDto?.personident} til $key") }
        .to(Topics.søkere.name, søkereSerde)
}

private val søkereSerde get() =
    Produced
        .with(Topics.søkere.keySerde, Topics.søkere.valueSerde)
        .withName("produce-soker-with-ny-personident")

private val log: Logger = LoggerFactory.getLogger("secureLog")
