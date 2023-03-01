package vedtak.stream

import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.KeyValue
import no.nav.aap.kafka.streams.v2.Topology
import vedtak.kafka.Topics
import vedtak.kafka.buffer

internal fun Topology.endredePersonidenterStream(søkere: KTable<SøkereKafkaDtoHistorikk>) {
    consume(Topics.endredePersonidenter)
        .joinWith(søkere, søkere.buffer)
        .flatMapKeyValue { forrigePersonident, endretPersonidenter, søker ->
            listOf(
                KeyValue(endretPersonidenter, søker),
                KeyValue(forrigePersonident, søker) // TODO: skal vi sende tombstone her?
            )
        }
        .secureLogWithKey { key, value ->
            trace("Bytter key på søker fra ${value.søkereKafkaDto.personident} til $key")
        }
        .produce(Topics.søkere, søkere.buffer) { it }
}
