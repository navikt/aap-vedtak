package no.nav.aap.app

import io.ktor.server.testing.*
import no.nav.aap.app.kafka.SØKERE_STORE_NAME
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.InntekterKafkaDto.Response.Inntekt
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.ManuellKafkaDto
import no.nav.aap.app.modell.ManuellKafkaDto.*
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.dto.*
import org.apache.kafka.streams.TestInputTopic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import no.nav.aap.avro.medlem.v1.ErMedlem as AvroErMedlem
import no.nav.aap.avro.medlem.v1.Response as AvroMedlemResponse

internal class ApiTest {

    @Test
    fun `søker får innvilget vedtak`() {
        withTestApp { mocks ->
            val søknadTopic = mocks.kafka.inputTopic(Topics.søknad)
            val medlemTopic = mocks.kafka.inputTopic(Topics.medlem)
            val medlemOutputTopic = mocks.kafka.outputTopic(Topics.medlem)
            val manuellTopic = mocks.kafka.inputTopic(Topics.manuell)
            val inntektTopic = mocks.kafka.inputTopic(Topics.inntekter)
            val inntektOutputTopic = mocks.kafka.outputTopic(Topics.inntekter)
            val stateStore = mocks.kafka.getStore<SøkereKafkaDto>(SØKERE_STORE_NAME)

            val fnr = "123"
            søknadTopic.produce(fnr) {
                JsonSøknad(fødselsdato = LocalDate.now().minusYears(40))
            }

            val medlemRequest = medlemOutputTopic.readValue()
            medlemTopic.produce(fnr) {
                medlemRequest.apply {
                    response = AvroMedlemResponse.newBuilder().setErMedlem(AvroErMedlem.JA).build()
                }
            }

            manuellTopic.produserLøsning(
                key = fnr,
                vurdertAv = "saksbehandler",
                løsning_11_3_manuell = Løsning_11_3_manuell(true)
            )
            manuellTopic.produserLøsning(
                key = fnr,
                vurdertAv = "veileder",
                løsning_11_5_manuell = Løsning_11_5_manuell(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            )
            manuellTopic.produserLøsning(
                key = fnr,
                vurdertAv = "saksbehandler",
                løsning_11_6_manuell = Løsning_11_6_manuell(
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true
                )
            )
            manuellTopic.produserLøsning(
                key = fnr,
                vurdertAv = "saksbehandler",
                løsning_11_12_l1_manuell = Løsning_11_12_ledd1_manuell("SPS", "INGEN", "", LocalDate.now())
            )
            manuellTopic.produserLøsning(
                key = fnr,
                vurdertAv = "saksbehandler",
                løsning_11_29_manuell = Løsning_11_29_manuell(true)
            )
            manuellTopic.produserLøsning(
                key = fnr,
                vurdertAv = "saksbehandler",
                løsningVurderingAvBeregningsdato = LøsningVurderingAvBeregningsdato(LocalDate.of(2022, 1, 1))
            )

            val inntekter: InntekterKafkaDto = inntektOutputTopic.readValue()
            inntektTopic.produce(fnr) {
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

            val søker = stateStore[fnr]
            assertNotNull(søker)
            val actual = søker.toDto()
            assertNotNull(actual.saker.firstOrNull()?.vedtak) { "Saken mangler vedtak - $actual" }
            val søknadstidspunkt = actual.saker.first().søknadstidspunkt

            fun vilkårsvurderingsid(index: Int) =
                actual.saker.first().sakstyper.first().vilkårsvurderinger[index].vilkårsvurderingsid

            val expected = DtoSøker(
                personident = fnr,
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
                                        vurdertAv = "maskinell saksbehandling",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_2",
                                        ledd = listOf("LEDD_1", "LEDD_2"),
                                        tilstand = "OPPFYLT_MASKINELT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_2_maskinell = DtoLøsningParagraf_11_2("JA"),
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(1),
                                        vurdertAv = "saksbehandler",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_3",
                                        ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
                                        tilstand = "OPPFYLT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_3_manuell = DtoLøsningParagraf_11_3(true)
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(2),
                                        vurdertAv = "maskinell saksbehandling",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        utfall = Utfall.OPPFYLT
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(3),
                                        vurdertAv = null,
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_2", "LEDD_3"),
                                        tilstand = "IKKE_RELEVANT",
                                        utfall = Utfall.IKKE_RELEVANT
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(4),
                                        vurdertAv = "veileder",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_5",
                                        ledd = listOf("LEDD_1", "LEDD_2"),
                                        tilstand = "OPPFYLT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_5_manuell = DtoLøsningParagraf_11_5(
                                            kravOmNedsattArbeidsevneErOppfylt = true,
                                            nedsettelseSkyldesSykdomEllerSkade = true,
                                        )
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(5),
                                        vurdertAv = "saksbehandler",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_6",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_6_manuell = DtoLøsningParagraf_11_6(
                                            harBehovForBehandling = true,
                                            harBehovForTiltak = true,
                                            harMulighetForÅKommeIArbeid = true
                                        )
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(6),
                                        vurdertAv = "saksbehandler",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_12",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_12_ledd1_manuell = DtoLøsningParagraf_11_12_ledd1(
                                            "SPS",
                                            "INGEN",
                                            "",
                                            LocalDate.now()
                                        )
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(7),
                                        vurdertAv = "saksbehandler",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_29",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_29_manuell = DtoLøsningParagraf_11_29(true)
                                    )
                                )
                            )
                        ),
                        vurderingAvBeregningsdato = DtoVurderingAvBeregningsdato(
                            tilstand = "FERDIG",
                            løsningVurderingAvBeregningsdato = DtoLøsningVurderingAvBeregningsdato(
                                vurdertAv = "saksbehandler",
                                beregningsdato = LocalDate.of(2022, 1, 1)
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

    private fun TestInputTopic<String, ManuellKafkaDto>.produserLøsning(
        key: String,
        vurdertAv: String,
        løsning_11_2_manuell: Løsning_11_2_manuell? = null,
        løsning_11_3_manuell: Løsning_11_3_manuell? = null,
        løsning_11_4_l2_l3_manuell: Løsning_11_4_ledd2_ledd3_manuell? = null,
        løsning_11_5_manuell: Løsning_11_5_manuell? = null,
        løsning_11_6_manuell: Løsning_11_6_manuell? = null,
        løsning_11_12_l1_manuell: Løsning_11_12_ledd1_manuell? = null,
        løsning_11_29_manuell: Løsning_11_29_manuell? = null,
        løsningVurderingAvBeregningsdato: LøsningVurderingAvBeregningsdato? = null
    ) {
        produce(key) {
            ManuellKafkaDto(
                vurdertAv = vurdertAv,
                løsning_11_2_manuell = løsning_11_2_manuell,
                løsning_11_3_manuell = løsning_11_3_manuell,
                løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_l2_l3_manuell,
                løsning_11_5_manuell = løsning_11_5_manuell,
                løsning_11_6_manuell = løsning_11_6_manuell,
                løsning_11_12_ledd1_manuell = løsning_11_12_l1_manuell,
                løsning_11_29_manuell = løsning_11_29_manuell,
                løsningVurderingAvBeregningsdato = løsningVurderingAvBeregningsdato
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
    "KAFKA_KEYSTORE_PATH" to "",
    "KAFKA_CREDSTORE_PASSWORD" to "",
    "KAFKA_CLIENT_ID" to "vedtak",
    "KAFKA_GROUP_ID" to "vedtak-1",
    "KAFKA_SCHEMA_REGISTRY" to "mock://schema-registry",
    "KAFKA_SCHEMA_REGISTRY_USER" to "",
    "KAFKA_SCHEMA_REGISTRY_PASSWORD" to "",
)
