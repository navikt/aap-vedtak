package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_4AndreOgTredjeLedd
import java.time.LocalDate

internal class Paragraf_11_4AndreOgTredjeLedd :
    Vilkårsvurdering(Paragraf.PARAGRAF_11_4, Ledd.LEDD_2 + Ledd.LEDD_3) {
    private lateinit var løsning: LøsningParagraf_11_4AndreOgTredjeLedd

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
        tilstand.håndterLøsning(this, løsning)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        private val name: String,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        internal open fun onEntry(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $name")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            løsning: LøsningParagraf_11_4AndreOgTredjeLedd
        ) {
            error("Oppgave skal ikke håndteres i tilstand $name")
        }

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }
        }

        object SøknadMottatt : Tilstand(name = "SØKNAD_MOTTATT", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd, hendelse: Hendelse) {
                hendelse.opprettBehov(Behov_11_4AndreOgTredjeLedd())
            }

            override fun håndterLøsning(
                vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
                løsning: LøsningParagraf_11_4AndreOgTredjeLedd
            ) {
                vilkårsvurdering.løsning = løsning
                if (løsning.erManueltOppfylt()) {
                    vilkårsvurdering.tilstand(Oppfylt, løsning)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
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
