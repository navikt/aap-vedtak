package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import java.time.Year
import java.time.YearMonth

class Inntekt(
    private val arbeidsgiver: Arbeidsgiver,
    private val inntekstmåned: YearMonth,
    private val beløp: Beløp
) {
    internal companion object {
        internal fun Iterable<Inntekt>.inntektSisteKalenderår(år: Year) = filter { Year.from(it.inntekstmåned) == år }

        internal fun Iterable<Inntekt>.inntektSiste3Kalenderår(år: Year) = this
            .filter { Year.from(it.inntekstmåned) in år.minusYears(2)..år }
            .groupBy { Year.from(it.inntekstmåned) }

        internal fun Iterable<Inntekt>.summerInntekt() = map { it.beløp }.summerBeløp()
    }
}
