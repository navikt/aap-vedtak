package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.tidslinje.Tidsperiode.Companion.summerDagsatser
import no.nav.aap.domene.beregning.Inntektsgrunnlag
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
