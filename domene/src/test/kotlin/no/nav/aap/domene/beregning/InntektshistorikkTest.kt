package no.nav.aap.domene.beregning

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Year

internal class InntektshistorikkTest {

    private companion object {
        private val FØDSELSDATO = Fødselsdato(1 januar 1970)
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis vi henter grunnlag for en bruker uten inntekt, blir inntektsgrunnlaget tomt`() {
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(emptyList())
        val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022, FØDSELSDATO)

        val expected = Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato = 1 januar 2022,
            inntekterSiste3Kalenderår = emptyList(),
            fødselsdato = FØDSELSDATO
        )

        assertEquals(expected, inntektsgrunnlag)
    }

    @Test
    fun `Hvis bruker kun har inntekt i år, blir inntektsgrunnlaget tomt`() {
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(listOf(Inntekt(ARBEIDSGIVER, januar(2022), Beløp(1000.0))))
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022, FØDSELSDATO)

        val expected = Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato = 1 januar 2022,
            inntekterSiste3Kalenderår = emptyList(),
            fødselsdato = FØDSELSDATO
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i forrige kalenderår`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2021), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022, FØDSELSDATO)

        val expected = Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato = 1 januar 2022,
            inntekterSiste3Kalenderår = listOf(InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2021), inntekter)),
            fødselsdato = FØDSELSDATO
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Hvis bruker kun har inntekt i 2020`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2020), Beløp(1000.0)))
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022, FØDSELSDATO)

        val expected = Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato = 1 januar 2022,
            inntekterSiste3Kalenderår = listOf(InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2020), inntekter)),
            fødselsdato = FØDSELSDATO
        )

        assertEquals(expected, beregning)
    }

    @Test
    fun `Har inntekt i 3 kalenderår`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2019), Beløp(1000.0)), //1076.1940404183
            Inntekt(ARBEIDSGIVER, januar(2020), Beløp(1000.0)), //1054.9909273894
            Inntekt(ARBEIDSGIVER, januar(2021), Beløp(1000.0))  //1016.0720424768
        )
        val inntektshistorikk = Inntektshistorikk()
        inntektshistorikk.leggTilInntekter(inntekter)
        val beregning = inntektshistorikk.finnInntektsgrunnlag(1 januar 2022, FØDSELSDATO)

        val expected = Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato = 1 januar 2022,
            inntekterSiste3Kalenderår = listOf(
                InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2019), listOf(inntekter[0])),
                InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2020), listOf(inntekter[1])),
                InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2021), listOf(inntekter[2]))
            ),
            fødselsdato = FØDSELSDATO
        )

        assertEquals(expected, beregning)
    }
}
