package no.nav.aap.component

import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_2
import no.nav.aap.hendelse.behov.Behov_11_5
import org.junit.jupiter.api.Assertions.*
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
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
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
        assertEquals("IKKE_OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
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
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
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
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
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
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_2).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
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
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_2).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om der bruker er medlem, blir vilkår om medlemskap oppfylt, men ikke vilkår om nedsatt arbeidsevne`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.JA)))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_2).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
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
                    paragraf = "PARAGRAF_11_4",
                    ledd = listOf("LEDD_1"),
                    tilstand = "OPPFYLT",
                    harÅpenOppgave = false
                ),
                FrontendVilkårsvurdering(
                    paragraf = "PARAGRAF_11_5",
                    ledd = listOf("LEDD_1", "LEDD_2"),
                    tilstand = "SØKNAD_MOTTATT",
                    harÅpenOppgave = true
                )
            )
        )
        assertEquals(expected, søker.toFrontendSaker().single())
    }

    private fun List<FrontendVilkårsvurdering>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.paragraf == paragraf.name }
}
