package no.nav.aap.app.kafka

import no.nav.aap.app.log
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
    private val interval: Duration = 10.toDuration(DurationUnit.SECONDS),
    private val deleteCondition: (value: ValueAndTimestamp<V>, now: Long) -> Boolean,
) : Processor<K, V, Void, Void> {

    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<K, ValueAndTimestamp<V>>>(storeName)
        context.schedule(interval.toJavaDuration(), PunctuationType.WALL_CLOCK_TIME) { wallClockTime ->
            store.all().use { iterator ->
                iterator.forEach { record: KeyValue<K, ValueAndTimestamp<V>> ->
                    if (deleteCondition(record.value, wallClockTime)) {
                        log.info("Removed ${record.key}] [${record.value.value()}]")
                        store.delete(record.key)
                    }
                }
            }
        }
    }

    override fun process(record: Record<K, V>?) {}
}

fun <K, V> KTable<K, V>.stateStoreCleaner(
    storeName: String,
    deleteCondition: (value: ValueAndTimestamp<V>, now: Long) -> Boolean,
) = toStream().process(ProcessorSupplier { StateStoreCleaner(storeName, deleteCondition = deleteCondition) }, storeName)
