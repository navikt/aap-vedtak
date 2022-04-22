package no.nav.aap

import no.nav.aap.app.config.Config
import no.nav.aap.app.createTopology
import no.nav.aap.app.kafka.KStreamsUML
import no.nav.aap.app.kafka.Topics
import no.nav.aap.ktor.config.loadConfig
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class GenerateUML {
    private companion object{
        private val log = LoggerFactory.getLogger("GenerateUML")
    }

    @Test
    fun `generate topology UML`() {
        val config = loadConfig<Config>()
        val topics = Topics(config.kafka)
        val topology = createTopology(topics)

        KStreamsUML.create(topology).also {
            log.info("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
        }
    }
}
