package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.modell.toDto
import no.nav.aap.app.modell.toJson
import no.nav.aap.domene.Søker
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem

internal fun StreamsBuilder.medlemStream(søkere: KTable<String, SøkereKafkaDto>) {
    consume(Topics.medlem)
        .filterNotNullBy("medlem-filter-tombstones-og-responses") { medlem -> medlem.response }
        .selectKey("keyed_personident") { _, value -> value.personident }
        .join(Topics.medlem with Topics.søkere, søkere, håndterMedlem)
        .produce(Topics.søkere, "produced-soker-med-medlem")
}

private val håndterMedlem = { avroMedlem: AvroMedlem, søkereKafkaDto: SøkereKafkaDto ->
    val søker = Søker.gjenopprett(søkereKafkaDto.toDto()).apply {
        val medlem = avroMedlem.toDto()
        håndterLøsning(medlem)
    }

    søker.toDto().toJson()
}
