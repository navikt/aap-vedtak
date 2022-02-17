package no.nav.aap.app.kafka

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.aap.app.log
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.KafkaStreams.State
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*

interface Kafka : AutoCloseable {
    fun start()
    fun create(topology: Topology)
    fun <K, V> getStore(name: String): ReadOnlyKeyValueStore<K, V>
    fun healthy(): Boolean
}

class KStreams(val config: KafkaConfig) : Kafka {
    private lateinit var streams: KafkaStreams

    override fun create(topology: Topology) {
        streams = KafkaStreams(topology, config.consumer + config.producer)
    }

    override fun <K, V> getStore(name: String): ReadOnlyKeyValueStore<K, V> =
        streams.store(StoreQueryParameters.fromNameAndType(name, QueryableStoreTypes.keyValueStore()))

    override fun healthy(): Boolean = streams.state() in listOf(State.CREATED, State.RUNNING, State.REBALANCING)
    override fun start() = streams.start()
    override fun close() = streams.close()

    inline fun <reified V : Any> createConsumer(): Consumer<String, V> = KafkaConsumer(config.consumer)

    inline fun <reified V : Any> createProducer(topic: Topic<String, V>): Producer<String, V> {
        val extras = mapOf(
            CommonClientConfigs.CLIENT_ID_CONFIG to "client-${topic.name}",
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to topic.keySerde.serializer()::class.java,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to topic.valueSerde.serializer()::class.java,
        )
        return KafkaProducer(config.producer.apply { putAll(extras) }, topic.keySerde.serializer(), topic.valueSerde.serializer())
    }
}

fun <K, V> KStreams.waitForStore(name: String): ReadOnlyKeyValueStore<K, V> = runBlocking {
    log.info("Waiting 10_000 ms for store $name to become available")
    val store = withTimeout(10_000L) {
        flow {
            while (true) {
                runCatching { getStore<K, V>(name) }.getOrNull()?.let { emit(it) }
                delay(100)
            }
        }.firstOrNull()
    }

    store ?: error("state store not awailable after 10s")
}

fun named(named: String): Named = Named.`as`(named)
fun <V> materialized(name: String): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> = Materialized.`as`(name)

fun <K, T> ReadOnlyKeyValueStore<K, T>.allValues(): List<T> =
    all().use { it.asSequence().map(KeyValue<K, T>::value).toList() }

operator fun Properties.plus(properties: Properties): Properties = apply { putAll(properties) }

data class KafkaConfig(
    val applicationId: String,
    val brokers: String,
    val clientId: String,
    val security: Boolean,
    val truststorePath: String,
    val keystorePath: String,
    val credstorePsw: String,
    val schemaRegistryUrl: String,
    val schemaRegistryUser: String,
    val schemaRegistryPwd: String,
) {

    private val kStreams: Properties = Properties().apply {
        this[StreamsConfig.APPLICATION_ID_CONFIG] = applicationId
        this[CommonClientConfigs.CLIENT_ID_CONFIG] = clientId
        this[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = "0"
        this[StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG] = LogAndSkipOnInvalidTimestamp::class.java
//        this[StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG)] = "all"
        this[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
    }

    private val ssl: Properties = Properties().apply {
        if (security) {
            this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SSL"
            this[SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG] = "JKS"
            this[SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG] = truststorePath
            this[SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG] = credstorePsw
            this[SslConfigs.SSL_KEYSTORE_TYPE_CONFIG] = "PKCS12"
            this[SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG] = keystorePath
            this[SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG] = credstorePsw
            this[SslConfigs.SSL_KEY_PASSWORD_CONFIG] = credstorePsw
            this[SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG] = ""
            this[AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG] = "${schemaRegistryUser}:${schemaRegistryPwd}"
        }
    }

    val consumer: Properties = kStreams + ssl + Properties().apply {
        this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = brokers
        this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        this[ConsumerConfig.GROUP_ID_CONFIG] = "aap-vedtak"
        this[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 124_000
    }

    val producer: Properties = kStreams + ssl + Properties().apply {
        this[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = brokers
        this[ProducerConfig.ACKS_CONFIG] = "all"
        this[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "5"
        this[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
    }
}
