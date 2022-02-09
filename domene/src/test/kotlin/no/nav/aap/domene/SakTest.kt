package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class SakTest {
    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        assertTilstand("START", sak, personident, fødselsdato)
        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_4, Vilkårsvurdering.Ledd.LEDD_1)
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er under 18 år får vi et ikke-oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        assertTilstand("START", sak, personident, fødselsdato)
        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("IKKE_OPPFYLT", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertTilstand(vilkårsvurderinger, "IKKE_OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_4, Vilkårsvurdering.Ledd.LEDD_1)
    }

    @Test
    fun `Hvis vi mottar to søknader etter hverandre kastes en feil`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        assertTilstand("START", sak, personident, fødselsdato)
        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)
        assertThrows<IllegalStateException> { sak.håndterSøknad(søknad, fødselsdato) }
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_4, Vilkårsvurdering.Ledd.LEDD_1)
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er over 18 år, er medlem og har nedsatt arbeidsevne med 50 prosent vil saken gå videre i behandlingen`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()
        assertTilstand("START", sak, personident, fødselsdato)

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.JA)))
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_3(true))
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_4AndreOgTredjeLedd(true))
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_6(true))
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_12FørsteLedd(true))
        assertTilstand("SØKNAD_MOTTATT", sak, personident, fødselsdato)

        sak.håndterLøsning(LøsningParagraf_11_29(true))
        assertTilstand("BEREGN_INNTEKT", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_4, Vilkårsvurdering.Ledd.LEDD_1)
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_4, Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3)
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "OPPFYLT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    private fun assertTilstand(actual: String, expected: Sak, personident: Personident, fødselsdato: Fødselsdato) {
        val frontendSak = listOf(expected).toFrontendSak(personident, fødselsdato).first()
        assertEquals(actual, frontendSak.tilstand)
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
