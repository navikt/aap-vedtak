package no.nav.aap.domene.entitet

import java.time.LocalDate

class Fødselsdato(private val dato: LocalDate) {
    private val `18ÅrsDagen`: LocalDate = this.dato.plusYears(18)
    private val `25ÅrsDagen`: LocalDate = this.dato.plusYears(25)
    private val `67ÅrsDagen`: LocalDate = this.dato.plusYears(67)

    private companion object{
        private const val MINSTE_G_OVER_25 = 2.0
        private const val MINSTE_G_UNDER_25 = 4.0 / 3
    }

    internal fun erMellom18Og67År(vurderingsdato: LocalDate) = vurderingsdato in `18ÅrsDagen`..`67ÅrsDagen`

    internal fun justerBeregningsfaktorForAlder(dato: LocalDate, beregningsfaktor: Double): Double {
        val minsteGForAlder = if(dato < `25ÅrsDagen`) MINSTE_G_UNDER_25 else MINSTE_G_OVER_25
        return maxOf(beregningsfaktor, minsteGForAlder)
    }

    internal fun toFrontendFødselsdato() = dato
    internal fun toDto() = dato
}
