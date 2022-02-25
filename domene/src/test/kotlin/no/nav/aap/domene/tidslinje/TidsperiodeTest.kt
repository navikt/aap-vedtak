package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.Year

internal class TidsperiodeTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Når man oppretter en ny tidsperiode med dato og grunnlag, får man en liste med dager med beløp`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)

        val tidsperiode = Tidsperiode.fyllPeriodeMedDager(1 januar 2017, grunnlag, Barnehage(emptyList()))

        assertEquals(0.beløp, tidsperiode.getDagsatsFor(1 januar 2017))
        assertEquals(1410.beløp, tidsperiode.getDagsatsFor(2 januar 2017))
        assertEquals(1410.beløp, tidsperiode.getDagsatsFor(13 januar 2017))
        assertEquals(0.beløp, tidsperiode.getDagsatsFor(14 januar 2017))
        assertEquals(14100.beløp, tidsperiode.summerDagsatserForPeriode())
        assertThrows<NoSuchElementException> { tidsperiode.getDagsatsFor(15 januar 2017) }
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
