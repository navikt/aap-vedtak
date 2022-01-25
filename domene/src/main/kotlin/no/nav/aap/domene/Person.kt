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
        sak.håndterSøknad(søknad, fødselsdato)
    }

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
    private fun alderPåDato(vurderingsdato: LocalDate) = this.dato.until(vurderingsdato, ChronoUnit.YEARS)

    internal fun erMellom18Og67År(vurderingsdato: LocalDate) =
        alderPåDato(vurderingsdato) in 18..67

    internal fun toFrontendFødselsdato() = dato
}

internal class Sak(private val søker: Søker) {
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering> = mutableListOf()
    private lateinit var vurderingsdato:LocalDate

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato) {
        this.vurderingsdato = LocalDate.now()
        val vilkår = Paragraf_11_4FørsteLedd()
        vilkårsvurderinger.add(vilkår)
        vilkårsvurderinger.forEach { it.håndterSøknad(søknad, fødselsdato, vurderingsdato) }
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

internal abstract class Vilkårsvurdering(
    private val paragraf: Paragraf,
    private val ledd: Ledd
) {
    internal enum class Paragraf {
        PARAGRAF_11_4
    }

    internal enum class Ledd {
        LEDD_1
    }

    internal abstract fun erOppfylt(): Boolean

    internal abstract fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate)

    private fun toFrontendVilkårsvurdering() =
        FrontendVilkårsvurdering(
            vilkår = FrontendVilkår(paragraf.name, ledd.name),
            tilstand = toFrontendTilstand()
        )

    protected abstract fun toFrontendTilstand(): String

    internal companion object {
        internal fun Iterable<Vilkårsvurdering>.toFrontendVilkårsvurdering() =
            map(Vilkårsvurdering::toFrontendVilkårsvurdering)
    }
}

internal class Paragraf_11_4FørsteLedd :
    Vilkårsvurdering(Paragraf.PARAGRAF_11_4, Ledd.LEDD_1) {
    private lateinit var fødselsdato: Fødselsdato
    private lateinit var vurderingsdato: LocalDate

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand = nyTilstand
    }

    private fun vurderAldersvilkår(fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        this.fødselsdato = fødselsdato
        this.vurderingsdato = vurderingsdato
        if (fødselsdato.erMellom18Og67År(vurderingsdato)) tilstand(Tilstand.Oppfylt)
        else tilstand(Tilstand.IkkeOppfylt)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun erOppfylt() = tilstand.erOppfylt()

    internal sealed class Tilstand(
        private val name: String,
        private val erOppfylt: Boolean
    ) {
        internal fun erOppfylt() = erOppfylt

        internal abstract fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        )

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4FørsteLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.vurderAldersvilkår(fødselsdato, vurderingsdato)
            }
        }

        object Oppfylt : Tilstand(
            name = "OPPFYLT",
            erOppfylt = true
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4FørsteLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                error("Vilkår allerede vurdert til oppfylt. Forventer ikke ny søknad")
            }
        }

        object IkkeOppfylt : Tilstand(
            name = "IKKE_OPPFYLT",
            erOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4FørsteLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                error("Vilkår allerede vurdert til ikke oppfylt. Forventer ikke ny søknad")
            }
        }

        internal fun toFrontendTilstand(): String = name
    }

    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
}
