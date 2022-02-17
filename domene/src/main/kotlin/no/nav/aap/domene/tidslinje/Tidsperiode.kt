package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.tidslinje.Dag.Companion.getDagsatsFor
import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import java.time.LocalDate

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
