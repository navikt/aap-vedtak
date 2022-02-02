package no.nav.aap.e2e

import no.nav.aap.domene.Lytter
import no.nav.aap.domene.Oppgave
import no.nav.aap.domene.Søker
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.OppgavesvarParagraf_11_2
import no.nav.aap.hendelse.OppgavesvarParagraf_11_5
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SøkerTest {
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
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på svar om nedsatt arbeidsevne`() {
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
    fun `Hvis vi mottar svar på oppgave om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterOppgavesvar(OppgavesvarParagraf_11_5(OppgavesvarParagraf_11_5.NedsattArbeidsevnegrad(50)))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på svar om nedsatt arbeidsevne og medlemskap`() {
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
    fun `Hvis vi mottar svar på oppgave om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt, men ikke vilkår om medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterOppgavesvar(OppgavesvarParagraf_11_5(OppgavesvarParagraf_11_5.NedsattArbeidsevnegrad(50)))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_2).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
    }

    @Test
    fun `Hvis vi mottar svar på oppgave om der bruker er medlem, blir vilkår om medlemskap oppfylt, men ikke vilkår om nedsatt arbeidsevne`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterOppgavesvar(OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.JA)))

        val saker = listOf(søker).toFrontendSaker(personident)
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(3, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_2).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("SØKNAD_MOTTATT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
    }

    @Test
    fun `En oppgave opprettes etter håndtering av søknad`() {
        val lytter = object : Lytter {
            lateinit var søker: Søker
            private var oppgaveOpprettet = false
            var oppgave: FrontendSak? = null

            override fun oppdaterSøker(søker: Søker) {
                this.søker = søker
            }

            override fun sendOppgave(oppgave: Oppgave) {
                this.oppgaveOpprettet = true
            }

            override fun finalize() {
                if (this.oppgaveOpprettet) this.oppgave = søker.toFrontendSaker().last()
            }
        }

        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker(lytter)
        søker.håndterSøknad(søknad)

        lytter.finalize()
        assertNotNull(lytter.oppgave)

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
        assertEquals(expected, lytter.oppgave)
    }

    private fun List<FrontendVilkårsvurdering>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.paragraf == paragraf.name }
}
