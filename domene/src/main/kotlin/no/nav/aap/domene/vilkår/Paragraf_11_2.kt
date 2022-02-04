package no.nav.aap.domene.vilkår

import no.nav.aap.domene.Lytter
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.hendelse.Søknad
import java.time.LocalDate

internal class Paragraf_11_2(lytter: Lytter = object : Lytter {}) :
    Vilkårsvurdering(lytter, Paragraf.PARAGRAF_11_2, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var maskineltLøsning: LøsningParagraf_11_2
    private lateinit var manueltLøsning: LøsningParagraf_11_2

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand.onExit(this)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this)
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
        private val name: String,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        internal open fun onEntry(vilkårsvurdering: Paragraf_11_2) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_2) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $name")
        }

        internal open fun vurderMedlemskap(
            vilkårsvurdering: Paragraf_11_2,
            løsning: LøsningParagraf_11_2
        ) {
            error("Oppgave skal ikke håndteres i tilstand $name")
        }

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_2,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt)
            }
        }

        object SøknadMottatt : Tilstand(name = "SØKNAD_MOTTATT", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2) {
                //send ut oppgave for innhenting av maskinell medlemskapsvurdering
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.maskineltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(Oppfylt)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfylt)
                    else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs)
                }
            }
        }

        object ManuellVurderingTrengs :
            Tilstand(name = "MANUELL_VURDERING_TRENGS", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2) {
                //send ut oppgave for manuell vurdering av medlemskap
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.manueltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(Oppfylt)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfylt)
                    else -> error("Veileder/saksbehandler må ta stilling til om bruker er medlem eller ikke")
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
