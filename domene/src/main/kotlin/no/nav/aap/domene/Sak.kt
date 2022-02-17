package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.*
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erAlleOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erNoenIkkeOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toFrontendVilkårsvurdering
import no.nav.aap.dto.DtoSak
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.hendelse.*
import java.time.LocalDate

internal class Sak private constructor(
    private var tilstand: Tilstand,
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering>,
    private val inntektshistorikk: Inntektshistorikk,
) {
    private lateinit var vurderingsdato: LocalDate
    private lateinit var vedtak: Vedtak

    constructor() : this(Start, mutableListOf(), Inntektshistorikk())

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

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
        tilstand.håndterLøsning(this, løsning, fødselsdato)
    }

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

        fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            error("Forventet ikke løsning på inntekter i tilstand ${tilstandsnavn.name}")
        }

        fun toFrontendTilstand() = tilstandsnavn.name
        fun toDto() = tilstandsnavn.name
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
            sak.vilkårsvurderinger.add(Paragraf_11_29())
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

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
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

        override fun onEntry() {
            // Be om inntekter
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            løsning.lagreInntekter(sak.inntektshistorikk)
            val grunnlagsberegning = sak.inntektshistorikk.finnInntektsgrunnlag(sak.vurderingsdato, fødselsdato)


        }
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

        internal fun Iterable<Sak>.toDto() = map { sak ->
            DtoSak(
                tilstand = sak.tilstand.toDto(),
                vilkårsvurderinger = sak.vilkårsvurderinger.toDto(),
                vurderingsdato = sak.vurderingsdato // ALLTID SATT
            )
        }

        internal fun create(sak: DtoSak): Sak = Sak(
            vilkårsvurderinger = sak.vilkårsvurderinger.mapNotNull(Vilkårsvurdering::create).toMutableList(), // todo: map
            tilstand = when (Tilstand.Tilstandsnavn.valueOf(sak.tilstand)) {
                Tilstand.Tilstandsnavn.BEREGN_INNTEKT -> BeregnInntekt
                Tilstand.Tilstandsnavn.START -> Start
                Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
                Tilstand.Tilstandsnavn.IKKE_OPPFYLT -> IkkeOppfylt
            },
            inntektshistorikk = Inntektshistorikk()
        ).apply {
            vurderingsdato = sak.vurderingsdato
        }
    }
}