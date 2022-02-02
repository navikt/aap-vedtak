package no.nav.aap.domene.entitet

import java.time.LocalDate

class Fødselsdato(private val dato: LocalDate) {
    private val `18ÅrsDagen`: LocalDate = this.dato.plusYears(18)
    private val `67ÅrsDagen`: LocalDate = this.dato.plusYears(67)

    internal fun erMellom18Og67År(vurderingsdato: LocalDate) = vurderingsdato in `18ÅrsDagen`..`67ÅrsDagen`

    internal fun toFrontendFødselsdato() = dato
}
