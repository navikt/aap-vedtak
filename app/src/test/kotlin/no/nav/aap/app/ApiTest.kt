package no.nav.aap.app

import io.ktor.server.testing.*
import no.nav.aap.app.kafka.SØKERE_STORE_NAME
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.*
import no.nav.aap.app.modell.InntekterKafkaDto.Response.Inntekt
import no.nav.aap.dto.*
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
            val manuell_11_3_Topic = mocks.kafka.inputTopic(Topics.manuell_11_3)
            val manuell_11_5_Topic = mocks.kafka.inputTopic(Topics.manuell_11_5)
            val manuell_11_6_Topic = mocks.kafka.inputTopic(Topics.manuell_11_6)
            val manuell_11_12_Topic = mocks.kafka.inputTopic(Topics.manuell_11_12)
            val manuell_11_29_Topic = mocks.kafka.inputTopic(Topics.manuell_11_29)
            val manuell_beregningsdato_Topic = mocks.kafka.inputTopic(Topics.manuell_beregningsdato)
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

            manuell_11_3_Topic.produce(fnr) {
                Løsning_11_3_manuell("saksbehandler", true)
            }
            manuell_11_5_Topic.produce(fnr) {
                Løsning_11_5_manuell(
                    vurdertAv = "veileder",
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            }
            manuell_11_6_Topic.produce(fnr) {
                Løsning_11_6_manuell(
                    vurdertAv = "saksbehandler",
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true
                )
            }
            manuell_11_12_Topic.produce(fnr) {
                Løsning_11_12_ledd1_manuell(
                    vurdertAv = "saksbehandler",
                    bestemmesAv = "SPS",
                    unntak = "INGEN",
                    unntaksbegrunnelse = "",
                    manueltSattVirkningsdato = LocalDate.now()
                )
            }
            manuell_11_29_Topic.produce(fnr) {
                Løsning_11_29_manuell("saksbehandler", true)
            }
            manuell_beregningsdato_Topic.produce(fnr) {
                LøsningVurderingAvBeregningsdato("saksbehandler", LocalDate.of(2022, 1, 1))
            }

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
                                        løsning_11_2_maskinell = DtoLøsningParagraf_11_2(
                                            "maskinell saksbehandling",
                                            "JA"
                                        ),
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(1),
                                        vurdertAv = "saksbehandler",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_3",
                                        ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
                                        tilstand = "OPPFYLT_MANUELT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_3_manuell = DtoLøsningParagraf_11_3("saksbehandler", true)
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(2),
                                        vurdertAv = "maskinell saksbehandling",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_4",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT_MANUELT",
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
                                        tilstand = "OPPFYLT_MANUELT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_5_manuell = DtoLøsningParagraf_11_5(
                                            vurdertAv = "veileder",
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
                                        tilstand = "OPPFYLT_MANUELT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_6_manuell = DtoLøsningParagraf_11_6(
                                            vurdertAv = "saksbehandler",
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
                                        tilstand = "OPPFYLT_MANUELT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_12_ledd1_manuell = DtoLøsningParagraf_11_12_ledd1(
                                            vurdertAv = "saksbehandler",
                                            bestemmesAv = "SPS",
                                            unntak = "INGEN",
                                            unntaksbegrunnelse = "",
                                            manueltSattVirkningsdato = LocalDate.now()
                                        )
                                    ),
                                    DtoVilkårsvurdering(
                                        vilkårsvurderingsid = vilkårsvurderingsid(7),
                                        vurdertAv = "saksbehandler",
                                        godkjentAv = null,
                                        paragraf = "PARAGRAF_11_29",
                                        ledd = listOf("LEDD_1"),
                                        tilstand = "OPPFYLT_MANUELT",
                                        utfall = Utfall.OPPFYLT,
                                        løsning_11_29_manuell = DtoLøsningParagraf_11_29("saksbehandler", true)
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

    @Test
    fun `SøkereKafkaDto opprettes med nyeste versjon`() {
        val nyeste = SøkereKafkaDto("", LocalDate.now(), emptyList())
        assertEquals(SøkereKafkaDto.VERSION, nyeste.version)
    }

    @Test
    fun `versjon på forrige søkereKafkaDto skal være 1 mindre enn nyeste versjon`() {
        val forrige = ForrigeSøkereKafkaDto("", LocalDate.now(), emptyList())
        assertEquals(SøkereKafkaDto.VERSION - 1, forrige.version)
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
