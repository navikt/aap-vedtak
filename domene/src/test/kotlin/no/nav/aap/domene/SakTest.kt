package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
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
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
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
        assertEquals(
            "IKKE_OPPFYLT",
            vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand
        )
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
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
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

        sak.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))
        assertTilstand("BEREGN_INNTEKT", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_2).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
    }

    private fun assertTilstand(actual: String, expected: Sak, personident: Personident, fødselsdato: Fødselsdato) {
        val frontendSak = listOf(expected).toFrontendSak(personident, fødselsdato).first()
        assertEquals(actual, frontendSak.tilstand)
    }

    private fun List<FrontendVilkårsvurdering>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.paragraf == paragraf.name }
}
