package no.nav.aap.app.kafka

import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException
import no.nav.aap.app.log
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ValueAndTimestamp
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

internal class StateStoreCleaner<K, V>(
    private val storeName: String,
    private val duration: Duration,
    private val recordCleanedCallback: (context: ProcessorContext<Void, Void>, record: KeyValue<K, ValueAndTimestamp<V>>) -> Unit,
    private val predicate: (value: ValueAndTimestamp<V>, now: Long) -> Boolean,
) : Processor<K, V, Void, Void> {
    override fun process(record: Record<K, V>?) {}
    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<K, ValueAndTimestamp<V>>>(storeName)
        context.schedule(duration.toJavaDuration(), PunctuationType.WALL_CLOCK_TIME) { wallClockTime ->
            store.all().use { iterator ->
                runCatching {
                    iterator.forEach { record: KeyValue<K, ValueAndTimestamp<V>> ->
                        if (predicate(record.value, wallClockTime)) {
                            recordCleanedCallback(context, record)
                            log.info("Removed ${record.key}] [${record.value.value()}]")
                            store.delete(record.key)
                        }
                    }
                }.onFailure {
                    if (it is SerializationException && it.cause is RestClientException)
                        log.warn("Failed to clear state store, schema registry is missing schema.")
                }
            }
        }
    }
}

fun <K, V> KTable<K, V>.stateStoreCleaner(
    stateStoreName: String,
    frequency: Duration = 10.toDuration(DurationUnit.SECONDS),
    callback: (context: ProcessorContext<Void, Void>, record: KeyValue<K, ValueAndTimestamp<V>>) -> Unit = doNothing(),
    deleteCondition: (value: ValueAndTimestamp<V>, now: Long) -> Boolean,
) {
    toStream().process(
        ProcessorSupplier { StateStoreCleaner(stateStoreName, frequency, callback, deleteCondition) },
        stateStoreName,
    )
}

private fun <K, V> doNothing() = { _: ProcessorContext<Void, Void>, _: KeyValue<K, ValueAndTimestamp<V>> ->
    log.info("state store cleared")
}
