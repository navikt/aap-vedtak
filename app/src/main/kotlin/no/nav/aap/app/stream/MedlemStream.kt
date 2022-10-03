package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toJson
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.dto.kafka.MedlemKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.kafka.streams.concurrency.RaceConditionBuffer
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.medlemStream(
    søkere: KTable<String, SøkereKafkaDto>,
    buffer: RaceConditionBuffer<String, SøkereKafkaDto>
) {
    consume(Topics.medlem)
        .filterNotNullBy("medlem-filter-tombstones-og-responses") { medlem -> medlem.response }
        .selectKey("keyed_personident") { _, value -> value.personident }
        .join(Topics.medlem with Topics.søkere, søkere, buffer, håndterMedlem)
        .produce(Topics.søkere, buffer, "produced-soker-med-medlem")
}

private val håndterMedlem = { medlemKafkaDto: MedlemKafkaDto, søkereKafkaDto: SøkereKafkaDto ->
    val søker = søkereKafkaDto.toModellApi()
    val (endretSøker) = medlemKafkaDto.toModellApi().håndter(søker)
    endretSøker.toJson(søkereKafkaDto.sekvensnummer)
}
