package no.nav.aap.app

import no.nav.aap.app.kafka.KafkaConfig
import no.nav.aap.app.kafka.KafkaFactory
import no.nav.aap.app.modell.KafkaSøknad
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

class EmbeddedKafka(topic: String) : AutoCloseable {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private val kafka = ThreadSafeKafkaEnvironment(topicNames = listOf(topic), autoStart = true)
    private val producer = kafka.createProducer()
    val brokersURL get() = kafka.brokersURL

    fun produce(topic: String, key: String, value: KafkaSøknad) {
        val record = ProducerRecord(topic, null, key, value, listOf())
        producer.send(record).get().also {
            log.info("broker: $brokersURL")
            log.info("Produced record on $it (topic-partition@offset)")
            log.info(record.toString())
        }
    }

    override fun close() {
        producer.close()
        kafka.close()
    }
}

private fun ThreadSafeKafkaEnvironment.createProducer(): Producer<String, KafkaSøknad> {
    val config = KafkaConfig(
        brokers = brokersURL,
        groupId = "",
        topic = "",
        clientId = "producer-test-client",
        security = false,
        truststorePath = "",
        keystorePath = "",
        credstorePsw = ""
    )

    return KafkaFactory.createProducer(config)
}
