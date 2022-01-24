package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.Vilkårsvurdering.Companion.toFrontendVilkårsvurdering
import no.nav.aap.domene.frontendView.FrontendSak
import no.nav.aap.domene.frontendView.FrontendVilkår
import no.nav.aap.domene.frontendView.FrontendVilkårsvurdering
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    private val saker: MutableList<Sak> = mutableListOf()

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak(this)
        saker.add(sak)
        sak.håndterSøknad(søknad)
    }

    internal fun alder() = fødselsdato.alder()

    private fun toFrontendSaker() =
        saker.toFrontendSak(
            personident = personident,
            fødselsdato = fødselsdato
        )

    companion object {
        fun Iterable<Søker>.toFrontendSaker() = flatMap(Søker::toFrontendSaker)
    }
}

class Personident(
    private val ident: String
) {
    internal fun toFrontendPersonident() = ident
}

class Fødselsdato(private val dato: LocalDate) {
    internal fun alder() = dato.until(LocalDate.now(), ChronoUnit.YEARS)

    internal fun toFrontendFødselsdato() = dato
}

internal class Sak(private val søker: Søker) {
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering> = mutableListOf()

    internal fun håndterSøknad(søknad: Søknad) {
        val vilkår = `§11-4 første ledd`()
        val vilkårsvurdering = vilkår.håndterAlder(søker.alder())
        vilkårsvurderinger.add(vilkårsvurdering)
        //opprett viklårsvurderinger
        //hent mer informasjon?
    }

    private fun toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) =
        FrontendSak(
            personident = personident.toFrontendPersonident(),
            fødselsdato = fødselsdato.toFrontendFødselsdato(),
            vilkårsvurderinger = vilkårsvurderinger.toFrontendVilkårsvurdering()
        )

    internal companion object {
        internal fun Iterable<Sak>.toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) = map {
            it.toFrontendSak(personident = personident, fødselsdato = fødselsdato)
        }
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

    private sealed class Tilstand(private val name: String) {
        fun toFrontendTilstand(): String = name

        object IkkeVurdert : Tilstand("IKKE_VURDERT")

        object Oppfylt : Tilstand("OPPFYLT")

        object IkkeOppfylt : Tilstand("IKKE_OPPFYLT")
    }

    internal fun erOppfylt() = tilstand == Tilstand.Oppfylt

    internal fun vurdertOppfylt() {
        tilstand = Tilstand.Oppfylt
    }

    fun vurdertIkkeOppfylt() {
        tilstand = Tilstand.IkkeOppfylt
    }

    private fun toFrontendVilkårsvurdering() =
        FrontendVilkårsvurdering(
            vilkår = vilkår.toFrontendVilkår(),
            tilstand = tilstand.toFrontendTilstand()
        )

    internal companion object {
        internal fun Iterable<Vilkårsvurdering>.toFrontendVilkårsvurdering() =
            map(Vilkårsvurdering::toFrontendVilkårsvurdering)
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

    fun toFrontendVilkår() = FrontendVilkår(paragraf = paragraf.name, ledd = ledd.name)
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
