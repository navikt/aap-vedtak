package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Year

internal class TidslinjeTest {

    private fun Iterable<Inntekt>.inntektsgrunnlag(år: Year, fødselsdato: Fødselsdato = Fødselsdato(1 januar 1970)) =
        Inntektsgrunnlag(år, this.inntektSiste3Kalenderår(år), fødselsdato)

    @Test
    fun `Når man oppretter en ny tidslinje med startdato og grunnlag, får man en periode med dager med beløp`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2014), 540527.beløp),
            Inntekt(Arbeidsgiver(), januar(2015), 459248.beløp),
            Inntekt(Arbeidsgiver(), januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2016))

        val tidslinje = Tidslinje.opprettTidslinje(1 januar 2017, grunnlag, Barnehage(emptyList()))

        Assertions.assertEquals(14100.beløp, tidslinje.summerTidslinje())
    }

    @Test
    fun `Når det legges til barn i tidslinja, blir barnetillegget lagt til dagsatsen`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2014), 540527.beløp),
            Inntekt(Arbeidsgiver(), januar(2015), 459248.beløp),
            Inntekt(Arbeidsgiver(), januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2016))

        val barnehage = Barnehage(
            listOf(
                Barnehage.Barn(Fødselsdato(1 januar 2012))
            )
        )

        val tidslinje = Tidslinje.opprettTidslinje(1 januar 2017, grunnlag, barnehage)

        Assertions.assertEquals((14100 + 270).beløp, tidslinje.summerTidslinje())
    }
}
