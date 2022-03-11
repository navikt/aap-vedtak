package no.nav.aap.app.kafka

import io.ktor.http.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.kafka.KafkaStreamsMetrics
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.KafkaStreams.State
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("app")
private val secureLog = LoggerFactory.getLogger("secureLog")

interface Kafka : AutoCloseable {
    fun start(topology: Topology, kafkaConfig: KafkaConfig, registry: MeterRegistry)
    fun <V : Any> createProducer(topic: Topic<String, V>): Producer<String, V>
    fun <V : Any> createConsumer(topic: Topic<String, V>): Consumer<String, V>
    fun <V> getStore(name: String): ReadOnlyKeyValueStore<String, V>
    fun healthy(): Boolean
    fun started(): Boolean
}

class KStreams : Kafka {
    private lateinit var config: KafkaConfig
    private lateinit var streams: KafkaStreams
    private var started: Boolean = false

    override fun start(topology: Topology, kafkaConfig: KafkaConfig, registry: MeterRegistry) {
        streams = KafkaStreams(topology, kafkaConfig.consumer + kafkaConfig.producer)
        streams.setUncaughtExceptionHandler(ProcessingExceptionHandler())
        streams.setStateListener { newState, oldState ->
            log.info("Kafka streams state changed: $oldState -> $newState")
            if (newState == State.RUNNING) started = true
        }
        KafkaStreamsMetrics(streams).bindTo(registry)
        config = kafkaConfig
        streams.start()
    }

    override fun <V> getStore(name: String): ReadOnlyKeyValueStore<String, V> =
        streams.store(StoreQueryParameters.fromNameAndType(name, QueryableStoreTypes.keyValueStore()))

    override fun started() = started
    override fun close() = streams.close()
    override fun healthy(): Boolean = streams.state() in listOf(State.CREATED, State.RUNNING, State.REBALANCING)
    override fun <V : Any> createConsumer(topic: Topic<String, V>): Consumer<String, V> =
        KafkaConsumer(
            config.consumer + mapOf(CommonClientConfigs.CLIENT_ID_CONFIG to "client-${topic.name}"),
            topic.keySerde.deserializer(),
            topic.valueSerde.deserializer()
        )

    override fun <V : Any> createProducer(topic: Topic<String, V>): Producer<String, V> =
        KafkaProducer(
            config.producer + mapOf(CommonClientConfigs.CLIENT_ID_CONFIG to "client-${topic.name}"),
            topic.keySerde.serializer(),
            topic.valueSerde.serializer()
        )
}

fun <V> Kafka.waitForStore(name: String): ReadOnlyKeyValueStore<String, V> = runBlocking {
    log.info("Waiting 10_000 ms for store $name to become available")
    val store = withTimeout(10_000L) {
        flow {
            while (true) {
                runCatching { getStore<V>(name) }.getOrNull()?.let { emit(it) }
                delay(100)
            }
        }.firstOrNull()
    }

    store ?: error("state store not awailable after 10s")
}

fun named(named: String): Named = Named.`as`(named)
fun <V> materialized(
    storeName: String,
    topic: Topic<String, V>,
): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> =
    Materialized.`as`<String?, V, KeyValueStore<Bytes, ByteArray>?>(storeName)
        .withKeySerde(topic.keySerde)
        .withValueSerde(topic.valueSerde)


fun <V> ReadOnlyKeyValueStore<String, V>.allValues(): List<V> =
    all().use { it.asSequence().map(KeyValue<String, V>::value).toList() }

internal fun <K, V> KStream<K, V>.filter(named: Named, predicate: (K, V) -> Boolean) = filter(predicate, named)
internal fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (V) -> VR) = mapValues(mapper, named)
internal fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (K, V) -> VR) = mapValues(mapper, named)
internal fun <K, V, VR> KStream<K, V>.flatMapValues(named: Named, mapper: (V) -> Iterable<VR>) =
    flatMapValues(mapper, named)

fun <V : Any> KStream<String, V>.logConsumed(): KStream<String, V> = transformValues(
    ValueTransformerWithKeySupplier {
        object : ValueTransformerWithKey<String, V?, V?> {
            private lateinit var context: ProcessorContext

            override fun init(context: ProcessorContext) {
                this.context = context
            }

            override fun transform(readOnlyKey: String, value: V?): V? {
                secureLog.info("consumed [${context.topic()}] K:$readOnlyKey V:$value")
                return value
            }

            override fun close() {}
        }
    }
)

fun <K, V> KStream<K, V>.to(topic: Topic<K, V>, producedWith: Produced<K, V>) = this
    .peek { key, value -> secureLog.info("produced [${topic.name}] K:$key V:$value") }
    .to(topic.name, producedWith)
