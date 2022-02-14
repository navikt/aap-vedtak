package no.nav.aap.app

import no.nav.aap.app.kafka.KStreamsUML
import org.junit.jupiter.api.Test

internal class GenerateUML {
    @Test
    fun `generate topology UML`() {
        KStreamsUML.file(createTopology(), "../doc/topology.puml").also {
            println("Generated UML to ${it.absoluteFile}. Use in https://plantuml-editor.kkeisuke.dev")
        }
    }
}
