package no.nav.aap.domene

import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal interface SøkerVisitor {
    fun preVisitSøker(personident: Personident, fødselsdato: Fødselsdato) {}
    fun visitVilkårsvurderingIkkeVurdert() {}
    fun visitVilkårsvurderingOppfylt() {}
    fun visitVilkårsvurderingIkkeOppfylt() {}
    fun postVisitSøker(personident: Personident, fødselsdato: Fødselsdato) {}
}

internal class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    private val saker: MutableList<Sak> = mutableListOf()

    fun accept(visitor: SøkerVisitor) {
        visitor.preVisitSøker(personident, fødselsdato)
        saker.forEach { it.accept(visitor) }
        visitor.postVisitSøker(personident, fødselsdato)
    }

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak(this)
        saker.add(sak)
        sak.håndterSøknad(søknad)
    }

    internal fun alder() = fødselsdato.alder()
}

class Personident

@JvmInline
value class Fødselsdato(private val dato: LocalDate) {
    internal fun alder() = dato.until(LocalDate.now(), ChronoUnit.YEARS)
}

internal class Sak(private val søker: Søker) {
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering> = mutableListOf()

    fun accept(visitor: SøkerVisitor) {
        vilkårsvurderinger.forEach { it.accept(visitor) }
    }

    fun håndterSøknad(søknad: Søknad) {
        val vilkår = `§11-4 første ledd`()
        val vilkårsvurdering = vilkår.håndterAlder(søker.alder())
        vilkårsvurderinger.add(vilkårsvurdering)
        //opprett viklårsvurderinger
        //hent mer informasjon?
    }
}

class Søknad(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    internal fun opprettSøker() = Søker(personident, fødselsdato)
}

internal open class Vilkårsvurdering(
    private val vilkår: Vilkår
) {
    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private sealed class Tilstand {
        abstract fun accept(visitor: SøkerVisitor)

        object IkkeVurdert : Tilstand() {
            override fun accept(visitor: SøkerVisitor) = visitor.visitVilkårsvurderingIkkeVurdert()
        }

        object Oppfylt : Tilstand() {
            override fun accept(visitor: SøkerVisitor) = visitor.visitVilkårsvurderingOppfylt()
        }

        object IkkeOppfylt : Tilstand() {
            override fun accept(visitor: SøkerVisitor) = visitor.visitVilkårsvurderingIkkeOppfylt()
        }
    }

    fun accept(visitor: SøkerVisitor) {
        tilstand.accept(visitor)
    }

    internal fun erOppfylt() = tilstand == Tilstand.Oppfylt

    internal fun vurdertOppfylt() {
        tilstand = Tilstand.Oppfylt
    }

    fun vurdertIkkeOppfylt() {
        tilstand = Tilstand.IkkeOppfylt
    }
}

internal open class Vilkår {

}

internal class `§11-4 første ledd` : Vilkår() {
    fun håndterAlder(alder: Long): Vilkårsvurdering {
        val erMellom18Og67År = alder in 18..67
        val vilkårsvurdering = Vilkårsvurdering(this)
        if (erMellom18Og67År) vilkårsvurdering.vurdertOppfylt()
        else vilkårsvurdering.vurdertIkkeOppfylt()
        return vilkårsvurdering
    }
}
