package no.nav.aap.domene

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate


internal class SøkerTest {
    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`(){
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident()
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val visitor = TestVisitor()
        søker.accept(visitor)
        assertTrue(visitor.oppfylt)
    }
}

private class TestVisitor : SøkerVisitor{
    var oppfylt = false

    override fun visitVilkårsvurderingOppfylt() {
        oppfylt = true
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