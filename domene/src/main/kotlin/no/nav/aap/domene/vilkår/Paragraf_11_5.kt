package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_5
import java.time.LocalDate

internal class Paragraf_11_5 private constructor(private var tilstand: Tilstand) :
    Vilkårsvurdering(Paragraf.PARAGRAF_11_5, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var løsning: LøsningParagraf_11_5
    private lateinit var nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad

    internal constructor() : this(Tilstand.IkkeVurdert)

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_5) {
        løsning.vurderNedsattArbeidsevne(this)
    }

    internal fun vurderNedsattArbeidsevne(
        løsning: LøsningParagraf_11_5,
        nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
    ) {
        tilstand.vurderNedsattArbeidsevne(this, løsning, nedsattArbeidsevnegrad)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            OPPFYLT({ Oppfylt }),
            IKKE_OPPFYLT({ IkkeOppfylt }),
        }

        internal open fun onEntry(vilkårsvurdering: Paragraf_11_5, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_5, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun vurderNedsattArbeidsevne(
            vilkårsvurdering: Paragraf_11_5,
            løsning: LøsningParagraf_11_5,
            nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
        ) {
            error("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_5,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }
        }

        object SøknadMottatt : Tilstand(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_5, hendelse: Hendelse) {
                hendelse.opprettBehov(Behov_11_5())
            }

            override fun vurderNedsattArbeidsevne(
                vilkårsvurdering: Paragraf_11_5,
                løsning: LøsningParagraf_11_5,
                nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
            ) {
                vilkårsvurdering.løsning = løsning
                vilkårsvurdering.nedsattArbeidsevnegrad = nedsattArbeidsevnegrad
                if (nedsattArbeidsevnegrad.erNedsattMedMinstHalvparten()) {
                    vilkårsvurdering.tilstand(Oppfylt, løsning)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
                }
            }

            override fun toFrontendHarÅpenOppgave() = true
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_5_manuell = paragraf.løsning.toDto()
            )

            override fun restoreData(paragraf: Paragraf_11_5, vilkårsvurdering: DtoVilkårsvurdering) {
                val løsning = requireNotNull(vilkårsvurdering.løsning_11_5_manuell)
                paragraf.løsning = LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(løsning.grad))
            }
        }

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_5_manuell = paragraf.løsning.toDto()
            )

            override fun restoreData(paragraf: Paragraf_11_5, vilkårsvurdering: DtoVilkårsvurdering) {
                val løsning = requireNotNull(vilkårsvurdering.løsning_11_5_manuell)
                paragraf.løsning = LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(løsning.grad))
            }
        }

        internal open fun restoreData(paragraf: Paragraf_11_5, vilkårsvurdering: DtoVilkårsvurdering) {}
        internal fun toFrontendTilstand(): String = tilstandsnavn.name
        internal open fun toFrontendHarÅpenOppgave() = false
        internal open fun toDto(paragraf: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            paragraf = paragraf.paragraf.name,
            ledd = paragraf.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name
        )
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)
    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
    override fun toFrontendHarÅpenOppgave() = tilstand.toFrontendHarÅpenOppgave()

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_5 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let(::Paragraf_11_5)
                .apply { this.tilstand.restoreData(this, vilkårsvurdering) }
    }
}
