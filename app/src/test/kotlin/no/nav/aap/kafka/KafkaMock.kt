package no.nav.aap.kafka

import no.nav.aap.app.kafka.KafkaConfig
import no.nav.aap.app.modell.KafkaSøknad
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.Producer
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

@Suppress("UNCHECKED_CAST")
class KafkaMock {
    private val topics = mutableMapOf<String, Topic<*>>()
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    fun <T> createTestConsumer(config: KafkaConfig): Consumer<String, T> {
        createTopic(Topic<KafkaSøknad>(config.topic))
        return ConsumerMock(config, this)
    }

    fun <T> createTestProducer(config: KafkaConfig): Producer<String, T> {
        createTopic(Topic<KafkaSøknad>(config.topic))
        return ProducerMock(config, this)
    }

    /** Used for asserting produced records */
    fun <V> allRecords(topic: String): List<ConsumerRecord<String, V>> = (topics[topic] as Topic<V>).getAll()

    fun <V> consume(topic: String): ConsumerRecord<String, V>? = (topics[topic] as Topic<V>).getNext()

    fun <V> produce(topic: String, key: String, value: () -> V): V {
        val record = (topics[topic] as Topic<V>).add(key, value())
        log.info("Produced=${record.topic()} key=${record.key()} offset=${record.offset()} partition=${record.partition()}")
        return record.value()
    }

    private fun <V> createTopic(topic: Topic<V>) = topics.put(topic.name, topic)
}

class Topic<V>(val name: String) {
    private val records = mutableListOf<ConsumerRecord<String, V>>()
    private var committedOffset: Long = 0L
    private var offset: Long = 0L

    fun getNext(): ConsumerRecord<String, V>? = records.getOrNull(offset.toInt())?.also { offset++ }
    fun getAll(): MutableList<ConsumerRecord<String, V>> = records
    fun add(key: String, value: V): ConsumerRecord<String, V> {
        val record = ConsumerRecord(name, 0, committedOffset, key, value)
        records.add(committedOffset.toInt(), record)
        committedOffset++
        return record
    }
}
