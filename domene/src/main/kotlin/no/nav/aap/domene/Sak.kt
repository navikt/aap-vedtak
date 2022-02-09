package no.nav.aap.domene

import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erAlleOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erNoenIkkeOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toFrontendVilkårsvurdering
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.*
import no.nav.aap.domene.vilkår.Paragraf_11_2
import no.nav.aap.domene.vilkår.Paragraf_11_4FørsteLedd
import no.nav.aap.domene.vilkår.Paragraf_11_5
import no.nav.aap.domene.vilkår.Paragraf_11_6
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.hendelse.*
import java.time.LocalDate

internal class Sak {
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering> = mutableListOf()
    private lateinit var vurderingsdato: LocalDate

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato) {
        this.vurderingsdato = LocalDate.now()
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_2) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_3) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) {
        tilstand.håndterLøsning(this, løsning)
    }

    private var tilstand: Tilstand = Start

    private fun tilstand(nyTilstand: Tilstand) {
        nyTilstand.onExit()
        tilstand = nyTilstand
        tilstand.onEntry()
    }

    private sealed interface Tilstand {
        val tilstandsnavn: Tilstandsnavn

        enum class Tilstandsnavn {
            START, SØKNAD_MOTTATT, IKKE_OPPFYLT, BEREGN_INNTEKT
        }

        fun onEntry() {}
        fun onExit() {}
        fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            error("Forventet ikke søknad i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun toFrontendTilstand() = tilstandsnavn.name
    }

    private object Start : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.START
        override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            //opprett initielle vilkårsvurderinger
            sak.vilkårsvurderinger.add(Paragraf_11_2())
            sak.vilkårsvurderinger.add(Paragraf_11_3())
            sak.vilkårsvurderinger.add(Paragraf_11_4FørsteLedd())
            sak.vilkårsvurderinger.add(Paragraf_11_4AndreOgTredjeLedd())
            sak.vilkårsvurderinger.add(Paragraf_11_5())
            sak.vilkårsvurderinger.add(Paragraf_11_6())
            sak.vilkårsvurderinger.add(Paragraf_11_12FørsteLedd())
            sak.vilkårsvurderinger.forEach { it.håndterSøknad(søknad, fødselsdato, vurderingsdato) }

            vurderNestetilstand(sak)
        }

        private fun vurderNestetilstand(sak: Sak) {
            when {
                sak.vilkårsvurderinger.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt)
                else -> sak.tilstand(SøknadMottatt)
            }
        }
    }

    private object SøknadMottatt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.SØKNAD_MOTTATT
        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak)
        }

        private fun vurderNesteTilstand(sak: Sak) {
            when {
                sak.vilkårsvurderinger.erAlleOppfylt() -> sak.tilstand(BeregnInntekt)
                sak.vilkårsvurderinger.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt)
            }
        }
    }

    private object BeregnInntekt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.BEREGN_INNTEKT
    }

    private object IkkeOppfylt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.IKKE_OPPFYLT
        override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            error("Forventet ikke søknad i tilstand IkkeOppfylt")
        }
    }


    private fun toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) =
        FrontendSak(
            personident = personident.toFrontendPersonident(),
            fødselsdato = fødselsdato.toFrontendFødselsdato(),
            tilstand = tilstand.toFrontendTilstand(),
            vilkårsvurderinger = vilkårsvurderinger.toFrontendVilkårsvurdering()
        )

    internal companion object {
        internal fun Iterable<Sak>.toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) = map {
            it.toFrontendSak(personident = personident, fødselsdato = fødselsdato)
        }
    }
}
