package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_2
import java.time.LocalDate

internal class Paragraf_11_2 :
    Vilkårsvurdering(Paragraf.PARAGRAF_11_2, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var maskineltLøsning: LøsningParagraf_11_2
    private lateinit var manueltLøsning: LøsningParagraf_11_2

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_2) {
        tilstand.vurderMedlemskap(this, løsning)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        enum class Tilstandsnavn { IKKE_VURDERT, SØKNAD_MOTTATT, MANUELL_VURDERING_TRENGS, IKKE_OPPFYLT, OPPFYLT, }

        internal open fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt
        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun vurderMedlemskap(
            vilkårsvurdering: Paragraf_11_2,
            løsning: LøsningParagraf_11_2
        ) {
            error("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_2,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }
        }

        object SøknadMottatt :
            Tilstand(tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT, erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
                //send ut behov for innhenting av maskinell medlemskapsvurdering
                hendelse.opprettBehov(Behov_11_2())
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.maskineltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(Oppfylt, løsning)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
                    else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, løsning)
                }
            }
        }

        object ManuellVurderingTrengs :
            Tilstand(tilstandsnavn = Tilstandsnavn.MANUELL_VURDERING_TRENGS, erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
                //send ut behov for manuell vurdering av medlemskap
                hendelse.opprettBehov(Behov_11_2()) //FIXME Eget behov for manuell????
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.manueltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(Oppfylt, løsning)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
                    else -> error("Veileder/saksbehandler må ta stilling til om bruker er medlem eller ikke")
                }
            }

            override fun toFrontendHarÅpenOppgave() = true

            override fun toDto(paragraf112: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf112.paragraf.name,
                ledd = paragraf112.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf112.maskineltLøsning.toDto(),
                løsning_11_2_manuell = null,
            )
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf112: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf112.paragraf.name,
                ledd = paragraf112.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf112.maskineltLøsning.toDto(),
                løsning_11_2_manuell = null, // todo
            )
        }

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf112: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf112.paragraf.name,
                ledd = paragraf112.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf112.maskineltLøsning.toDto(),
                løsning_11_2_manuell = null, // todo
            )
        }

        internal open fun toDto(paragraf112: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
            paragraf = paragraf112.paragraf.name,
            ledd = paragraf112.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            null,
            null
        )

        internal fun toFrontendTilstand(): String = tilstandsnavn.name
        internal open fun toFrontendHarÅpenOppgave() = false
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)
    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
    override fun toFrontendHarÅpenOppgave() = tilstand.toFrontendHarÅpenOppgave()

    internal companion object {
        internal fun create(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_2 =
            Paragraf_11_2().apply {
                vilkårsvurdering.løsning_11_2_maskinell?.let {
                    maskineltLøsning = LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.valueOf(it.erMedlem))
                }
                vilkårsvurdering.løsning_11_2_manuell?.let {
                    manueltLøsning = LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.valueOf(it.erMedlem))
                }
                tilstand = when (Tilstand.Tilstandsnavn.valueOf(vilkårsvurdering.tilstand)) {
                    Tilstand.Tilstandsnavn.IKKE_VURDERT -> Tilstand.IkkeVurdert
                    Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> Tilstand.SøknadMottatt
                    Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS -> Tilstand.ManuellVurderingTrengs
                    Tilstand.Tilstandsnavn.IKKE_OPPFYLT -> Tilstand.IkkeOppfylt
                    Tilstand.Tilstandsnavn.OPPFYLT -> Tilstand.Oppfylt
                }
            }
    }
}
