package no.nav.aap

import no.nav.aap.app.streamsBuilder
import no.nav.aap.kafka.streams.uml.KStreamsUML
import org.apache.kafka.streams.StreamsBuilder
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables

internal class GenerateUML {
    private companion object {
        private val log = LoggerFactory.getLogger("GenerateUML")
    }

    @Test
    fun `generate topology UML`() {
        EnvironmentVariables(containerProperties()).execute {
            val topology = StreamsBuilder().apply { streamsBuilder() }.build()

            KStreamsUML.create(topology).also {
                log.info("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
            }
        }
    }

    private fun containerProperties(): Map<String, String> = mapOf(
        "KAFKA_STREAMS_APPLICATION_ID" to "vedtak",
        "KAFKA_BROKERS" to "mock://kafka",
        "KAFKA_TRUSTSTORE_PATH" to "",
        "KAFKA_KEYSTORE_PATH" to "",
        "KAFKA_CREDSTORE_PASSWORD" to "",
        "KAFKA_CLIENT_ID" to "vedtak",
        "KAFKA_GROUP_ID" to "vedtak-1",
        "KAFKA_SCHEMA_REGISTRY" to "mock://schema-registry",
        "KAFKA_SCHEMA_REGISTRY_USER" to "",
        "KAFKA_SCHEMA_REGISTRY_PASSWORD" to "",
    )
}
