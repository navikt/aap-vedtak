package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.testing.*
import no.nav.aap.app.modell.JsonPersonident
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toDto
import no.nav.aap.dto.*
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.state.KeyValueStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import no.nav.aap.avro.inntekter.v1.Inntekt as AvroInntekt
import no.nav.aap.avro.inntekter.v1.Inntekter as AvroInntekter
import no.nav.aap.avro.inntekter.v1.Response as AvroInntekterResponse
import no.nav.aap.avro.manuell.v1.LosningVurderingAvBeregningsdato as AvroLøsningVurderingAvBeregningsdato
import no.nav.aap.avro.manuell.v1.Losning_11_12_l1 as AvroLøsning_11_12_l1
import no.nav.aap.avro.manuell.v1.Losning_11_2 as AvroLøsning_11_2
import no.nav.aap.avro.manuell.v1.Losning_11_29 as AvroLøsning_11_29
import no.nav.aap.avro.manuell.v1.Losning_11_3 as AvroLøsning_11_3
import no.nav.aap.avro.manuell.v1.Losning_11_4_l2_l3 as AvroLøsning_11_4_l2_l3
import no.nav.aap.avro.manuell.v1.Losning_11_5 as AvroLøsning_11_5
import no.nav.aap.avro.manuell.v1.Losning_11_6 as AvroLøsning_11_6
import no.nav.aap.avro.manuell.v1.Manuell as AvroManuell
import no.nav.aap.avro.medlem.v1.ErMedlem as AvroErMedlem
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.medlem.v1.Response as AvroMedlemResponse
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal class ApiTest {

    @Test
    fun `søker får innvilget vedtak`() {
        withTestApp { mocks ->
            initializeTopics(mocks.kafka)

            søknadTopic.produce("123") {
                JsonSøknad(JsonPersonident("FNR", "123"), LocalDate.now().minusYears(40))
            }

            val medlemRequest = medlemOutputTopic.readValue()
            medlemTopic.produce("123") {
                medlemRequest.apply {
                    response = AvroMedlemResponse.newBuilder().setErMedlem(AvroErMedlem.JA).build()
                }
            }

            produserLøsning(key = "123", losning_11_3_manuell = AvroLøsning_11_3(true))
            produserLøsning(key = "123", losning_11_5_manuell = AvroLøsning_11_5(60))
            produserLøsning(key = "123", losning_11_6_manuell = AvroLøsning_11_6(true))
            produserLøsning(key = "123", losning_11_12_l1_manuell = AvroLøsning_11_12_l1(true))
            produserLøsning(key = "123", losning_11_29_manuell = AvroLøsning_11_29(true))
            produserLøsning(
                key = "123",
                losningVurderingAvBeregningsdato = AvroLøsningVurderingAvBeregningsdato(
                    LocalDate.of(2022, 1, 1)
                )
            )

            val inntektRequest = inntektOutputTopic.readValue()
            inntektTopic.produce("123") {
                inntektRequest.apply {
                    response = AvroInntekterResponse.newBuilder()
                        .setInntekter(
                            listOf(
                                AvroInntekt("321", request.fom.plusYears(2), 400000.0),
                                AvroInntekt("321", request.fom.plusYears(1), 400000.0),
                                AvroInntekt("321", request.fom, 400000.0)
                            )
                        )
                        .build()
                }
            }

            val søker = stateStore["123"]
            assertNotNull(søker)
            val actual = søker.toDto()
            assertNotNull(actual.saker.firstOrNull()?.vedtak) { "Saken mangler vedtak - $actual" }
            val søknadstidspunkt = actual.saker.first().vedtak!!.søknadstidspunkt
            val expected = DtoSøker(
                personident = "123",
                fødselsdato = LocalDate.now().minusYears(40),
                saker = listOf(
                    DtoSak(
                        tilstand = "VEDTAK_FATTET",
                        vurderingsdato = LocalDate.now(),
                        sakstyper = listOf(
                            DtoSakstype(
                                type = "STANDARD",
                                vilkårsvurderinger = listOf(
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_2",
                                        ledd = listOf("LEDD_1", "LEDD_2"),
                                        tilstand = "OPPFYLT_MASKINELT",
                                        måVurderesManuelt = false,
                                        løsning_11_2_maskinell = DtoLøsningParagraf_11_2("JA"),
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_3",
                                        ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_3_manuell = DtoLøsningParagraf_11_3(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_2", "LEDD_3"),
                                        tilstand = "IKKE_RELEVANT",
                                        måVurderesManuelt = false
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_5",
                                        ledd = listOf("LEDD_1", "LEDD_2"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_5_manuell = DtoLøsningParagraf_11_5(60)
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_6",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_6_manuell = DtoLøsningParagraf_11_6(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_12",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_12_ledd1_manuell = DtoLøsningParagraf_11_12_ledd1(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        paragraf = "PARAGRAF_11_29",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_29_manuell = DtoLøsningParagraf_11_29(true)
                                    )
                                )
                            )
                        ),
                        vurderingAvBeregningsdato = DtoVurderingAvBeregningsdato(
                            tilstand = "FERDIG",
                            løsningVurderingAvBeregningsdato = DtoLøsningVurderingAvBeregningsdato(
                                LocalDate.of(2022, 1, 1)
                            )
                        ),
                        vedtak = DtoVedtak(
                            innvilget = true,
                            inntektsgrunnlag = DtoInntektsgrunnlag(
                                beregningsdato = LocalDate.of(2022, 1, 1),
                                inntekterSiste3Kalenderår = listOf(
                                    DtoInntektsgrunnlagForÅr(
                                        år = Year.of(2021),
                                        inntekter = listOf(
                                            DtoInntekt(
                                                arbeidsgiver = "321",
                                                inntekstmåned = YearMonth.of(2021, 1),
                                                beløp = 400000.0
                                            )
                                        ),
                                        beløpFørJustering = 400000.0,
                                        beløpJustertFor6G = 400000.0,
                                        erBeløpJustertFor6G = false,
                                        grunnlagsfaktor = 3.819856
                                    ),
                                    DtoInntektsgrunnlagForÅr(
                                        år = Year.of(2020),
                                        inntekter = listOf(
                                            DtoInntekt(
                                                arbeidsgiver = "321",
                                                inntekstmåned = YearMonth.of(2020, 1),
                                                beløp = 400000.0
                                            )
                                        ),
                                        beløpFørJustering = 400000.0,
                                        beløpJustertFor6G = 400000.0,
                                        erBeløpJustertFor6G = false,
                                        grunnlagsfaktor = 3.966169
                                    ),
                                    DtoInntektsgrunnlagForÅr(
                                        år = Year.of(2019),
                                        inntekter = listOf(
                                            DtoInntekt(
                                                arbeidsgiver = "321",
                                                inntekstmåned = YearMonth.of(2019, 1),
                                                beløp = 400000.0
                                            )
                                        ),
                                        beløpFørJustering = 400000.0,
                                        beløpJustertFor6G = 400000.0,
                                        erBeløpJustertFor6G = false,
                                        grunnlagsfaktor = 4.04588
                                    )
                                ),
                                fødselsdato = LocalDate.now().minusYears(40),
                                sisteKalenderår = Year.of(2021),
                                grunnlagsfaktor = 3.943968
                            ),
                            søknadstidspunkt = søknadstidspunkt,
                            vedtaksdato = LocalDate.now(),
                            virkningsdato = LocalDate.now()
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
            manuellTopic = kafka.inputAvroTopic("aap.manuell.v1")
            inntektTopic = kafka.inputAvroTopic("aap.inntekter.v1")
            inntektOutputTopic = kafka.outputAvroTopic("aap.inntekter.v1")
            søkerOutputTopic = kafka.outputAvroTopic("aap.sokere.v1")
            stateStore = kafka.getKeyValueStore(SØKERE_STORE_NAME)
        }

        inline fun <reified T> TestApplicationResponse.parseBody(): T = objectMapper.readValue(content!!)

        private val objectMapper = jacksonObjectMapper().apply { registerModule(JavaTimeModule()) }

        private lateinit var søknadTopic: TestInputTopic<String, JsonSøknad>
        private lateinit var medlemTopic: TestInputTopic<String, AvroMedlem>
        private lateinit var medlemOutputTopic: TestOutputTopic<String, AvroMedlem>
        private lateinit var manuellTopic: TestInputTopic<String, AvroManuell>
        private lateinit var inntektTopic: TestInputTopic<String, AvroInntekter>
        private lateinit var inntektOutputTopic: TestOutputTopic<String, AvroInntekter>
        private lateinit var søkerOutputTopic: TestOutputTopic<String, AvroSøker>
        private lateinit var stateStore: KeyValueStore<String, AvroSøker>
    }

    private fun produserLøsning(
        key: String,
        losning_11_2_manuell: AvroLøsning_11_2? = null,
        losning_11_3_manuell: AvroLøsning_11_3? = null,
        losning_11_4_l2_l3_manuell: AvroLøsning_11_4_l2_l3? = null,
        losning_11_5_manuell: AvroLøsning_11_5? = null,
        losning_11_6_manuell: AvroLøsning_11_6? = null,
        losning_11_12_l1_manuell: AvroLøsning_11_12_l1? = null,
        losning_11_29_manuell: AvroLøsning_11_29? = null,
        losningVurderingAvBeregningsdato: AvroLøsningVurderingAvBeregningsdato? = null
    ) {
        manuellTopic.produce(key) {
            AvroManuell(
                losning_11_2_manuell,
                losning_11_3_manuell,
                losning_11_4_l2_l3_manuell,
                losning_11_5_manuell,
                losning_11_6_manuell,
                losning_11_12_l1_manuell,
                losning_11_29_manuell,
                losningVurderingAvBeregningsdato
            )
        }
    }
}

private fun <R> withTestApp(test: TestApplicationEngine.(mocks: Mocks) -> R): R = Mocks().use { mocks ->
    val externalConfig = mapOf(
        "KAFKA_STREAMS_APPLICATION_ID" to "vedtak",
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
