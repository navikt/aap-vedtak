package vedtak.kafka

import io.micrometer.core.instrument.MeterRegistry
import no.nav.aap.kafka.streams.v2.KeyValue
import no.nav.aap.kafka.streams.v2.processor.Processor
import no.nav.aap.kafka.streams.v2.processor.ProcessorMetadata

internal class KafkaCounter<T>(
    private val prometheus: MeterRegistry,
    private val metricName: String,
) : Processor<T, T>(
    named = "$metricName-counter",
) {
    override fun process(metadata: ProcessorMetadata, keyValue: KeyValue<String, T>): T {
        prometheus.counter(metricName).increment()
        return keyValue.value
    }
}
