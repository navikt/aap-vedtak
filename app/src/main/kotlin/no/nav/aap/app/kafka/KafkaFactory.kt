package no.nav.aap.app.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

object KafkaFactory {
    fun <K, V> createConsumer(
        config: KafkaConfig,
        deserializer: KDeserializer = KDeserializer(),
    ): KafkaConsumer<K, V> {
        val props = kafkaProperties(config) + mapOf(
            ConsumerConfig.GROUP_ID_CONFIG to config.groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to deserializer.key,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to deserializer.value,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        )
        return KafkaConsumer(props)
    }

    fun <K, V> createProducer(
        config: KafkaConfig,
        serializer: KSerialiser = KSerialiser(),
    ): KafkaProducer<K, V> {
        val props = kafkaProperties(config) + mapOf(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to serializer.key,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to serializer.value,
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
        )
        return KafkaProducer(props)
    }

    private fun kafkaProperties(config: KafkaConfig) =
        when (config.security) {
            false -> mapOf(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
                CommonClientConfigs.CLIENT_ID_CONFIG to config.clientId,
            )
            true -> mapOf(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG to config.brokers,
                CommonClientConfigs.CLIENT_ID_CONFIG to config.clientId,
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
        }
}

data class KafkaConfig(
    val brokers: String,
    val groupId: String,
    val clientId: String,
    val security: Boolean,
    val truststorePath: String,
    val keystorePath: String,
    val credstorePsw: String,
    val topic: String,
)

data class KSerialiser(
    val key: Class<*> = StringSerializer::class.java,
    val value: Class<*> = JsonSerializer::class.java,
)

data class KDeserializer(
    val key: Class<*> = StringDeserializer::class.java,
    val value: Class<*> = JsonDeserializer::class.java,
)