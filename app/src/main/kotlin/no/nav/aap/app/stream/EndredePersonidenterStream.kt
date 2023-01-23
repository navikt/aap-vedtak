package no.nav.aap.app.stream

import net.logstash.logback.argument.StructuredArguments
import no.nav.aap.app.kafka.Topics
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.BufferableTopic
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.concurrency.Bufferable
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.processor.api.FixedKeyProcessor
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext
import org.apache.kafka.streams.processor.api.FixedKeyRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun StreamsBuilder.endredePersonidenterStream(søkere: KTable<String, SøkereKafkaDtoHistorikk>) {
        consume(Topics.endredePersonidenter)
        .filterNotNull("filter-endrede-personidenter-tombstone")
        .join(Topics.endredePersonidenter with Topics.søkere, søkere, ::Pair)
        .flatMap { forrigePersonident, (endretPersonidenter, søker) ->
            listOf(
                KeyValue(endretPersonidenter, søker),
                KeyValue(forrigePersonident, null)
            )
        }

            .produce(Topics.søkere, "endrer-personident-for-soker")
}

fun <V : Bufferable<V>> KStream<String, V?>.produce(
    topic: BufferableTopic<V>,
    name: String
) {
    this.processValues({ LogProduceNullable("Produserer til Topic", topic, false) }, name)
        .to(topic.name,  Produced.with(topic.keySerde, topic.valueSerde).withName("$name-buffered2"))
}


private val log: Logger = LoggerFactory.getLogger("secureLog")

internal class LogProduceNullable<K, V>(
    private val message: String,
    private val topic: Topic<V>,
    private val logValue: Boolean = false,
) : FixedKeyProcessor<K, V?, V?> {
    private lateinit var context: FixedKeyProcessorContext<K, V?>
    override fun init(ctxt: FixedKeyProcessorContext<K, V?>) = let { context = ctxt }
    override fun close() {}
    override fun process(record: FixedKeyRecord<K, V?>) {
        log.trace(
            message,
            StructuredArguments.kv("key", record.key()),
            StructuredArguments.kv("topic", topic.name),
            StructuredArguments.kv("partition", context.recordMetadata().get().partition()),
            if (logValue) StructuredArguments.kv("value", record.value()) else null,
        )
        context.forward(record)
    }
}
