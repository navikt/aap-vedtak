package no.nav.aap.domene.beregning

import no.nav.aap.januar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InntektshistorikkTest {

    @Test
    fun `Har ingen inntekt`() {
        val inntektshistorikk = Inntektshistorikk(emptyList())
        val beregning = inntektshistorikk.beregnGrunnlag(1.januar)

        val expected = Grunnlagsberegning(
            Beløp(0.0),
            emptyList(),
            emptyList()
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har kun inntekt i år`() {
        val inntektshistorikk = Inntektshistorikk(listOf(Inntekt(Arbeidsgiver(), januar(2022), Beløp(1000.0))))
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(0.0),
            emptyList(),
            emptyList()
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har kun inntekt forrige kalenderår`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(1000.0),
            inntekter,
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har kun inntekt i 2020`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(1000.0 / 3),
            emptyList(),
            inntekter
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har inntekt i 3 kalenderår`() {
        val inntekter = listOf(
            Inntekt(Arbeidsgiver(), januar(2019), Beløp(1000.0)),
            Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)),
            Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0))
        )
        val inntektshistorikk = Inntektshistorikk(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(1000.0),
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
        val inntektshistorikk = Inntektshistorikk(inntekter)
        val beregning = inntektshistorikk.beregnGrunnlag(1 januar 2022)

        val expected = Grunnlagsberegning(
            Beløp(6000.0 / 3),
            inntekter.takeLast(1),
            inntekter
        )

        assertEquals(expected, beregning)
    }
}