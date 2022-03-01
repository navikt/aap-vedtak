package no.nav.aap.app.kafka

import no.nav.aap.app.log
import no.nav.aap.app.søkereToDelete
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
) : Processor<String, V, Void, Void> {

    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<String, ValueAndTimestamp<V>>>(storeName)
        context.schedule(Duration.ofSeconds(1), PunctuationType.WALL_CLOCK_TIME) {
            søkereToDelete.poll()?.let { key ->
                log.info("Trying to deleted [$storeName] [$key] [???]")
                store.delete(key)?.let {
                    log.info("Deleted [$storeName] [$key] [${it.value()}]")
                }
            }
        }
    }

    override fun process(record: Record<String, V>?) {}
}

fun <V> KTable<String, V>.scheduleCleanup(store: String) =
    toStream().process(
        ProcessorSupplier { StateStoreDeleter(store) },
        store
    )
