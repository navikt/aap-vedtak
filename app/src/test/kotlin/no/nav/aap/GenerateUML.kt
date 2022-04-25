package no.nav.aap

import no.nav.aap.app.Config
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.streamsBuilder
import no.nav.aap.kafka.streams.uml.KStreamsUML
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.streams.StreamsBuilder
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class GenerateUML {
    private companion object {
        private val log = LoggerFactory.getLogger("GenerateUML")
    }

    @Test
    fun `generate topology UML`() {
        val config = loadConfig<Config>()
        val topics = Topics(config.kafka)
        val tables = Tables(topics)
        val topology = StreamsBuilder().apply { streamsBuilder(topics, tables) }.build()

        KStreamsUML.create(topology).also {
            log.info("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
        }
    }
}
