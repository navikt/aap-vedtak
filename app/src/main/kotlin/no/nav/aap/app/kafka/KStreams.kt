package no.nav.aap.app.kafka

import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.aap.app.log
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.KafkaStreams.State
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*

interface Kafka : AutoCloseable {
    fun init(topology: Topology, config: KafkaConfig)
    fun <V : Any> createProducer(topic: Topic<String, V>): Producer<String, V>
    fun <V : Any> createConsumer(topic: Topic<String, V>): Consumer<String, V>
    fun <V> getStore(name: String): ReadOnlyKeyValueStore<String, V>
    fun healthy(): Boolean
}

class KStreams : Kafka {
    private lateinit var config: KafkaConfig
    private lateinit var streams: KafkaStreams

    override fun init(topology: Topology, config: KafkaConfig) {
        this.streams = KafkaStreams(topology, config.consumer + config.producer).apply { start() }
        this.config = config
    }

    override fun <V> getStore(name: String): ReadOnlyKeyValueStore<String, V> =
        streams.store(StoreQueryParameters.fromNameAndType(name, QueryableStoreTypes.keyValueStore()))

    override fun healthy(): Boolean = streams.state() in listOf(State.CREATED, State.RUNNING, State.REBALANCING)
    override fun close() = streams.close()

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
fun <V> materialized(name: String): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> = Materialized.`as`(name)

fun <V> ReadOnlyKeyValueStore<String, V>.allValues(): List<V> =
    all().use { it.asSequence().map(KeyValue<String, V>::value).toList() }

class LogContinueErrorHandler : DefaultProductionExceptionHandler() {
    override fun handle(record: ProducerRecord<ByteArray, ByteArray>?, exception: Exception?)
            : ProductionExceptionHandler.ProductionExceptionHandlerResponse {
        log.error("Feil i streams, logger og leser neste record", exception)
        return ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
    }
}
