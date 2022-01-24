package no.nav.aap.domene

import no.nav.aap.domene.Søker.Companion.toFrontendSaker
import org.junit.jupiter.api.Assertions.*
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

        val saker = listOf(søker).toFrontendSaker()
        assertEquals("OPPFYLT", saker.first().vilkårsvurderinger.first().tilstand)
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

internal class `§11-4 første ledd Test` {
    @Test
    fun `Hvis søkers alder er 67 år, er vilkår oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = `§11-4 første ledd`()

        val resultat = vilkår.håndterAlder(fødselsdato.alder())

        assertTrue(resultat.erOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 68 år, er vilkår ikke oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(68))

        val vilkår = `§11-4 første ledd`()

        val resultat = vilkår.håndterAlder(fødselsdato.alder())

        assertFalse(resultat.erOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 18 år, er vilkår oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))

        val vilkår = `§11-4 første ledd`()

        val resultat = vilkår.håndterAlder(fødselsdato.alder())

        assertTrue(resultat.erOppfylt())
    }

    @Test
    fun `Hvis søkers alder er 17 år, er vilkår ikke oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))

        val vilkår = `§11-4 første ledd`()

        val resultat = vilkår.håndterAlder(fødselsdato.alder())

        assertFalse(resultat.erOppfylt())
    }
}