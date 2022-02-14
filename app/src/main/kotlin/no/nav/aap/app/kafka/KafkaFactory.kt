package no.nav.aap.app.kafka

import no.nav.aap.app.kafka.json.JsonDeserializer
import no.nav.aap.app.kafka.json.JsonSerializer
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

object KafkaFactory {
    inline fun <reified V : Any> createConsumer(config: KafkaConfig): Consumer<String, V> {
        val props = kafkaProperties(config) + mapOf(
            ConsumerConfig.GROUP_ID_CONFIG to config.groupId,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
        )
        return KafkaConsumer(props, StringDeserializer(), JsonDeserializer(V::class))
    }

    fun <V : Any> createProducer(config: KafkaConfig): Producer<String, V> {
        val props = kafkaProperties(config) + mapOf(
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
        )
        return KafkaProducer(props, StringSerializer(), JsonSerializer<V>())
    }

    fun kafkaProperties(config: KafkaConfig) =
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

