package no.nav.aap.domene.beregning

import no.nav.aap.januar
import no.nav.aap.juli
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InntektshistorikkTest {

    @Test
    fun `Hvis vi beregner grunnlag for en bruker uten inntekt, blir grunnlaget 0`() {
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(emptyList())
        val beregning = inntektshistorikk.beregnGrunnlag(1.januar)

        val expected = Grunnlagsberegning(
            Beløp(0.0),
            emptyList(),
            emptyList()
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i år, blir grunnlaget 0`() {
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(listOf(Inntekt(Arbeidsgiver(), januar(2022), Beløp(1000.0))))
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(0.0),
            emptyList(),
            emptyList()
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i forrige kalenderår`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(1016.0720424767944),
            inntekter,
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt over 6G forrige kalenderår, blir beløp G-regulert`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(638394.0),
            inntekter,
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i 2020`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(1054.9909273893686 / 3),
            emptyList(),
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har inntekt i 3 kalenderår`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(1000.0)), //1076.1940404183
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)), //1054.9909273894
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0))  //1016.0720424768
        )
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(3147.2570102845066 / 3),
            inntekter.takeLast(1),
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har inntekt i 3 kalenderår - høyere i 2019`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(4000.0)),
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)),
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0))
        )
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(6375.839131539539 / 3),
            inntekter.takeLast(1),
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har inntekt i 3 kalenderår - alle over 6G`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(700000.0)),
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(800000.0)),
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(900000.0))
        )
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(1915182.0 / 3),
            inntekter.takeLast(1),
            inntekter
        )

        assertEquals(expected, beregning)
    }

//    2016	645.246 kroner	550.440 kroner	550.440 kroner	561.804 kroner
//    2015	459.248 kroner	537.012 kroner	459.248 kroner	480.450 kroner
//    2014	540.527 kroner	523.968 kroner	523.968 kroner	561.804 kroner

    @Test
    fun `Har inntekt i 3 kalenderår - fra rundskriv`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2014), Beløp(540527.0)),
            Inntekt(Arbeidsgiver(), januar(2015), Beløp(459248.0)),
            Inntekt(Arbeidsgiver(), januar(2016), Beløp(645246.0))
        )
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 juli 2017)

//        (561804.0 + 480449.90315300215 + 561804.0) / 3
//        Summen over er lavere enn oppjustert 2016 inntekt, så beløpet for 2016 brukes

        val expected = Grunnlagsberegning(
            Beløp(561804.0),
            inntekter.takeLast(1),
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker har snittinntekt over 3 år som er høyere enn siste år`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2014), Beløp(540527.0)),
            Inntekt(Arbeidsgiver(), januar(2015), Beløp(459248.0)),
            Inntekt(Arbeidsgiver(), januar(2016), Beløp(445700.0))
        )
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 juli 2017)

        val expected = Grunnlagsberegning(
            Beløp(499051.83807592624),
            inntekter.takeLast(1),
            inntekter
        )

        assertEquals(expected, beregning)
    }
}
