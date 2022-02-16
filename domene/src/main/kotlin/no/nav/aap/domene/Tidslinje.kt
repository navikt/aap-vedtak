package no.nav.aap.domene

import no.nav.aap.domene.Dag.Companion.getDagsatsFor
import no.nav.aap.domene.Tidsperiode.Companion.summerDagsatser
import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import java.time.DayOfWeek
import java.time.LocalDate

internal class Tidslinje private constructor (
    private val grunnlag: Inntektsgrunnlag
) {

    private val tidsperioder: MutableList<Tidsperiode> = mutableListOf()

    companion object {
        fun opprettTidslinje(virkningsdato: LocalDate, grunnlag: Inntektsgrunnlag): Tidslinje {
            val tidslinje = Tidslinje(grunnlag)
            tidslinje.tidsperioder.add(Tidsperiode.fyllPeriodeMedDager(virkningsdato, grunnlag))
            return tidslinje
        }
    }

    fun summerTidslinje() = tidsperioder.summerDagsatser()

}

internal class Tidsperiode private constructor(
    private val dager: List<Dag>
){

    companion object {
        private const val DAGER_I_EN_PERIODE = 14L

        fun fyllPeriodeMedDager(fraDato: LocalDate, grunnlag: Inntektsgrunnlag) =
            Tidsperiode((0 until DAGER_I_EN_PERIODE).map { fraDato.plusDays(it) }.map { Dag(it, grunnlag.grunnlagForDag(it)) })

        fun Iterable<Tidsperiode>.summerDagsatser() = this.map { it.summerDagsatserForPeriode() }.summerBeløp()

    }

    fun summerDagsatserForPeriode() = dager.map { it.getDagsats() }.summerBeløp()

    fun getDagsatsFor(date: LocalDate) = dager.getDagsatsFor(date)

}


internal class Dag (
    private val dato: LocalDate,
    private val grunnlag: Beløp
) {

    private lateinit var dagsats: Beløp

    companion object {
        fun Iterable<Dag>.getDagsatsFor(date: LocalDate) = this.first { it.dato == date }.dagsats
    }

    init {
        beregnDagsats()
    }

    fun beregnDagsats() {
        dagsats = if(dato.erHelg()) {
            0.beløp
        } else {
            (grunnlag * 0.66) / 260

        }
    }

    fun getDagsats() = dagsats

    private fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
}