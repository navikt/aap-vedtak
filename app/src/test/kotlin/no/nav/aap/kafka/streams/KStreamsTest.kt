package no.nav.aap.kafka.streams

import io.ktor.server.testing.*
import no.nav.aap.app.createTopology
import no.nav.aap.app.modell.KafkaPersonident
import no.nav.aap.app.modell.KafkaSøknad
import no.nav.aap.app.streamsApp
import no.nav.aap.app.toDto
import no.nav.aap.avro.medlem.v1.ErMedlem
import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.avro.medlem.v1.Response
import no.nav.aap.dto.DtoLøsningParagraf_11_2
import no.nav.aap.dto.DtoSak
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.util.*
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker

class KStreamsTest {

    @Test
    fun `generate topology UML`() {
        KStreamsUML.file(createTopology(), "../doc/topology.puml").also {
            println("Generated UML to ${it.absoluteFile}. Use in https://plantuml-editor.kkeisuke.dev")
        }
    }

    @Test
    fun `søker med medlem`() {
        withTestApp { kafka ->
            val søknadTopic = kafka.inputJsonTopic<KafkaSøknad>("aap.aap-soknad-sendt.v1")
            val medlemTopic = kafka.inputAvroTopic<Medlem>("aap.medlem.v1")
            val medlemOutputTopic = kafka.outputAvroTopic<Medlem>("aap.medlem.v1")
            val søkerOutputTopic = kafka.outputAvroTopic<no.nav.aap.avro.vedtak.v1.Søker>("aap.sokere.v1")

            søknadTopic.produce("123") {
                KafkaSøknad(KafkaPersonident("FNR", "123"), LocalDate.now().minusYears(40))
            }

            val medlemBehov = medlemOutputTopic.readValue()
            assertNull(medlemBehov.response)
            assertNotNull(medlemBehov.request)
            assertNotNull(medlemBehov.request.mottattDato)
            assertEquals(false, medlemBehov.request.arbeidetUtenlands)
            assertEquals("AAP", medlemBehov.request.ytelse)

            medlemTopic.produce(key = "NO THE ID WE WANT") {
                medlemBehov.apply {
                    response = Response.newBuilder()
                        .setErMedlem(ErMedlem.JA)
                        .build()
                }
            }

            println(søkerOutputTopic.readValuesToList())

            val søker = kafka.getKeyValueStore<String, AvroSøker>("ktable-sokere").get("123")
            assertNotNull(søker)
            val actual = søker.toDto()
            val expected = DtoSøker(
                personident = "123",
                fødselsdato = LocalDate.now().minusYears(40),
                saker = listOf(
                    DtoSak(
                        tilstand = "BEREGN_INNTEKT", // todo: SØKNAD_MOTTATT når fler vilkår er implementert
                        vurderingsdato = LocalDate.now(),
                        vilkårsvurderinger = listOf(
                            DtoVilkårsvurdering(
                                paragraf = "PARAGRAF_11_2",
                                ledd = listOf("LEDD_1", "LEDD_2"),
                                tilstand = "OPPFYLT",
                                løsning_11_2_maskinell = DtoLøsningParagraf_11_2("JA"),
                                løsning_11_2_manuell = null,
                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_3",
//                                ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                null, null
//                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_4",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "OPPFYLT",
//                                null, null
//                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_4",
//                                ledd = listOf("LEDD_2", "LEDD_3"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                null, null
//                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_5",
//                                ledd = listOf("LEDD_1", "LEDD_2"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                null, null
//                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_6",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                null, null
//                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_12",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                null, null
//                            ),
//                            DtoVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_29",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                null, null
//                            )
                        )
                    )
                )
            )

            assertEquals(expected, actual)
        }
    }
}

private fun <R> withTestApp(test: TestApplicationEngine.(kafka: KStreamsMock) -> R): R {
    val externalConfig = mapOf(
        "AZURE_OPENID_CONFIG_ISSUER" to "",
        "AZURE_APP_WELL_KNOWN_URL" to "http:://localhost:123",
        "AZURE_APP_CLIENT_ID" to "",
        "KAFKA_BROKERS" to "mock://kafka",
        "KAFKA_TRUSTSTORE_PATH" to "",
        "KAFKA_SECURITY_ENABLED" to "false",
        "KAFKA_KEYSTORE_PATH" to "",
        "KAFKA_CREDSTORE_PASSWORD" to "",
        "KAFKA_CLIENT_ID" to "vedtak",
        "KAFKA_GROUP_ID" to "vedtak-1",
        "KAFKA_SCHEMA_REGISTRY" to "mock://schema-registry-${UUID.randomUUID()}",
        "KAFKA_SCHEMA_REGISTRY_USER" to "",
        "KAFKA_SCHEMA_REGISTRY_PASSWORD" to "",
    )

    return EnvironmentVariables(externalConfig).execute<R> {
        val kafka = KStreamsMock()
        withTestApplication(
            { streamsApp(kafka) },
            { test(kafka) }
        )
    }
}