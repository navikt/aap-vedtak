package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import no.nav.aap.domene.frontendView.FrontendVilkårsvurdering
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate


internal class SøkerTest {
    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker()
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

        val saker = listOf(søker).toFrontendSaker()
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

        val saker = listOf(søker).toFrontendSaker()
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(2, vilkårsvurderinger.size) { "Har ${vilkårsvurderinger.size} vilkårsvurderinger, skulle vært 2" }
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

        søker.håndterOppgavesvar(OppgavesvarParagraf_11_5(50))

        val saker = listOf(søker).toFrontendSaker()
        val vilkårsvurderinger = saker.first().vilkårsvurderinger
        assertEquals(2, vilkårsvurderinger.size) { "Har ${vilkårsvurderinger.size} vilkårsvurderinger, skulle vært 2" }
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_4).tilstand)
        assertEquals("OPPFYLT", vilkårsvurderinger.single(Vilkårsvurdering.Paragraf.PARAGRAF_11_5).tilstand)
    }

    private fun List<FrontendVilkårsvurdering>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.vilkår.paragraf == paragraf.name }
}

internal class SakTest {
    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        assertTilstand("Start", sak, personident, fødselsdato)
        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SøknadMottatt", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        assertEquals("OPPFYLT", saker.first().vilkårsvurderinger.first().tilstand)
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er under 18 år får vi et ikke-oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        assertTilstand("Start", sak, personident, fødselsdato)
        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("IkkeOppfylt", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        assertEquals("IKKE_OPPFYLT", saker.first().vilkårsvurderinger.first().tilstand)
    }

    @Test
    fun `Hvis vi mottar to søknader etter hverandre kastes en feil`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        assertTilstand("Start", sak, personident, fødselsdato)
        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SøknadMottatt", sak, personident, fødselsdato)
        assertThrows<IllegalStateException> { sak.håndterSøknad(søknad, fødselsdato) }
        assertTilstand("SøknadMottatt", sak, personident, fødselsdato)

        val saker = listOf(sak).toFrontendSak(personident, fødselsdato)
        assertEquals("OPPFYLT", saker.first().vilkårsvurderinger.first().tilstand)
    }

    private fun assertTilstand(actual: String, expected: Sak, personident: Personident, fødselsdato: Fødselsdato) {
        val frontendSak = listOf(expected).toFrontendSak(personident, fødselsdato).first()
        assertEquals(actual, frontendSak.tilstand)
    }
}

internal class FrontendTest {
    @Test
    fun `Noe til frontend som inneholder aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = listOf(søker).toFrontendSaker()
        assertEquals(1, saker.size)
    }
}

internal class FødselsdatoTest {
    @Test
    fun `Er mellom 18 og 67 år på 18-årsdagen`() {
        val `18-årsdagen` = 1.januar(2022)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertTrue(fødselsdato.erMellom18Og67År(`18-årsdagen`))
    }

    @Test
    fun `Er ikke mellom 18 og 67 år dagen før 18-årsdagen`() {
        val `dagen før 18-årsdagen` = 31.desember(2021)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertFalse(fødselsdato.erMellom18Og67År(`dagen før 18-årsdagen`))
    }

    @Test
    fun `Er mellom 18 og 67 år på 67-årsdagen`() {
        val `67-årsdagen` = 1.januar(2071)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertTrue(fødselsdato.erMellom18Og67År(`67-årsdagen`))
    }

    @Test
    fun `Er ikke mellom 18 og 67 år dagen etter 67-årsdagen`() {
        val `dagen etter 67-årsdagen` = 2.januar(2071)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertFalse(fødselsdato.erMellom18Og67År(`dagen etter 67-årsdagen`))
    }
}

internal fun Int.januar(år: Int) = LocalDate.of(år, 1, this)
internal val Int.januar get() = this.januar(2022)
internal fun Int.desember(år: Int) = LocalDate.of(år, 12, this)
internal val Int.desember get() = this.desember(2022)

internal class `§11-4 første ledd Test` {
    @Test
    fun `Hvis søkers alder er 67 år, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 68 år, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(68))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 18 år, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 17 år, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_4FørsteLedd()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }
}

internal class `§11-5 Test` {
    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 50 prosent, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val oppgavesvar = OppgavesvarParagraf_11_5(50)
        oppgavesvar.vurderNedsattArbeidsevne(vilkår)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 49 prosent, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val oppgavesvar = OppgavesvarParagraf_11_5(49)
        oppgavesvar.vurderNedsattArbeidsevne(vilkår)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_5()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke oppgavesvar, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }
}
