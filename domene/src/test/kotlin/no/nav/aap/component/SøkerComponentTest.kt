package no.nav.aap.component

import no.nav.aap.domene.Søker
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.dto.DtoSøker
import no.nav.aap.frontendView.*
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.Behov_11_2
import no.nav.aap.hendelse.behov.Behov_11_5
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.september
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Year

internal class SøkerComponentTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er under 18 år får vi et ikke-oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne og medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar en søknad med bruker over 62 får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne og medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(65))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "SØKNAD_MOTTATT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt, men ikke vilkår om medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om der bruker er medlem, blir vilkår om medlemskap oppfylt, men ikke vilkår om nedsatt arbeidsevne`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.JA))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `En behov opprettes etter håndtering av søknad`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val behov = søknad.behov()
        assertTrue(behov.filterIsInstance<Behov_11_2>().isNotEmpty())
        assertTrue(behov.filterIsInstance<Behov_11_5>().isNotEmpty())

        val expected = FrontendSak(
            personident = "12345678910",
            fødselsdato = LocalDate.now().minusYears(18),
            tilstand = "SØKNAD_MOTTATT",
            vilkårsvurderinger = listOf(
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_2",
                    ledd = listOf("LEDD_1", "LEDD_2"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_3",
                    ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = true
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_4",
                    ledd = listOf("LEDD_1"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_4",
                    ledd = listOf("LEDD_2", "LEDD_3"),
                    tilstand = "IKKE_RELEVANT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_5",
                    ledd = listOf("LEDD_1", "LEDD_2"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = true
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_6",
                    ledd = listOf("LEDD_1"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = true
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_12",
                    ledd = listOf("LEDD_1"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = true
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_29",
                    ledd = listOf("LEDD_1"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = true
                )
            ),
            vedtak = null
        )
        assertEquals(expected, søker.toFrontendSaker().single())
    }

    @Test
    fun `Beregner vedtak og mapper sak til frontend`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(40))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.JA))
        søker.håndterLøsning(LøsningParagraf_11_3(true))
        søker.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))
        søker.håndterLøsning(LøsningParagraf_11_6(true))
        søker.håndterLøsning(LøsningParagraf_11_12FørsteLedd(true))
        søker.håndterLøsning(LøsningParagraf_11_29(true))
        søker.håndterLøsning(LøsningVurderingAvBeregningsdato(13 januar 2022))
        søker.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(ARBEIDSGIVER, januar(2021), 400000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2020), 400000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2019), 400000.beløp)
                )
            )
        )

        val behov = søknad.behov()
        assertTrue(behov.filterIsInstance<Behov_11_2>().isNotEmpty())
        assertTrue(behov.filterIsInstance<Behov_11_5>().isNotEmpty())

        val actual = søker.toFrontendSaker().single()
        Assertions.assertNotNull(actual.vedtak) { "Saken mangler vedtak - $actual" }
        val søknadstidspunkt = actual.vedtak!!.søknadstidspunkt

        val expected = FrontendSak(
            personident = "12345678910",
            fødselsdato = LocalDate.now().minusYears(40),
            tilstand = "VEDTAK_FATTET",
            vilkårsvurderinger = listOf(
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_2",
                    ledd = listOf("LEDD_1", "LEDD_2"),
                    tilstand = "OPPFYLT_MASKINELT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_3",
                    ledd = listOf("LEDD_1", "LEDD_2", "LEDD_3"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_4",
                    ledd = listOf("LEDD_1"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_4",
                    ledd = listOf("LEDD_2", "LEDD_3"),
                    tilstand = "IKKE_RELEVANT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_5",
                    ledd = listOf("LEDD_1", "LEDD_2"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_6",
                    ledd = listOf("LEDD_1"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_12",
                    ledd = listOf("LEDD_1"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_29",
                    ledd = listOf("LEDD_1"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                )
            ),
            vedtak = FrontendVedtak(
                innvilget = true,
                inntektsgrunnlag = FrontendInntektsgrunnlag(
                    beregningsdato = 13 januar 2022,
                    inntekterSiste3Kalenderår = listOf(
                        FrontendInntektsgrunnlagForÅr(
                            år = Year.of(2021),
                            inntekter = listOf(
                                FrontendInntekt(
                                    arbeidsgiver = ARBEIDSGIVER.toFrontendArbeidsgiver(),
                                    inntekstmåned = januar(2021),
                                    beløp = 400000.0
                                )
                            ),
                            beløpFørJustering = 400000.0,
                            beløpJustertFor6G = 400000.0,
                            erBeløpJustertFor6G = false,
                            grunnlagsfaktor = 3.819856
                        ),
                        FrontendInntektsgrunnlagForÅr(
                            år = Year.of(2020),
                            inntekter = listOf(
                                FrontendInntekt(
                                    arbeidsgiver = ARBEIDSGIVER.toFrontendArbeidsgiver(),
                                    inntekstmåned = januar(2020),
                                    beløp = 400000.0
                                )
                            ),
                            beløpFørJustering = 400000.0,
                            beløpJustertFor6G = 400000.0,
                            erBeløpJustertFor6G = false,
                            grunnlagsfaktor = 3.966169
                        ),
                        FrontendInntektsgrunnlagForÅr(
                            år = Year.of(2019),
                            inntekter = listOf(
                                FrontendInntekt(
                                    arbeidsgiver = ARBEIDSGIVER.toFrontendArbeidsgiver(),
                                    inntekstmåned = januar(2019),
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
        assertEquals(expected, actual)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt`() {
        val fødselsdato = Fødselsdato(17 juli 1995)
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.JA))
        søker.håndterLøsning(LøsningParagraf_11_3(true))
        søker.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))
        søker.håndterLøsning(LøsningParagraf_11_6(true))
        søker.håndterLøsning(LøsningParagraf_11_12FørsteLedd(true))
        søker.håndterLøsning(LøsningParagraf_11_29(true))
        søker.håndterLøsning(LøsningVurderingAvBeregningsdato(13 september 2021))
        søker.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(ARBEIDSGIVER, januar(2020), 500000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2019), 500000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2018), 500000.beløp)
                )
            )
        )

        val dtoSøker = søker.toDto()

        assertEquals("VEDTAK_FATTET", dtoSøker.saker.single().tilstand)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt - med serialisering og deserialisering`() {
        val fødselsdato = Fødselsdato(17 juli 1995)
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        var dtoSøker: DtoSøker = søknad.opprettSøker().apply { håndterSøknad(søknad) }.toDto()

        fun medSøker(block: Søker.() -> Unit) {
            val søker = Søker.gjenopprett(dtoSøker)
            block(søker)
            dtoSøker = søker.toDto()
        }

        medSøker { håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.JA)) }
        medSøker { håndterLøsning(LøsningParagraf_11_3(true)) }
        medSøker { håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50))) }
        medSøker { håndterLøsning(LøsningParagraf_11_6(true)) }
        medSøker { håndterLøsning(LøsningParagraf_11_12FørsteLedd(true)) }
        medSøker { håndterLøsning(LøsningParagraf_11_29(true)) }
        medSøker { håndterLøsning(LøsningVurderingAvBeregningsdato(13 september 2021)) }
        medSøker {
            håndterLøsning(
                LøsningInntekter(
                    listOf(
                        Inntekt(ARBEIDSGIVER, januar(2020), 500000.beløp),
                        Inntekt(ARBEIDSGIVER, januar(2019), 500000.beløp),
                        Inntekt(ARBEIDSGIVER, januar(2018), 500000.beløp)
                    )
                )
            )
        }
        medSøker { }//Map frem og tilbake enda en gang for å sjekke at vedtak også blir mappet korrekt

        assertEquals("VEDTAK_FATTET", dtoSøker.saker.single().tilstand)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<FrontendVilkårsvurdering>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf
    ) {
        assertEquals(tilstand, vilkårsvurderinger.single(paragraf).tilstand)
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<FrontendVilkårsvurdering>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: Vilkårsvurdering.Ledd
    ) {
        assertTilstand(vilkårsvurderinger, tilstand, paragraf, listOf(ledd))
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<FrontendVilkårsvurdering>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>
    ) {
        assertEquals(tilstand, vilkårsvurderinger.single(paragraf, ledd).tilstand)
    }

    private fun List<FrontendVilkårsvurdering>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.paragraf == paragraf.name }

    private fun List<FrontendVilkårsvurdering>.single(
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>
    ) = single { it.paragraf == paragraf.name && it.ledd == ledd.map(Vilkårsvurdering.Ledd::name) }
}
