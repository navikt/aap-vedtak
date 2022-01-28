package no.nav.aap.kafka

import no.nav.aap.app.kafka.KafkaConfig
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

internal class ProducerMock<T>(
    private val config: KafkaConfig,
    private val kafka: KafkaMock,
) : Producer<String, T> {

    override fun send(record: ProducerRecord<String, T>): Future<RecordMetadata> {
        kafka.produce(config.topic, record.key()) {
            record.value()
        }
        return CompletableFuture.completedFuture(RecordMetadata(null, 0L, 0, 0L, 0, 0))
    }

    override fun close() {}
    override fun close(t: Duration) = TODO("dead end")
    override fun initTransactions() = TODO("dead end")
    override fun beginTransaction() = TODO("dead end")
    override fun commitTransaction() = TODO("dead end")
    override fun abortTransaction() = TODO("dead end")
    override fun flush() = TODO("dead end")
    override fun partitionsFor(t: String) = TODO("dead end")
    override fun metrics() = TODO("dead end")
    override fun send(r: ProducerRecord<String, T>, c: Callback) = TODO("dead end")
    override fun sendOffsetsToTransaction(o: MutableMap<TopicPartition, OffsetAndMetadata>, g: String) =
        TODO("dead end")

    override fun sendOffsetsToTransaction(o: MutableMap<TopicPartition, OffsetAndMetadata>, m: ConsumerGroupMetadata) =
        TODO("dead end")
}
