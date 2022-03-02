package no.nav.aap

import no.nav.aap.app.config.Config
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

        KStreamsUML.create(topology).also {
            println("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
        }
    }
}
