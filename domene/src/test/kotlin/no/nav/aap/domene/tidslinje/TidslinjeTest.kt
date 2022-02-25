package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Year

internal class TidslinjeTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Når man oppretter en ny tidslinje med startdato og grunnlag, får man en periode med dager med beløp`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)

        val tidslinje = Tidslinje.opprettTidslinje(1 januar 2017, grunnlag, Barnehage(emptyList()))

        assertEquals(14100.beløp, tidslinje.summerTidslinje())
    }

    @Test
    fun `Når det legges til barn i tidslinja, blir barnetillegget lagt til dagsatsen`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)

        val barnehage = Barnehage(
            listOf(
                Barnehage.Barn(Fødselsdato(1 januar 2012))
            )
        )

        val tidslinje = Tidslinje.opprettTidslinje(1 januar 2017, grunnlag, barnehage)

        assertEquals((14100 + 270).beløp, tidslinje.summerTidslinje())
    }

    private fun Iterable<Inntekt>.inntektsgrunnlag(
        beregningsdato: LocalDate,
        fødselsdato: Fødselsdato = Fødselsdato(1 januar 1970)
    ) =
        Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato,
            this.inntektSiste3Kalenderår(Year.from(beregningsdato).minusYears(1)),
            fødselsdato
        )
}
