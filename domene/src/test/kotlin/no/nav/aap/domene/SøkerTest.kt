package no.nav.aap.domene

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

        val visitor = TestVisitor()
        søker.accept(visitor)
        assertTrue(visitor.oppfylt)
    }
}

private class TestVisitor : SøkerVisitor {
    var oppfylt = false

    override fun visitVilkårsvurderingOppfylt() {
        oppfylt = true
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

        val visitor = FrontendVisitor()
        søker.accept(visitor)
        val saker = visitor.saker()
        assertEquals(1, saker.size)
    }
}

private class FrontendVisitor : SøkerVisitor {
    private lateinit var ident: String
    private lateinit var fødselsdato: LocalDate

    override fun visitPersonident(ident: String) {
        this.ident = ident
    }

    override fun visitFødselsdato(fødselsdato: LocalDate) {
        this.fødselsdato = fødselsdato
    }

    private val vilkårsvurderinger: MutableList<FrontendVilkårsvurdering> = mutableListOf()

    override fun preVisitSak() {
        vilkårsvurderinger.clear()
    }

    private lateinit var vilkår: FrontendVilkår

    override fun visitVilkår(paragraf: String, ledd: String) {
        this.vilkår = FrontendVilkår(paragraf, ledd)
    }

    override fun visitVilkårsvurderingIkkeVurdert() {
        vilkårsvurderinger.add(FrontendVilkårsvurdering(vilkår, "IKKE_VURDERT"))
    }

    override fun visitVilkårsvurderingOppfylt() {
        vilkårsvurderinger.add(FrontendVilkårsvurdering(vilkår, "OPPFYLT"))
    }

    override fun visitVilkårsvurderingIkkeOppfylt() {
        vilkårsvurderinger.add(FrontendVilkårsvurdering(vilkår, "IKKE_OPPFYLT"))
    }

    private val saker: MutableList<FrontendSak> = mutableListOf()

    override fun postVisitSak() {
        saker.add(FrontendSak(ident, fødselsdato, vilkårsvurderinger.toList()))
    }

    fun saker() = saker.toList()
}

private class FrontendSak(
    private val personident: String,
    private val fødselsdato: LocalDate,
    private val vilkårsvurdering: List<FrontendVilkårsvurdering>
)

private class FrontendVilkårsvurdering(
    private val vilkår: FrontendVilkår,
    private val tilstand: String
)

private class FrontendVilkår(
    private val paragraf: String,
    private val ledd: String
)

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