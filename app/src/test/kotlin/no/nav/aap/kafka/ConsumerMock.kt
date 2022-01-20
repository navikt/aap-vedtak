package no.nav.aap.kafka

import no.nav.aap.app.kafka.KafkaConfig
import org.apache.kafka.clients.consumer.*
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

internal class ConsumerMock<T>(
    private val config: KafkaConfig,
    private val kafka: KafkaMock,
) : Consumer<String, T> {

    override fun poll(timeout: Duration): ConsumerRecords<String, T> {
        val timeoutMillis = Instant.now().plus(timeout).toEpochMilli()
        val records: MutableList<ConsumerRecord<String, T>> = mutableListOf()
        // Simulate kafka poll timeout by reading produced records until the timeout is reached.
        while (Instant.now().toEpochMilli() < timeoutMillis) {
            kafka.consume<T>(config.topic)?.let(records::add)
        }

        val partition = TopicPartition(config.topic, 0)
        return ConsumerRecords(mapOf(partition to records))
    }

    override fun subscribe(topics: MutableCollection<String>) {}
    override fun unsubscribe() {}
    override fun close() {}
    override fun close(timeout: Duration) {}
    override fun close(t: Long, u: TimeUnit) = TODO("dead end")
    override fun assignment() = TODO("dead end")
    override fun subscription() = TODO("dead end")
    override fun subscribe(t: MutableCollection<String>, c: ConsumerRebalanceListener) = TODO("dead end")
    override fun subscribe(p: Pattern, c: ConsumerRebalanceListener) = TODO("dead end")
    override fun subscribe(p: Pattern) = TODO("dead end")
    override fun assign(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun poll(t: Long) = TODO("dead end")
    override fun commitSync() = TODO("dead end")
    override fun commitSync(t: Duration) = TODO("dead end")
    override fun commitSync(o: MutableMap<TopicPartition, OffsetAndMetadata>) = TODO("dead end")
    override fun commitSync(o: MutableMap<TopicPartition, OffsetAndMetadata>, t: Duration) = TODO("dead end")
    override fun commitAsync() = TODO("dead end")
    override fun commitAsync(c: OffsetCommitCallback) = TODO("dead end")
    override fun commitAsync(o: MutableMap<TopicPartition, OffsetAndMetadata>, c: OffsetCommitCallback) = TODO("")
    override fun seek(p: TopicPartition, o: Long) = TODO("dead end")
    override fun seek(p: TopicPartition, o: OffsetAndMetadata) = TODO("dead end")
    override fun seekToBeginning(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun seekToEnd(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun position(p: TopicPartition) = TODO("dead end")
    override fun position(p: TopicPartition, timeout: Duration) = TODO("dead end")
    override fun committed(p: TopicPartition) = TODO("dead end")
    override fun committed(p: TopicPartition, timeout: Duration) = TODO("dead end")
    override fun committed(p: MutableSet<TopicPartition>) = TODO("dead end")
    override fun committed(p: MutableSet<TopicPartition>, t: Duration) = TODO("dead end")
    override fun metrics() = TODO("dead end")
    override fun partitionsFor(t: String) = TODO("dead end")
    override fun partitionsFor(t: String, tt: Duration) = TODO("dead end")
    override fun listTopics() = TODO("dead end")
    override fun listTopics(t: Duration) = TODO("dead end")
    override fun paused() = TODO("dead end")
    override fun pause(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun resume(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun offsetsForTimes(t: MutableMap<TopicPartition, Long>) = TODO("dead end")
    override fun offsetsForTimes(tts: MutableMap<TopicPartition, Long>, t: Duration) = TODO("dead end")
    override fun beginningOffsets(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun beginningOffsets(p: MutableCollection<TopicPartition>, t: Duration) = TODO("dead end")
    override fun endOffsets(p: MutableCollection<TopicPartition>) = TODO("dead end")
    override fun endOffsets(p: MutableCollection<TopicPartition>, t: Duration) = TODO("dead end")
    override fun groupMetadata() = TODO("dead end")
    override fun enforceRebalance() = TODO("dead end")
    override fun wakeup() = TODO("dead end")
}
