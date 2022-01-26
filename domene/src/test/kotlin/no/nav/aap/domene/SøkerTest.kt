package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.Søker.Companion.toFrontendSaker
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
        assertEquals("OPPFYLT", saker.first().vilkårsvurderinger.first().tilstand)
    }
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
    fun `Hvis vi mottar to søknader etterhverandre kastes en feil`() {
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

    private fun assertTilstand(actual: String, expected:Sak, personident: Personident, fødselsdato: Fødselsdato){
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
    }

    @Test
    fun `Hvis søkers alder er 68 år, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(68))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 18 år, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertTrue(vilkår.erOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 17 år, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
    }
}