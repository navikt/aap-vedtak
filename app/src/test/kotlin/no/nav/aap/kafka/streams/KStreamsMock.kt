package no.nav.aap.kafka.streams

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.aap.app.kafka.KafkaConfig
import no.nav.aap.app.kafka.json.JsonSerde
import no.nav.aap.app.kafka.streams.Kafka
import no.nav.aap.app.kafka.streams.KafkaStreamsFactory
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.state.KeyValueStore
import java.util.*

class KStreamsMock : Kafka {
    lateinit var driver: TopologyTestDriver
    lateinit var config: KafkaConfig

    override fun createKafkaStream(topology: Topology, config: KafkaConfig) {
        this.config = config
        this.driver = TopologyTestDriver(topology, Properties().apply {
            putAll(KafkaStreamsFactory().streamsProperties(config))
            put(StreamsConfig.STATE_DIR_CONFIG, "build/kafka-streams/state")
            put(StreamsConfig.MAX_TASK_IDLE_MS_CONFIG, StreamsConfig.MAX_TASK_IDLE_MS_DISABLED)
        })
    }

    override fun start() {}
    override fun close() = driver.close()

    inline fun <reified V : Any> inputJsonTopic(name: String): TestInputTopic<String, V> =
        driver.createInputTopic(name, Serdes.StringSerde().serializer(), JsonSerde(V::class).serializer())

    inline fun <reified V : Any> outputJsonTopic(name: String): TestOutputTopic<String, V> =
        driver.createOutputTopic(name, Serdes.StringSerde().deserializer(), JsonSerde(V::class).deserializer())

    fun <V : SpecificRecord> inputAvroTopic(name: String): TestInputTopic<String, V> {
        val serde = SpecificAvroSerde<V>().apply { configure(avroConfig, false) }
        return driver.createInputTopic(name, Serdes.String().serializer(), serde.serializer())
    }

    fun <V : SpecificRecord> outputAvroTopic(name: String): TestOutputTopic<String, V> {
        val serde = SpecificAvroSerde<V>().apply { configure(avroConfig, false) }
        return driver.createOutputTopic(name, Serdes.String().deserializer(), serde.deserializer())
    }

    inline fun <reified K : Any, reified V : Any> getKeyValueStore(storeName: String): KeyValueStore<K, V> =
        driver.getKeyValueStore(storeName)

    private val avroConfig: Map<String, String>
        get() = mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrl)
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) {
    pipeInput(key, value())
}

fun <T> KeyValueStore<String, T>.getAllValues(): List<T> = all().use { iterator ->
    iterator.asSequence().map(KeyValue<String, T>::value).toList()
}

fun <T> KeyValueStore<String, T>.getAll(): Map<String, T> = all().use { iterator ->
    iterator.asSequence().associateBy(KeyValue<String, T>::key, KeyValue<String, T>::value)
}
