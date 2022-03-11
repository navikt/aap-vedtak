package no.nav.aap.app

import com.nimbusds.jwt.SignedJWT
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.kafka.KtorKafkaMetrics
import no.nav.aap.app.kafka.*
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class Mocks : AutoCloseable {
    val azure = AzureMock().apply { start() }
    val kafka = KStreamsMock()

    override fun close() {
        azure.close()
    }
}

class AzureMock(private val server: MockOAuth2Server = MockOAuth2Server()) {
    fun wellKnownUrl(): String = server.wellKnownUrl("azure").toString()
    fun issueAzureToken(): SignedJWT = server.issueToken(issuerId = "azure", audience = "vedtak")
    fun start() = server.start()
    fun close() = server.shutdown()
}

class KStreamsMock : Kafka {
    lateinit var driver: TopologyTestDriver
    lateinit var config: KafkaConfig

    override fun start(topology: Topology, kafkaConfig: KafkaConfig, registry: MeterRegistry) {
        driver = TopologyTestDriver(topology, kafkaConfig.consumer + kafkaConfig.producer + testConfig)
        KtorKafkaMetrics(registry, driver::metrics)
        config = kafkaConfig
    }

    internal val schemaRegistryUrl: String by lazy { "mock://schema-registry/${UUID.randomUUID()}" }

    override fun <V : Any> createProducer(topic: Topic<String, V>): Producer<String, V> =
        object : MockProducer<String, V>(true, topic.keySerde.serializer(), topic.valueSerde.serializer()) {
            override fun send(record: ProducerRecord<String, V>): Future<RecordMetadata> {
                inputAvroTopic<SpecificRecord>(topic.name).pipeInput(record.key(), record.value() as SpecificRecord)
                return object : Future<RecordMetadata> {
                    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun isCancelled(): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun isDone(): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun get() =
                        RecordMetadata(TopicPartition(topic.name, 0), 0, 0, 0, 0, 0)

                    override fun get(timeout: Long, unit: TimeUnit): RecordMetadata {
                        TODO("Not yet implemented")
                    }
                }
            }
        }

    override fun <V : Any> createConsumer(topic: Topic<String, V>): Consumer<String, V> = MockConsumer(EARLIEST)
    override fun <V> getStore(name: String): ReadOnlyKeyValueStore<String, V> = driver.getKeyValueStore(name)
    override fun close() = driver.close().also { MockSchemaRegistry.dropScope(schemaRegistryUrl) }
    override fun state(): KafkaStreams.State = KafkaStreams.State.RUNNING
    override fun started(): Boolean = true

    inline fun <reified V : Any> inputJsonTopic(name: String): TestInputTopic<String, V> =
        driver.createInputTopic(name, Serdes.StringSerde().serializer(), JsonSerde(V::class).serializer())

    fun <V : SpecificRecord> inputAvroTopic(name: String): TestInputTopic<String, V> {
        val serde = SpecificAvroSerde<V>().apply { configure(avroConfig, false) }
        return driver.createInputTopic(name, Serdes.String().serializer(), serde.serializer())
    }

    fun <V : SpecificRecord> outputAvroTopic(name: String): TestOutputTopic<String, V> {
        val serde = SpecificAvroSerde<V>().apply { configure(avroConfig, false) }
        return driver.createOutputTopic(name, Serdes.String().deserializer(), serde.deserializer())
    }

    inline fun <reified V : Any> getKeyValueStore(storeName: String): KeyValueStore<String, V> =
        driver.getKeyValueStore(storeName)

    private val testConfig = Properties().apply {
        this[StreamsConfig.STATE_DIR_CONFIG] = "build/kafka-streams/state"
        this[StreamsConfig.MAX_TASK_IDLE_MS_CONFIG] = StreamsConfig.MAX_TASK_IDLE_MS_DISABLED
    }

    private val avroConfig: Map<String, String>
        get() = mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrl)
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) = pipeInput(key, value())
