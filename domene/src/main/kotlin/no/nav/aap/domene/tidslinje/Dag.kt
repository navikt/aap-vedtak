package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import java.time.DayOfWeek
import java.time.LocalDate

internal class Dag(
    private val dato: LocalDate,
    grunnlag: Beløp,
    private val barnetillegg: Beløp
) {
    private val grunnlag: Beløp = if (dato.erHelg()) 0.beløp else grunnlag

    private val minsteDagsats = this.grunnlag * 0.66 / 260
    private val høyesteDagsats = this.grunnlag * 0.9 / 260
    private val dagsats: Beløp = minOf(høyesteDagsats, minsteDagsats + barnetillegg)

    companion object {
        fun Iterable<Dag>.getDagsatsFor(date: LocalDate) = this.first { it.dato == date }.dagsats
    }

    fun getDagsats() = dagsats

    private fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
}
