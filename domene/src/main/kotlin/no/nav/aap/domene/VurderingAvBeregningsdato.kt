package no.nav.aap.domene

import no.nav.aap.hendelse.LøsningVurderingAvBeregningsdato
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.BehovVurderingAvBeregningsdato
import java.time.LocalDate

internal class VurderingAvBeregningsdato {

    private var tilstand: Tilstand = Tilstand.Start
    private lateinit var løsning: LøsningVurderingAvBeregningsdato

    internal fun håndterSøknad(søknad: Søknad) {
        tilstand.håndterSøknad(this, søknad)
    }

    internal fun håndterLøsning(løsning: LøsningVurderingAvBeregningsdato) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun beregningsdato() = tilstand.beregningsdato(this)

    internal sealed class Tilstand {
        internal open fun håndterSøknad(vurderingAvBeregningsdato: VurderingAvBeregningsdato, søknad: Søknad) {}
        internal open fun håndterLøsning(
            vurderingAvBeregningsdato: VurderingAvBeregningsdato,
            løsning: LøsningVurderingAvBeregningsdato
        ) {
        }

        internal open fun beregningsdato(vurderingAvBeregningsdato: VurderingAvBeregningsdato): LocalDate =
            error("Kan ikke hente beregningsdato uten løsning")

        internal object Start : Tilstand() {
            override fun håndterSøknad(vurderingAvBeregningsdato: VurderingAvBeregningsdato, søknad: Søknad) {
                søknad.opprettBehov(BehovVurderingAvBeregningsdato())
                vurderingAvBeregningsdato.tilstand = SøknadMottatt
            }
        }

        internal object SøknadMottatt : Tilstand() {
            override fun håndterLøsning(
                vurderingAvBeregningsdato: VurderingAvBeregningsdato,
                løsning: LøsningVurderingAvBeregningsdato
            ) {
                vurderingAvBeregningsdato.løsning = løsning
                vurderingAvBeregningsdato.tilstand = Ferdig
            }
        }

        internal object Ferdig : Tilstand() {
            override fun beregningsdato(vurderingAvBeregningsdato: VurderingAvBeregningsdato): LocalDate =
                vurderingAvBeregningsdato.løsning.beregningsdato
        }
    }
}
