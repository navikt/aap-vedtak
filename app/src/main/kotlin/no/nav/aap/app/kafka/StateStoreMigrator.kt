package no.nav.aap.app.kafka

import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.aap.kafka.serde.json.Migratable
import no.nav.aap.kafka.streams.Table
import no.nav.aap.kafka.streams.named
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ValueAndTimestamp
import org.slf4j.LoggerFactory

internal class StateStoreMigrator<K, V : Migratable>(
    private val table: Table<V>,
    private val producer: Producer<K, V>,
    private val logValue: Boolean,
) : Processor<K, V, Void, Void> {
    override fun process(record: Record<K, V>) {}

    override fun init(context: ProcessorContext<Void, Void>) {
        val store = context.getStateStore<KeyValueStore<K, ValueAndTimestamp<V>>>(table.stateStoreName)
        store.all().use {
            it.asSequence()
                .forEach { record ->
                    val (key, value) = record.key to record.value.value()
                    if (value.erMigrertAkkuratNå()) {
                        val migrertRecord = ProducerRecord(table.source.name, key, value)
                        producer.send(migrertRecord) { meta, error ->
                            if (error == null) {
                                secureLog.trace(
                                    "Migrerte state store",
                                    kv("key", key),
                                    kv("topic", table.source.name),
                                    kv("table", table.name),
                                    kv("store", table.stateStoreName),
                                    kv("partition", meta.partition()),
                                    kv("offset", meta.offset()),
                                    if (logValue) kv("value", value) else null
                                )
                            } else {
                                secureLog.error("klarte ikke sende migrert dto", error)
                            }
                        }
                    }
                }
        }
    }

    private companion object {
        private val secureLog = LoggerFactory.getLogger("secureLog")
    }
}

internal fun <K, V : Migratable> KTable<K, V>.migrateStateStore(
    table: Table<V>,
    migrationProducer: Producer<K, V>,
    logValue: Boolean = false,
) = toStream().process(
    ProcessorSupplier { StateStoreMigrator(table, migrationProducer, logValue) },
    named("migrate-${table.stateStoreName}"),
    table.stateStoreName
)
