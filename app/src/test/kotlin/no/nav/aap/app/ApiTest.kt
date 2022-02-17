package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.aap.app.modell.JsonPersonident
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toDto
import no.nav.aap.avro.medlem.v1.ErMedlem
import no.nav.aap.dto.DtoLøsningParagraf_11_2
import no.nav.aap.dto.DtoSak
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.state.KeyValueStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker

internal class ApiTest {

    @Test
    fun `GET alle saker returns 200 OK`() {
        withTestApp { mocks ->
            initializeTopics(mocks.kafka)

            søknadTopic.produce("11111111111") {
                JsonSøknad(
                    ident = JsonPersonident("FNR", "11111111111"),
                    fødselsdato = LocalDate.of(1990, 1, 1)
                )
            }

            with(handleRequest(HttpMethod.Get, "/api/sak") {
                val token = mocks.azure.issueAzureToken()
                addHeader("Authorization", "Bearer ${token.serialize()}")
            }) {
                val expected = listOf(
                    FrontendSak(
                        personident = "11111111111",
                        fødselsdato = LocalDate.of(1990, 1, 1),
                        tilstand = "SØKNAD_MOTTATT",
                        vilkårsvurderinger = listOf(
                            FrontendVilkårsvurdering(
                                paragraf = "PARAGRAF_11_2",
                                ledd = listOf("LEDD_1", "LEDD_2"),
                                tilstand = "SØKNAD_MOTTATT",
                                harÅpenOppgave = false
                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_3",
//                                ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_4",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "OPPFYLT",
//                                harÅpenOppgave = false
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_4",
//                                ledd = listOf("LEDD_2", "LEDD_3"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_5",
//                                ledd = listOf("LEDD_1", "LEDD_2"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_6",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_12",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_29",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            )
                        )
                    )
                )
                assertEquals(response.status(), HttpStatusCode.OK)
                assertEquals(expected, response.parseBody<List<FrontendSak>>())
            }
        }
    }

    @Test
    fun `GET neste sak returns 200 OK`() {
        withTestApp { mocks ->
            initializeTopics(mocks.kafka)

            søknadTopic.produce("11111111111") {
                JsonSøknad(
                    ident = JsonPersonident("FNR", "11111111111"),
                    fødselsdato = LocalDate.of(1990, 1, 1)
                )
            }

            with(handleRequest(HttpMethod.Get, "/api/sak/neste") {
                val token = mocks.azure.issueAzureToken()
                addHeader("Authorization", "Bearer ${token.serialize()}")
            }) {
                val expected = FrontendSak(
                    personident = "11111111111",
                    fødselsdato = LocalDate.of(1990, 1, 1),
                    tilstand = "SØKNAD_MOTTATT",
                    vilkårsvurderinger = listOf(
                        FrontendVilkårsvurdering(
                            paragraf = "PARAGRAF_11_2",
                            ledd = listOf("LEDD_1", "LEDD_2"),
                            tilstand = "SØKNAD_MOTTATT",
                            harÅpenOppgave = false
                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_3",
//                            ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
//                            tilstand = "SØKNAD_MOTTATT",
//                            harÅpenOppgave = true
//                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_4",
//                            ledd = listOf("LEDD_1"),
//                            tilstand = "OPPFYLT",
//                            harÅpenOppgave = false
//                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_4",
//                            ledd = listOf("LEDD_2", "LEDD_3"),
//                            tilstand = "SØKNAD_MOTTATT",
//                            harÅpenOppgave = true
//                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_5",
//                            ledd = listOf("LEDD_1", "LEDD_2"),
//                            tilstand = "SØKNAD_MOTTATT",
//                            harÅpenOppgave = true
//                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_6",
//                            ledd = listOf("LEDD_1"),
//                            tilstand = "SØKNAD_MOTTATT",
//                            harÅpenOppgave = true
//                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_12",
//                            ledd = listOf("LEDD_1"),
//                            tilstand = "SØKNAD_MOTTATT",
//                            harÅpenOppgave = true
//                        ),
//                        FrontendVilkårsvurdering(
//                            paragraf = "PARAGRAF_11_29",
//                            ledd = listOf("LEDD_1"),
//                            tilstand = "SØKNAD_MOTTATT",
//                            harÅpenOppgave = true
//                        )
                    )
                )
                assertEquals(response.status(), HttpStatusCode.OK)
                assertEquals(expected, response.parseBody<FrontendSak>())
            }
        }
    }

    @Test
    fun `GET saker for person returns 200 OK`() {
        withTestApp { mocks ->
            initializeTopics(mocks.kafka)

            søknadTopic.produce("11111111111") {
                JsonSøknad(
                    ident = JsonPersonident("FNR", "11111111111"),
                    fødselsdato = LocalDate.of(1990, 1, 1)
                )
            }

            with(handleRequest(HttpMethod.Get, "/api/sak/11111111111") {
                val token = mocks.azure.issueAzureToken()
                addHeader("Authorization", "Bearer ${token.serialize()}")
            }) {
                val expected = listOf(
                    FrontendSak(
                        personident = "11111111111",
                        fødselsdato = LocalDate.of(1990, 1, 1),
                        tilstand = "SØKNAD_MOTTATT",
                        vilkårsvurderinger = listOf(
                            FrontendVilkårsvurdering(
                                paragraf = "PARAGRAF_11_2",
                                ledd = listOf("LEDD_1", "LEDD_2"),
                                tilstand = "SØKNAD_MOTTATT",
                                harÅpenOppgave = false
                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_3",
//                                ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_4",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "OPPFYLT",
//                                harÅpenOppgave = false
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_4",
//                                ledd = listOf("LEDD_2", "LEDD_3"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_5",
//                                ledd = listOf("LEDD_1", "LEDD_2"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_6",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_12",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            ),
//                            FrontendVilkårsvurdering(
//                                paragraf = "PARAGRAF_11_29",
//                                ledd = listOf("LEDD_1"),
//                                tilstand = "SØKNAD_MOTTATT",
//                                harÅpenOppgave = true
//                            )
                        )
                    )
                )
                assertEquals(response.status(), HttpStatusCode.OK)
                assertEquals(expected, response.parseBody<List<FrontendSak>>())
            }
        }
    }

    @Test
    fun `søker med medlem`() {
        withTestApp { mocks ->
            initializeTopics(mocks.kafka)

            søknadTopic.produce("123") {
                JsonSøknad(JsonPersonident("FNR", "123"), LocalDate.now().minusYears(40))
            }

            val medlemBehov = medlemOutputTopic.readValue()
            assertNull(medlemBehov.response)
            assertNotNull(medlemBehov.request)
            assertNotNull(medlemBehov.request.mottattDato)
            assertEquals(false, medlemBehov.request.arbeidetUtenlands)
            assertEquals("AAP", medlemBehov.request.ytelse)

            // midlertidig
            val medlemLøsning = medlemOutputTopic.readValue()
            assertNotNull(medlemLøsning.response)
            assertEquals(ErMedlem.JA, medlemLøsning.response.erMedlem)
            assertEquals("flotters", medlemLøsning.response.begrunnelse)

//            medlemTopic.produce(key = "NO THE ID WE WANT") {
//                medlemBehov.apply {
//                    response = Response.newBuilder()
//                        .setErMedlem(ErMedlem.JA)
//                        .build()
//                }
//            }

            println(søkerOutputTopic.readValuesToList())

            val søker = stateStore["123"]
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
                                tilstand = "OPPFYLT_MASKINELT",
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

    companion object {
        internal fun initializeTopics(kafka: KStreamsMock) {
            søknadTopic = kafka.inputJsonTopic("aap.aap-soknad-sendt.v1")
            medlemTopic = kafka.inputAvroTopic("aap.medlem.v1")
            medlemOutputTopic = kafka.outputAvroTopic("aap.medlem.v1")
            søkerOutputTopic = kafka.outputAvroTopic("aap.sokere.v1")
            stateStore = kafka.getKeyValueStore("soker-store")
        }

        inline fun <reified T> TestApplicationResponse.parseBody(): T = objectMapper.readValue(content!!)

        private val objectMapper = jacksonObjectMapper().apply { registerModule(JavaTimeModule()) }

        private lateinit var søknadTopic: TestInputTopic<String, JsonSøknad>
        private lateinit var medlemTopic: TestInputTopic<String, AvroMedlem>
        private lateinit var medlemOutputTopic: TestOutputTopic<String, AvroMedlem>
        private lateinit var søkerOutputTopic: TestOutputTopic<String, AvroSøker>
        private lateinit var stateStore: KeyValueStore<String, AvroSøker>
    }
}

fun <R> withTestApp(test: TestApplicationEngine.(mocks: Mocks) -> R): R = Mocks().use { mocks ->
    val externalConfig = mapOf(
        "AZURE_OPENID_CONFIG_ISSUER" to "azure",
        "AZURE_APP_WELL_KNOWN_URL" to mocks.azure.wellKnownUrl(),
        "AZURE_APP_CLIENT_ID" to "vedtak",
        "KAFKA_BROKERS" to "mock://kafka",
        "KAFKA_TRUSTSTORE_PATH" to "",
        "KAFKA_SECURITY_ENABLED" to "false",
        "KAFKA_KEYSTORE_PATH" to "",
        "KAFKA_CREDSTORE_PASSWORD" to "",
        "KAFKA_CLIENT_ID" to "vedtak",
        "KAFKA_GROUP_ID" to "vedtak-1",
        "KAFKA_SCHEMA_REGISTRY" to mocks.kafka.schemaRegistryUrl,
        "KAFKA_SCHEMA_REGISTRY_USER" to "",
        "KAFKA_SCHEMA_REGISTRY_PASSWORD" to "",
    )

    return EnvironmentVariables(externalConfig).execute<R> {
        withTestApplication(
            { server(mocks.kafka) },
            { test(mocks) }
        )
    }
}
