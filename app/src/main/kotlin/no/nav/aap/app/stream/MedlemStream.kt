package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toForrigeDto
import no.nav.aap.app.kafka.toJson
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.dto.kafka.MedlemKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.medlemStream(søkere: KTable<String, SøkereKafkaDtoHistorikk>) {
    consume(Topics.medlem)
        .filterNotNullBy("medlem-filter-tombstones-og-responses") { medlem -> medlem.response }
        .selectKey("keyed_personident") { _, value -> value.personident }
        .join(Topics.medlem with Topics.søkere, søkere, håndterMedlem)
        .produce(Topics.søkere, "produced-soker-med-medlem")
}

private val håndterMedlem = { medlemKafkaDto: MedlemKafkaDto, (søkereKafkaDto): SøkereKafkaDtoHistorikk ->
    val søker = søkereKafkaDto.toModellApi()
    val (endretSøker) = medlemKafkaDto.toModellApi().håndter(søker)
    val endretSøkereKafkaDto = endretSøker.toJson(søkereKafkaDto.sekvensnummer)
    val forrigeSøkereKafkaDto = endretSøkereKafkaDto.toForrigeDto()
    SøkereKafkaDtoHistorikk(endretSøkereKafkaDto, forrigeSøkereKafkaDto)
}
