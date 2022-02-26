package no.nav.aap.app.kafka

import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.aap.app.log
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Lytter
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.KafkaStreams.State
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*

interface Kafka : AutoCloseable {
    fun start(topology: Topology, kafkaConfig: KafkaConfig)
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

    override fun start(topology: Topology, kafkaConfig: KafkaConfig) {
        streams = KafkaStreams(topology, kafkaConfig.consumer + kafkaConfig.producer)
        streams.setUncaughtExceptionHandler { err: Throwable ->
            log.error("Uventet feil, logger og leser neste record, ${err.message}")
            StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD
        }
        streams.setStateListener { newState, oldState ->
            log.info("Kafka streams state changed: $oldState -> $newState")
            if (oldState == State.CREATED && newState == State.RUNNING) started = true
        }
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

class LogContinueErrorHandler : ProductionExceptionHandler {
    override fun configure(configs: MutableMap<String, *>?) {}
    override fun handle(
        record: ProducerRecord<ByteArray, ByteArray>?,
        exception: Exception?
    ): ProductionExceptionHandlerResponse {
        log.error("Feil i streams, logger og leser neste record", exception)
        return CONTINUE
    }
}

internal fun <AVROVALUE : Any, MAPPER> BranchedKStream<String, DtoBehov>.branch(
    topic: Topic<String, AVROVALUE>,
    branchName: String,
    predicate: (DtoBehov) -> Boolean,
    getMapper: () -> MAPPER
) where MAPPER : ToAvro<AVROVALUE>, MAPPER : Lytter =
    branch({ _, value -> predicate(value) }, Branched.withConsumer<String?, DtoBehov?> { chain ->
        chain
            .mapValues(named("branch-$branchName-map-behov")) { value -> getMapper().also(value::accept).toAvro() }
            .peek { k: String, v -> log.info("produced [${topic.name}] [$k] [$v]") }
            .to(topic.name, topic.produced("branch-$branchName-produced-behov"))
    }.withName("-branch-$branchName"))

internal fun <K, V> KStream<K, V>.filter(named: Named, predicate: (K, V) -> Boolean) = filter(predicate, named)
internal fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (V) -> VR) = mapValues(mapper, named)
internal fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (K, V) -> VR) = mapValues(mapper, named)
internal fun <K, V, VR> KStream<K, V>.flatMapValues(named: Named, mapper: (V) -> Iterable<VR>) =
    flatMapValues(mapper, named)


internal interface ToAvro<out AVROVALUE> {
    fun toAvro(): AVROVALUE
}
