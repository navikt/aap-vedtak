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
import no.nav.aap.hendelse.behov.BehovInntekter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

internal class Sak private constructor(
    private var tilstand: Tilstand,
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering>,
    private val inntektshistorikk: Inntektshistorikk,
) {
    private lateinit var vurderingAvBeregningsdato: VurderingAvBeregningsdato
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

    internal fun håndterLøsning(løsning: LøsningVurderingAvBeregningsdato) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
        tilstand.håndterLøsning(this, løsning, fødselsdato)
    }

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        nyTilstand.onExit(this, hendelse)
        tilstand = nyTilstand
        tilstand.onEntry(this, hendelse)
    }

    private sealed interface Tilstand {
        val tilstandsnavn: Tilstandsnavn

        enum class Tilstandsnavn {
            START,
            SØKNAD_MOTTATT,
            BEREGN_INNTEKT,
            VEDTAK_FATTET,
            IKKE_OPPFYLT
        }

        fun onEntry(sak: Sak, hendelse: Hendelse) {}
        fun onExit(sak: Sak, hendelse: Hendelse) {}
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

        fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
            error("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            error("Forventet ikke løsning på inntekter i tilstand ${tilstandsnavn.name}")
        }

        fun toFrontendSak(sak: Sak, personident: Personident, fødselsdato: Fødselsdato) =
            FrontendSak(
                personident = personident.toFrontendPersonident(),
                fødselsdato = fødselsdato.toFrontendFødselsdato(),
                tilstand = tilstandsnavn.name,
                vilkårsvurderinger = sak.vilkårsvurderinger.toFrontendVilkårsvurdering(),
                vedtak = null
            )

        fun toDto(sak: Sak) = DtoSak(
            tilstand = tilstandsnavn.name,
            vilkårsvurderinger = sak.vilkårsvurderinger.toDto(),
            vurderingsdato = sak.vurderingsdato, // ALLTID SATT
            vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
            vedtak = null
        )

        fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
            sak.vurderingsdato = dtoSak.vurderingsdato
            sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
        }
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

            sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato()
            sak.vurderingAvBeregningsdato.håndterSøknad(søknad)

            vurderNestetilstand(sak, søknad)
        }

        private fun vurderNestetilstand(sak: Sak, søknad: Søknad) {
            when {
                sak.vilkårsvurderinger.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt, søknad)
                else -> sak.tilstand(SøknadMottatt, søknad)
            }
        }
    }

    private object SøknadMottatt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.SØKNAD_MOTTATT
        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
            sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
            vurderNesteTilstand(sak, løsning)
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
            sak.vurderingAvBeregningsdato.håndterLøsning(løsning)
            vurderNesteTilstand(sak, løsning)
        }

        private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
            when {
                sak.vilkårsvurderinger.erAlleOppfylt() && sak.vurderingAvBeregningsdato.erFerdig() ->
                    sak.tilstand(BeregnInntekt, hendelse)
                sak.vilkårsvurderinger.erNoenIkkeOppfylt() ->
                    sak.tilstand(IkkeOppfylt, hendelse)
            }
        }
    }

    private object BeregnInntekt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.BEREGN_INNTEKT

        override fun onEntry(sak: Sak, hendelse: Hendelse) {
            hendelse.opprettBehov(
                BehovInntekter(
                    fom = Year.from(sak.vurderingAvBeregningsdato.beregningsdato()).minusYears(3),
                    tom = Year.from(sak.vurderingAvBeregningsdato.beregningsdato()).minusYears(1)
                )
            )
        }

        override fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            løsning.lagreInntekter(sak.inntektshistorikk)
            val inntektsgrunnlag =
                sak.inntektshistorikk.finnInntektsgrunnlag(sak.vurderingAvBeregningsdato.beregningsdato(), fødselsdato)
            sak.vedtak = Vedtak(
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                søknadstidspunkt = LocalDateTime.now(),
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )

            sak.tilstand(VedtakFattet, løsning)
        }
    }

    private object VedtakFattet : Tilstand {
        override val tilstandsnavn: Tilstand.Tilstandsnavn = Tilstand.Tilstandsnavn.VEDTAK_FATTET

        override fun toDto(sak: Sak) = DtoSak(
            tilstand = tilstandsnavn.name,
            vilkårsvurderinger = sak.vilkårsvurderinger.toDto(),
            vurderingsdato = sak.vurderingsdato, // ALLTID SATT
            vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
            vedtak = sak.vedtak.toDto()
        )

        override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
            sak.vurderingsdato = dtoSak.vurderingsdato
            sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
            val dtoVedtak = requireNotNull(dtoSak.vedtak)
            sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
        }

        override fun toFrontendSak(sak: Sak, personident: Personident, fødselsdato: Fødselsdato) =
            FrontendSak(
                personident = personident.toFrontendPersonident(),
                fødselsdato = fødselsdato.toFrontendFødselsdato(),
                tilstand = tilstandsnavn.name,
                vilkårsvurderinger = sak.vilkårsvurderinger.toFrontendVilkårsvurdering(),
                vedtak = sak.vedtak.toFrontendVedtak()
            )
    }

    private object IkkeOppfylt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.IKKE_OPPFYLT
        override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            error("Forventet ikke søknad i tilstand IkkeOppfylt")
        }
    }

    private fun toDto() = tilstand.toDto(this)

    private fun toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) =
        tilstand.toFrontendSak(this, personident, fødselsdato)

    internal companion object {
        internal fun Iterable<Sak>.toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) = map {
            it.toFrontendSak(personident = personident, fødselsdato = fødselsdato)
        }

        internal fun Iterable<Sak>.toDto() = map { sak -> sak.toDto() }

        internal fun gjenopprett(dtoSak: DtoSak): Sak = Sak(
            vilkårsvurderinger = dtoSak.vilkårsvurderinger.mapNotNull(Vilkårsvurdering::gjenopprett).toMutableList(),
            tilstand = when (Tilstand.Tilstandsnavn.valueOf(dtoSak.tilstand)) {
                Tilstand.Tilstandsnavn.START -> Start
                Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
                Tilstand.Tilstandsnavn.BEREGN_INNTEKT -> BeregnInntekt
                Tilstand.Tilstandsnavn.VEDTAK_FATTET -> VedtakFattet
                Tilstand.Tilstandsnavn.IKKE_OPPFYLT -> IkkeOppfylt
            },
            inntektshistorikk = Inntektshistorikk()
        ).apply {
            this.tilstand.gjenopprettTilstand(this, dtoSak)
        }
    }
}
