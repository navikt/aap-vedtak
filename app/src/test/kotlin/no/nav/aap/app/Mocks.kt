package no.nav.aap.app

import com.nimbusds.jwt.SignedJWT
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.JsonSerde
import no.nav.aap.app.kafka.Kafka
import no.nav.aap.app.kafka.KafkaConfig
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import java.util.*

class Mocks() : AutoCloseable {
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

class KStreamsMock() : Kafka {
    lateinit var driver: TopologyTestDriver
    lateinit var config: KafkaConfig

    internal val schemaRegistryUrl: String by lazy { "mock://schema-registry/${UUID.randomUUID()}" }

    override fun create(topology: Topology) {
        val testConfig = Properties().apply {
            putAll(config.consumer)
            putAll(config.producer)
            this[StreamsConfig.STATE_DIR_CONFIG] = "build/kafka-streams/state"
            this[StreamsConfig.MAX_TASK_IDLE_MS_CONFIG] = StreamsConfig.MAX_TASK_IDLE_MS_DISABLED
        }

        driver = TopologyTestDriver(topology, testConfig)
    }

    override fun start() {}
    override fun healthy(): Boolean = true
    override fun <K, V> getStore(name: String): ReadOnlyKeyValueStore<K, V> = driver.getKeyValueStore(name)
    override fun close() = driver.close().also { MockSchemaRegistry.dropScope(schemaRegistryUrl) }

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

    inline fun <reified K : Any, reified V : Any> getKeyValueStore(storeName: String): KeyValueStore<K, V> =
        driver.getKeyValueStore(storeName)

    private val avroConfig: Map<String, String>
        get() = mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrl)
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) = pipeInput(key, value())
