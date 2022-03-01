package no.nav.aap.app.kafka

import no.nav.aap.app.log
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.processor.PunctuationType
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ValueAndTimestamp
import java.time.Duration

internal class StateStoreDeleter<V>(
    private val storeName: String,
    private val deleteKey: () -> String?,
) : Processor<String, V, Void, Void> {

    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<String, ValueAndTimestamp<V>>>(storeName)
        context.schedule(Duration.ofSeconds(1), PunctuationType.WALL_CLOCK_TIME) {
            deleteKey.invoke()?.let { key ->
                store.delete(key)?.let {
                    log.info("Deleted [$storeName] [$key] [${it.value()}]")
                }
            }
        }
    }

    override fun process(record: Record<String, V>?) {}
}

fun <V> KTable<String, V>.scheduleCleanup(store: String, deleteKey: () -> String?) =
    toStream().process(
        ProcessorSupplier { StateStoreDeleter(store, deleteKey) },
        store
    )
