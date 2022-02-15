package no.nav.aap.domene.beregning

import no.nav.aap.august
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.mars
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Year

internal class InntektsgrunnlagTest {

    private fun Iterable<Inntekt>.inntektsgrunnlag(år: Year) = Inntektsgrunnlag(år, this.inntektSiste3Kalenderår(år))

    @Test
    fun `Hvis vi beregner grunnlag for en bruker uten inntekt, blir grunnlaget 0`() {
        val grunnlag = Inntektsgrunnlag(Year.of(2021), emptyList())
        assertEquals(0.beløp, grunnlag.grunnlagForDag(1.januar))
    }

    @Test
    fun `Hvis bruker kun har inntekt i år, blir grunnlaget 0`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2022), Beløp(1000.0)))
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        assertEquals(0.beløp, grunnlag.grunnlagForDag(1 januar 2022))
    }

    @Test
    fun `Hvis bruker kun har inntekt i forrige kalenderår`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0)))
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(1016.0720424767943.beløp, grunnlagForDag)
    }

    @Test
    fun `Hvis bruker kun har inntekt over 6G forrige kalenderår, blir beløp G-regulert`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000000.0)))
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(638394.beløp, grunnlagForDag)
    }

    @Test
    fun `Hvis bruker kun har inntekt i 2020`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)))
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(1054.9909273893618.beløp / 3, grunnlagForDag)
    }

    @Test
    fun `Har inntekt i 3 kalenderår`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(1000.0)), //1076.19
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)), //1054.99
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0))  //1016.07
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(3147.25.beløp / 3, grunnlagForDag)
    }

    @Test
    fun `Har inntekt i 3 kalenderår - høyere i 2019`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(4000.0)),
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)),
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0))
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(6375.839131539539.beløp / 3, grunnlagForDag)
    }

    @Test
    fun `Har inntekt i 3 kalenderår - alle over 6G`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(700000.0)),
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(800000.0)),
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(900000.0))
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2021))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(1915182.beløp / 3, grunnlagForDag)
    }

//    2016	645.246 kroner	550.440 kroner	550.440 kroner	561.804 kroner
//    2015	459.248 kroner	537.012 kroner	459.248 kroner	480.450 kroner
//    2014	540.527 kroner	523.968 kroner	523.968 kroner	561.804 kroner

    @Test
    fun `Har inntekt i 3 kalenderår - fra rundskriv`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2014), 540527.beløp),
            Inntekt(Arbeidsgiver(), januar(2015), 459248.beløp),
            Inntekt(Arbeidsgiver(), januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2016))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 juli 2017)

//        (561804.0 + 480449.90315300215 + 561804.0) / 3
//        Summen over er lavere enn oppjustert 2016 inntekt, så beløpet for 2016 brukes

        assertEquals(561804.beløp, grunnlagForDag)
    }

    @Test
    fun `Hvis bruker har snittinntekt over 3 år som er høyere enn siste år`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2014), Beløp(540527.0)),
            Inntekt(Arbeidsgiver(), januar(2015), Beløp(459248.0)),
            Inntekt(Arbeidsgiver(), januar(2016), Beløp(445700.0))
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2016))
        val grunnlagForDag = grunnlag.grunnlagForDag(1 juli 2017)

        assertEquals(499051.83807592624.beløp, grunnlagForDag)
    }

    @Test
    fun `Eksempel 1`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2018), 714273.beløp),
            Inntekt(Arbeidsgiver(), januar(2019), 633576.beløp),
            Inntekt(Arbeidsgiver(), januar(2020), 915454.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2020))
        val grunnlagForDag = grunnlag.grunnlagForDag(19 mars 2022)

        assertEquals(638394.beløp, grunnlagForDag)
    }

    @Test
    fun `Eksempel 2`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2017), 190402.beløp),
            Inntekt(Arbeidsgiver(), januar(2018), 268532.beløp),
            Inntekt(Arbeidsgiver(), januar(2019), 350584.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(Year.of(2019))
        val grunnlagForDag = grunnlag.grunnlagForDag(31 august 2021)

        assertEquals(377296.411466.beløp, grunnlagForDag)
    }
}
