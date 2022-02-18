package no.nav.aap.domene.entitet

import java.time.LocalDate

class Fødselsdato(private val dato: LocalDate) {
    private val `18ÅrsDagen`: LocalDate = this.dato.plusYears(18)
    private val `25ÅrsDagen`: LocalDate = this.dato.plusYears(25)
    private val `62ÅrsDagen`: LocalDate = this.dato.plusYears(62)
    private val `67ÅrsDagen`: LocalDate = this.dato.plusYears(67)

    private companion object {
        private const val MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR = 2.0 / .66
        private const val MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR = 4.0 / 3 / .66
    }

    internal fun erMellom18Og67År(vurderingsdato: LocalDate) = vurderingsdato in `18ÅrsDagen`..`67ÅrsDagen`
    internal fun erUnder18År(dato: LocalDate) = dato < `18ÅrsDagen`
    internal fun erUnder62(dato: LocalDate) = dato < `62ÅrsDagen`

    internal fun justerGrunnlagsfaktorForAlder(dato: LocalDate, beregningsfaktor: Double): Double {
        val minsteGrunnlagsfaktorForAlder =
            if (dato < `25ÅrsDagen`) MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR else MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR
        return maxOf(beregningsfaktor, minsteGrunnlagsfaktorForAlder)
    }

    internal fun toFrontendFødselsdato() = dato
    internal fun toDto() = dato
}
