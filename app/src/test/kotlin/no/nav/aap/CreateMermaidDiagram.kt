package no.nav.aap

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.aap.app.topology
import no.nav.aap.kafka.streams.topology.Mermaid
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.io.File

internal class CreateMermaidDiagram {
    @Test
    fun `generate mermaid diagram`() {
        EnvironmentVariables(containerProperties()).execute {
            val registry = SimpleMeterRegistry()
            val topology = topology(registry)
            val flowchart = Mermaid.graph("Vedtak", topology)
            val mermaidFlowcharMarkdown = markdown(flowchart)
            File("../doc/topology.md").apply { writeText(mermaidFlowcharMarkdown) }
            File("../doc/topology.mermaid").apply { writeText(flowchart) }
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

fun markdown(mermaid: String) = """
```mermaid
$mermaid
```
"""
