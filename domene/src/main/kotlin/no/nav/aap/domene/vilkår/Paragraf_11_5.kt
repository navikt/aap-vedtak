package no.nav.aap.domene.vilkår

import no.nav.aap.domene.Lytter
import no.nav.aap.domene.Behov
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.hendelse.Søknad
import java.time.LocalDate

internal class Paragraf_11_5(lytter: Lytter = object : Lytter {}) :
    Vilkårsvurdering(lytter, Paragraf.PARAGRAF_11_5, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var løsning: LøsningParagraf_11_5
    private lateinit var nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand.onExit(this)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this)
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
        private val name: String,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        internal open fun onEntry(vilkårsvurdering: Paragraf_11_5) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_5) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $name")
        }

        internal open fun vurderNedsattArbeidsevne(
            vilkårsvurdering: Paragraf_11_5,
            løsning: LøsningParagraf_11_5,
            nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
        ) {
            error("Oppgave skal ikke håndteres i tilstand $name")
        }

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_5,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt)
            }
        }

        object SøknadMottatt : Tilstand(name = "SØKNAD_MOTTATT", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_5) {
                vilkårsvurdering.lytter.sendOppgave(Behov_11_5())
            }

            class Behov_11_5 : Behov

            override fun vurderNedsattArbeidsevne(
                vilkårsvurdering: Paragraf_11_5,
                løsning: LøsningParagraf_11_5,
                nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
            ) {
                vilkårsvurdering.løsning = løsning
                vilkårsvurdering.nedsattArbeidsevnegrad = nedsattArbeidsevnegrad
                if (nedsattArbeidsevnegrad.erNedsattMedMinstHalvparten()) {
                    vilkårsvurdering.tilstand(Oppfylt)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt)
                }
            }

            override fun toFrontendHarÅpenOppgave() = true
        }

        object Oppfylt : Tilstand(
            name = "OPPFYLT",
            erOppfylt = true,
            erIkkeOppfylt = false
        )

        object IkkeOppfylt : Tilstand(
            name = "IKKE_OPPFYLT",
            erOppfylt = false,
            erIkkeOppfylt = true
        )

        internal fun toFrontendTilstand(): String = name
        internal open fun toFrontendHarÅpenOppgave() = false
    }

    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
    override fun toFrontendHarÅpenOppgave() = tilstand.toFrontendHarÅpenOppgave()
}
