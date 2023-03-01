package vedtak

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.aap.kafka.streams.v2.config.StreamsConfig
import no.nav.aap.kafka.streams.v2.test.KStreamsMock
import org.apache.kafka.clients.producer.MockProducer
import org.junit.jupiter.api.Test
import java.io.File

internal class TopologyDiagram {

    @Test
    fun `mermaid diagram`() {
        val kafka = KStreamsMock().apply {
            connect(
                topology = topology(SimpleMeterRegistry(), MockProducer(), true),
                config = StreamsConfig("", ""),
                registry = SimpleMeterRegistry(),
            )
        }

        val mermaid = kafka.visulize().mermaid().generateDiagram()
        File("../docs/topology.mmd").apply { writeText(mermaid) }
    }
}
