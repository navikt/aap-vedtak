package no.nav.aap.domene

import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface SøkerVisitor : VilkårsvurderingVisitor {
    fun preVisitSøker(personident: Personident, fødselsdato: Fødselsdato) {}
    fun visitPersonident(ident: String) {}
    fun visitFødselsdato(fødselsdato: LocalDate) {}
    fun preVisitSak() {}
    fun visitVilkår(paragraf: String, ledd: String) {}
    fun postVisitSak() {}
    fun postVisitSøker(personident: Personident, fødselsdato: Fødselsdato) {}
}

interface VilkårsvurderingVisitor {
    fun visitVilkårsvurderingIkkeVurdert() {}
    fun visitVilkårsvurderingOppfylt() {}
    fun visitVilkårsvurderingIkkeOppfylt() {}
}

class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    private val saker: MutableList<Sak> = mutableListOf()

    fun accept(visitor: SøkerVisitor) {
        visitor.preVisitSøker(personident, fødselsdato)
        personident.accept(visitor)
        fødselsdato.accept(visitor)
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

class Personident(
    private val ident: String
) {
    internal fun accept(visitor: SøkerVisitor) {
        visitor.visitPersonident(ident)
    }
}

@JvmInline
value class Fødselsdato(private val dato: LocalDate) {
    internal fun alder() = dato.until(LocalDate.now(), ChronoUnit.YEARS)

    internal fun accept(visitor: SøkerVisitor) {
        visitor.visitFødselsdato(dato)
    }
}

internal class Sak(private val søker: Søker) {
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering> = mutableListOf()

    fun accept(visitor: SøkerVisitor) {
        visitor.preVisitSak()
        vilkårsvurderinger.forEach { it.accept(visitor) }
        visitor.postVisitSak()
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
    fun opprettSøker() = Søker(personident, fødselsdato)
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
        vilkår.accept(visitor)
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

internal abstract class Vilkår(
    private val paragraf: Paragraf,
    private val ledd: Ledd,
) {
    internal enum class Paragraf {
        PARAGRAF_11_4
    }

    internal enum class Ledd {
        LEDD_1
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.visitVilkår(paragraf.name, ledd.name)
    }
}

internal class `§11-4 første ledd` : Vilkår(Paragraf.PARAGRAF_11_4, Ledd.LEDD_1) {
    fun håndterAlder(alder: Long): Vilkårsvurdering {
        val erMellom18Og67År = alder in 18..67
        val vilkårsvurdering = Vilkårsvurdering(this)
        if (erMellom18Og67År) vilkårsvurdering.vurdertOppfylt()
        else vilkårsvurdering.vurdertIkkeOppfylt()
        return vilkårsvurdering
    }
}
