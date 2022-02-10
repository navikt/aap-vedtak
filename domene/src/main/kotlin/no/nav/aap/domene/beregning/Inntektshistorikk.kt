package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSisteKalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import java.time.LocalDate
import java.time.Year

internal class Inntektshistorikk(
    private val inntekter: List<Inntekt>
) {
    private companion object {
        private const val ANTALL_ÅR_FOR_GJENNOMSNITT = 3
    }

    internal fun beregnGrunnlag(beregningsdato: LocalDate): Grunnlagsberegning {
        val fjor = Year.from(beregningsdato).minusYears(1)
        val inntekterSisteKalenderår = inntektSisteKalenderår(fjor)
        val sumInntekterSisteKalenderår = inntekterSisteKalenderår.summerInntekt()
        val inntekterSiste3Kalenderår = inntektSiste3Kalenderår(fjor)
        val sumInntekterSiste3Kalenderår = inntekterSiste3Kalenderår.summerInntekt()

        val grunnlag = maxOf(sumInntekterSisteKalenderår, sumInntekterSiste3Kalenderår / ANTALL_ÅR_FOR_GJENNOMSNITT)

        return Grunnlagsberegning(
            grunnlag = grunnlag,
            inntekterSisteKalenderår = inntekterSisteKalenderår,
            inntekterSiste3Kalenderår = inntekterSiste3Kalenderår
        )
    }

    private fun inntektSisteKalenderår(år: Year): List<Inntekt> = inntekter.inntektSisteKalenderår(år)

    private fun inntektSiste3Kalenderår(år: Year): List<Inntekt> = inntekter.inntektSiste3Kalenderår(år)

}
