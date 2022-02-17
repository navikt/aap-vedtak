package no.nav.aap.domene

import no.nav.aap.domene.Dag.Companion.getDagsatsFor
import no.nav.aap.domene.Tidsperiode.Companion.summerDagsatser
import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import java.time.DayOfWeek
import java.time.LocalDate

internal class Tidslinje private constructor(
    private val grunnlag: Inntektsgrunnlag
) {

    private val tidsperioder: MutableList<Tidsperiode> = mutableListOf()

    companion object {
        fun opprettTidslinje(virkningsdato: LocalDate, grunnlag: Inntektsgrunnlag, barnehage: Barnehage): Tidslinje {
            val tidslinje = Tidslinje(grunnlag)
            tidslinje.tidsperioder.add(Tidsperiode.fyllPeriodeMedDager(virkningsdato, grunnlag, barnehage))
            return tidslinje
        }
    }

    fun summerTidslinje() = tidsperioder.summerDagsatser()

}

internal class Tidsperiode private constructor(
    private val dager: List<Dag>
) {

    companion object {
        private const val DAGER_I_EN_PERIODE = 14L

        fun fyllPeriodeMedDager(fraDato: LocalDate, grunnlag: Inntektsgrunnlag, barnehage: Barnehage) =
            Tidsperiode((0 until DAGER_I_EN_PERIODE)
                .map(fraDato::plusDays)
                .map { dato -> Dag(dato, grunnlag.grunnlagForDag(dato), barnehage.barnetilleggForDag(dato)) })

        fun Iterable<Tidsperiode>.summerDagsatser() = this.map { it.summerDagsatserForPeriode() }.summerBeløp()

    }

    fun summerDagsatserForPeriode() = dager.map { it.getDagsats() }.summerBeløp()

    fun getDagsatsFor(date: LocalDate) = dager.getDagsatsFor(date)

}


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