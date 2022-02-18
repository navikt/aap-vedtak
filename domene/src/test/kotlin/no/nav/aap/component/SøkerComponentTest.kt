package no.nav.aap.component

import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.Behov_11_2
import no.nav.aap.hendelse.behov.Behov_11_5
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SøkerComponentTest {
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
            )
        )
        assertEquals(expected, søker.toFrontendSaker().single())
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
                    Inntekt(Arbeidsgiver(), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver(), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver(), januar(2018), 500000.beløp)
                )
            )
        )

        val dtoSøker = søker.toDto()

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
