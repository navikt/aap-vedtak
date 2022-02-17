package no.nav.aap

import no.nav.aap.app.Config
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.createTopology
import no.nav.aap.app.kafka.KStreamsUML
import no.nav.aap.app.kafka.Topics
import org.junit.jupiter.api.Test

internal class GenerateUML {

    @Test
    fun `generate topology UML`() {
        val config = loadConfig<Config>()
        val topics = Topics(config.kafka)
        val topology = createTopology(topics)
        val filePath = "../doc/topology.puml"

        KStreamsUML.create(topology, filePath).also {
            println("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
        }
    }
}
