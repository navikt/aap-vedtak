package no.nav.aap

import no.nav.aap.app.Config
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.createTopology
import no.nav.aap.app.kafka.KStreamsUML
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class GenerateUML {

    @Test
    fun `generate topology UML`() {
        val config = loadConfig<Config>()

        assertNotNull(config)

        KStreamsUML.create(createTopology(config.kafka), "../doc/topology.puml").also {
            println("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
        }
    }
}
