package no.nav.aap.domene.beregning

import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Year

internal class InntektshistorikkTest {

    @Test
    fun `Hvis vi henter grunnlag for en bruker uten inntekt, blir inntektsgrunnlaget tomt`() {
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(emptyList())
        val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022)

        val expected = Inntektsgrunnlag(
            sisteKalenderår = Year.of(2021),
            inntekterSiste3Kalenderår = emptyList()
        )

        assertEquals(expected, inntektsgrunnlag)
    }

    @Test
    fun `Hvis bruker kun har inntekt i år, blir inntektsgrunnlaget tomt`() {
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(listOf(Inntekt(Arbeidsgiver(), januar(2022), Beløp(1000.0))))
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022)

        val expected = Inntektsgrunnlag(
            sisteKalenderår = Year.of(2021),
            inntekterSiste3Kalenderår = emptyList()
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i forrige kalenderår`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2021), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022)

        val expected = Inntektsgrunnlag(
            sisteKalenderår = Year.of(2021),
            inntekterSiste3Kalenderår = listOf(InntektsgrunnlagForÅr(Year.of(2021), inntekter))
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i 2020`() {
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022)

        val expected = Inntektsgrunnlag(
            sisteKalenderår = Year.of(2021),
            inntekterSiste3Kalenderår = listOf(InntektsgrunnlagForÅr(Year.of(2020), inntekter))
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
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022)

        val expected = Inntektsgrunnlag(
            sisteKalenderår = Year.of(2021),
            inntekterSiste3Kalenderår = listOf(
                InntektsgrunnlagForÅr(Year.of(2019), listOf(inntekter[0])),
                InntektsgrunnlagForÅr(Year.of(2020), listOf(inntekter[1])),
                InntektsgrunnlagForÅr(Year.of(2021), listOf(inntekter[2]))
            )
        )

        assertEquals(expected, beregning)
    }
}
