package no.nav.aap.app

import io.ktor.server.testing.*
import no.nav.aap.app.kafka.SØKERE_STORE_NAME
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.InntekterKafkaDto.Response.Inntekt
import no.nav.aap.app.modell.JsonPersonident
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toDto
import no.nav.aap.dto.*
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.streams.TestInputTopic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
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
import no.nav.aap.avro.medlem.v1.Response as AvroMedlemResponse
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal class ApiTest {

    @Test
    fun `søker får innvilget vedtak`() {
        withTestApp { mocks ->
            val config = loadConfig<Config>()
            val topics = Topics(config.kafka)

            val søknadTopic = mocks.kafka.inputTopic(topics.søknad)
            val medlemTopic = mocks.kafka.inputTopic(topics.medlem)
            val medlemOutputTopic = mocks.kafka.outputTopic(topics.medlem)
            val manuellTopic = mocks.kafka.inputTopic(topics.manuell)
            val inntektTopic = mocks.kafka.inputTopic(topics.inntekter)
            val inntektOutputTopic = mocks.kafka.outputTopic(topics.inntekter)
            val stateStore = mocks.kafka.getStore<AvroSøker>(SØKERE_STORE_NAME)

            søknadTopic.produce("123") {
                JsonSøknad(JsonPersonident("FNR", "123"), LocalDate.now().minusYears(40))
            }

            val medlemRequest = medlemOutputTopic.readValue()
            medlemTopic.produce("123") {
                medlemRequest.apply {
                    response = AvroMedlemResponse.newBuilder().setErMedlem(AvroErMedlem.JA).build()
                }
            }

            manuellTopic.produserLøsning(key = "123", losning_11_3_manuell = AvroLøsning_11_3(true))
            manuellTopic.produserLøsning(key = "123", losning_11_5_manuell = AvroLøsning_11_5(60))
            manuellTopic.produserLøsning(key = "123", losning_11_6_manuell = AvroLøsning_11_6(true))
            manuellTopic.produserLøsning(key = "123", losning_11_12_l1_manuell = AvroLøsning_11_12_l1(true))
            manuellTopic.produserLøsning(key = "123", losning_11_29_manuell = AvroLøsning_11_29(true))
            manuellTopic.produserLøsning(
                key = "123",
                losningVurderingAvBeregningsdato = AvroLøsningVurderingAvBeregningsdato(LocalDate.of(2022, 1, 1))
            )

            val inntekter: InntekterKafkaDto = inntektOutputTopic.readValue()
            inntektTopic.produce("123") {
                inntekter.copy(
                    response = InntekterKafkaDto.Response(
                        listOf(
                            Inntekt("321", inntekter.request.fom.plusYears(2), 400000.0),
                            Inntekt("321", inntekter.request.fom.plusYears(1), 400000.0),
                            Inntekt("321", inntekter.request.fom, 400000.0),
                        )
                    )
                )
            }

            val søker = stateStore["123"]
            assertNotNull(søker)
            val actual = søker.toDto()
            assertNotNull(actual.saker.firstOrNull()?.vedtak) { "Saken mangler vedtak - $actual" }
            val søknadstidspunkt = actual.saker.first().søknadstidspunkt

            fun vilkårsvurderingsid(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].vilkårsvurderingsid

            val expected = DtoSøker(
                personident = "123",
                fødselsdato = LocalDate.now().minusYears(40),
                saker = listOf(
                    DtoSak(
                        saksid = actual.saker.first().saksid,
                        tilstand = "VEDTAK_FATTET",
                        vurderingsdato = LocalDate.now(),
                        sakstyper = listOf(
                            DtoSakstype(
                                type = "STANDARD",
                                aktiv = true,
                                vilkårsvurderinger = listOf(
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(0),
                                        paragraf = "PARAGRAF_11_2",
                                        ledd = listOf("LEDD_1", "LEDD_2"),
                                        tilstand = "OPPFYLT_MASKINELT",
                                        måVurderesManuelt = false,
                                        løsning_11_2_maskinell = DtoLøsningParagraf_11_2("JA"),
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(1),
                                        paragraf = "PARAGRAF_11_3",
                                        ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_3_manuell = DtoLøsningParagraf_11_3(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(2),
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(3),
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_2", "LEDD_3"),
                                        tilstand = "IKKE_RELEVANT",
                                        måVurderesManuelt = false
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(4),
                                        paragraf = "PARAGRAF_11_5",
                                        ledd = listOf("LEDD_1", "LEDD_2"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_5_manuell = DtoLøsningParagraf_11_5(60)
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(5),
                                        paragraf = "PARAGRAF_11_6",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_6_manuell = DtoLøsningParagraf_11_6(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(6),
                                        paragraf = "PARAGRAF_11_12",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        måVurderesManuelt = false,
                                        løsning_11_12_ledd1_manuell = DtoLøsningParagraf_11_12_ledd1(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(7),
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
                        søknadstidspunkt = søknadstidspunkt,
                        vedtak = DtoVedtak(
                            vedtaksid = actual.saker.first().vedtak!!.vedtaksid,
                            innvilget = true,
                            inntektsgrunnlag = DtoInntektsgrunnlag(
                                beregningsdato = LocalDate.of(2022, 1, 1),
                                inntekterSiste3Kalenderår = listOf(
                                    DtoInntekterForBeregning(
                                        inntekter = listOf(
                                            DtoInntekt(
                                                arbeidsgiver = "321",
                                                inntekstmåned = YearMonth.of(2021, 1),
                                                beløp = 400000.0
                                            )
                                        ),
                                        inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
                                            år = Year.of(2021),
                                            beløpFørJustering = 400000.0,
                                            beløpJustertFor6G = 400000.0,
                                            erBeløpJustertFor6G = false,
                                            grunnlagsfaktor = 3.819856
                                        )
                                    ),
                                    DtoInntekterForBeregning(
                                        inntekter = listOf(
                                            DtoInntekt(
                                                arbeidsgiver = "321",
                                                inntekstmåned = YearMonth.of(2020, 1),
                                                beløp = 400000.0
                                            )
                                        ),
                                        inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
                                            år = Year.of(2020),
                                            beløpFørJustering = 400000.0,
                                            beløpJustertFor6G = 400000.0,
                                            erBeløpJustertFor6G = false,
                                            grunnlagsfaktor = 3.966169
                                        )
                                    ),
                                    DtoInntekterForBeregning(
                                        inntekter = listOf(
                                            DtoInntekt(
                                                arbeidsgiver = "321",
                                                inntekstmåned = YearMonth.of(2019, 1),
                                                beløp = 400000.0
                                            )
                                        ),
                                        inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
                                            år = Year.of(2019),
                                            beløpFørJustering = 400000.0,
                                            beløpJustertFor6G = 400000.0,
                                            erBeløpJustertFor6G = false,
                                            grunnlagsfaktor = 4.04588
                                        )
                                    )
                                ),
                                fødselsdato = LocalDate.now().minusYears(40),
                                yrkesskade = null,
                                sisteKalenderår = Year.of(2021),
                                grunnlagsfaktor = 3.943968
                            ),
                            vedtaksdato = LocalDate.now(),
                            virkningsdato = LocalDate.now()
                        )
                    )
                )
            )

            assertEquals(expected, actual)
        }
    }

    private fun TestInputTopic<String, AvroManuell>.produserLøsning(
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
        produce(key) {
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

private fun withTestApp(test: ApplicationTestBuilder.(mocks: Mocks) -> Unit) = Mocks().use { mocks ->
    EnvironmentVariables(containerProperties()).execute {
        testApplication {
            application {
                server(mocks.kafka)
                this@testApplication.test(mocks)
            }
        }
    }
}

private fun containerProperties(): Map<String, String> = mapOf(
    "KAFKA_STREAMS_APPLICATION_ID" to "vedtak",
    "KAFKA_BROKERS" to "mock://kafka",
    "KAFKA_TRUSTSTORE_PATH" to "",
    "KAFKA_SECURITY_ENABLED" to "false",
    "KAFKA_KEYSTORE_PATH" to "",
    "KAFKA_CREDSTORE_PASSWORD" to "",
    "KAFKA_CLIENT_ID" to "vedtak",
    "KAFKA_GROUP_ID" to "vedtak-1",
    "KAFKA_SCHEMA_REGISTRY" to "mock://schema-registry",
    "KAFKA_SCHEMA_REGISTRY_USER" to "",
    "KAFKA_SCHEMA_REGISTRY_PASSWORD" to "",
)
