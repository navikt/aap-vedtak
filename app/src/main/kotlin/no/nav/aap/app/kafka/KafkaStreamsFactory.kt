package no.nav.aap.app.kafka

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.aap.app.config.KafkaConfig
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.*
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*
import kotlin.reflect.full.isSubclassOf

interface Kafka : AutoCloseable {
    fun start()
    fun createKafkaStream(topology: Topology, config: KafkaConfig)
    fun <K, V> stateStore(name: String): ReadOnlyKeyValueStore<K, V>
}

class KafkaStreamsFactory : Kafka {
    private lateinit var streams: KafkaStreams

    override fun createKafkaStream(topology: Topology, config: KafkaConfig) {
        val properties = Properties().apply {
            val serde = serde<Any>()
            putAll(consumerProperties(config, serde.deserializer()))
            putAll(producerProperties(config, serde.serializer()))
            putAll(streamsProperties(config))
            putAll(aivenProperties(config))
        }

        streams = KafkaStreams(topology, properties)
    }

    override fun <K, V> stateStore(name: String): ReadOnlyKeyValueStore<K, V> =
        streams.store(
            StoreQueryParameters.fromNameAndType(
                name,
                QueryableStoreTypes.keyValueStore()
            )
        )

    override fun start() = streams.start()
    override fun close() = streams.close()

    fun consumerProperties(config: KafkaConfig, deserializer: Deserializer<out Any>) = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        ConsumerConfig.GROUP_ID_CONFIG to config.groupId,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to deserializer::class.java.name,
    )

    fun producerProperties(config: KafkaConfig, serializer: Serializer<out Any>) = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to serializer::class.java.name,
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
        ProducerConfig.ACKS_CONFIG to "all",
    )

    fun streamsProperties(config: KafkaConfig) = mapOf(
        StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.StringSerde::class.java,
        StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to SpecificAvroSerde::class.java,
        StreamsConfig.APPLICATION_ID_CONFIG to config.groupId,
        StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG to 0,
        StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG to LogAndSkipOnInvalidTimestamp::class.java,
        StreamsConfig.REPLICATION_FACTOR_CONFIG to 1,
        StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG) to "all",
        StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG to StreamsConfig.OPTIMIZE,
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrl,
        AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG to "${config.schemaRegistryUser}:${config.schemaRegistryPwd}",
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
            )
            false -> mapOf()
        }
}

inline fun <reified V : Any> KafkaStreamsFactory.createConsumer(config: KafkaConfig): Consumer<String, V> =
    KafkaConsumer(consumerProperties(config, serde<V>().deserializer()))

inline fun <reified V : Any> KafkaStreamsFactory.createProducer(config: KafkaConfig): Producer<String, V> =
    KafkaProducer(producerProperties(config, serde<V>().serializer()))

inline fun <reified V : Any> serde(): Serde<out Any> = when (V::class.isSubclassOf(SpecificRecord::class)) {
    true -> SpecificAvroSerde()
    false -> JsonSerde(V::class)
}
