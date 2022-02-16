package no.nav.aap.app.kafka

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import no.nav.aap.app.config.KafkaConfig
import no.nav.aap.app.log
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.streams.*
import org.apache.kafka.streams.KafkaStreams.*
import org.apache.kafka.streams.errors.InvalidStateStoreException
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.time.Duration
import java.util.*

interface Kafka : AutoCloseable {
    fun start()
    fun createKafkaStream(topology: Topology, config: KafkaConfig)
    fun <K, V> stateStore(name: String): ReadOnlyKeyValueStore<K, V>
    fun healthy(): Boolean
    suspend fun <K, V> waitForStore(name: String): ReadOnlyKeyValueStore<K, V>
}

class KafkaStreamsFactory : Kafka {
    private lateinit var streams: KafkaStreams

    override fun createKafkaStream(topology: Topology, config: KafkaConfig) {
        val properties = Properties().apply {
            putAll(consumerProperties(config))
            putAll(producerProperties(config))
            putAll(streamsProperties(config))
            putAll(aivenProperties(config))
        }

        streams = KafkaStreams(topology, properties)
    }

    override fun <K, V> stateStore(name: String): ReadOnlyKeyValueStore<K, V> =
        streams.store(StoreQueryParameters.fromNameAndType(name, QueryableStoreTypes.keyValueStore()))

    override fun healthy(): Boolean = streams.state() in listOf(State.CREATED, State.RUNNING, State.REBALANCING)
    override fun start() = streams.start()
    override fun close() = streams.close()

    fun consumerProperties(config: KafkaConfig) = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        ConsumerConfig.GROUP_ID_CONFIG to "aap-vedtak",
        ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG to Duration.ofSeconds(124).toMillis().toInt(),
    )

    fun producerProperties(config: KafkaConfig, vararg additionalConfig: Pair<String, String>) = mapOf(
        CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
        ProducerConfig.ACKS_CONFIG to "all",
        ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to "1",
        *additionalConfig
    )

    fun streamsProperties(config: KafkaConfig) = mapOf(
        StreamsConfig.APPLICATION_ID_CONFIG to "aap-vedtak",
        StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG to 0,
        StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG to LogAndSkipOnInvalidTimestamp::class.java,
        StreamsConfig.REPLICATION_FACTOR_CONFIG to 1,
//        StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG to StreamsConfig.OPTIMIZE,
        StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG) to "all",
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrl,
    )

    private fun aivenProperties(config: KafkaConfig): Map<String, String> =
        when (config.security) {
            true -> mapOf(
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SSL",
                SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG to "JKS",
                SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to config.truststorePath,
                SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to config.credstorePsw,
                SslConfigs.SSL_KEYSTORE_TYPE_CONFIG to "PKCS12",
                SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to config.keystorePath,
                SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to config.credstorePsw,
                SslConfigs.SSL_KEY_PASSWORD_CONFIG to config.credstorePsw,
                SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG to "",
                AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG to "${config.schemaRegistryUser}:${config.schemaRegistryPwd}",
            )
            false -> mapOf()
        }

    override suspend fun <K, V> waitForStore(name: String): ReadOnlyKeyValueStore<K, V> {
        log.info("Waiting 10_000 ms for store $name to become available")

        val store = withTimeoutOrNull(10_000L) {
            try {
                stateStore<Any, Any>(name)
            } catch (e: InvalidStateStoreException) {
                delay(500L)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return when (store) {
            null -> error("state store not awailable after 10s")
            else -> store as ReadOnlyKeyValueStore<K, V>
        }
    }
}

inline fun <reified V : Any> KafkaStreamsFactory.createConsumer(config: KafkaConfig): Consumer<String, V> =
    KafkaConsumer(consumerProperties(config))

inline fun <reified V : Any> KafkaStreamsFactory.createProducer(config: KafkaConfig): Producer<String, V> =
    KafkaProducer(producerProperties(config))

fun <K, T> ReadOnlyKeyValueStore<K, T>.getAllValues(): List<T> =
    all().use { iterator -> iterator.asSequence().map(KeyValue<K, T>::value).toList() }

fun named(named: String): Named = Named.`as`(named)