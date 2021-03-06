package no.nav.aap.domene.beregning

import no.nav.aap.*
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Year
import java.time.YearMonth
import java.time.temporal.ChronoUnit

internal class InntektTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis vi summerer en tom liste med inntekter, blir summen 0`() {
        val sum = emptyList<Inntekt>().summerInntekt()
        assertEquals(Beløp(0.0), sum)
    }

    @Test
    fun `Hvis vi summerer en liste med ett beløp på 0, blir summen 0`() {
        val inntekter = inntekterFor(februar(2021) til januar(2021))
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(0.0), sum)
    }

    @Test
    fun `Hvis vi summerer en liste med ett beløp på 1, blir summen 1`() {
        val inntekter = inntekterFor(januar(2021) til januar(2021))
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(1.0), sum)
    }

    @Test
    fun `Hvis vi summerer en liste med to beløp på 1, blir summen 2`() {
        val inntekter = inntekterFor(januar(2021) til februar(2021))
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(2.0), sum)
    }

    @Test
    fun `Summerer en liste med inntekter`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2020), 12000.beløp),
            Inntekt(ARBEIDSGIVER, februar(2020), 13000.beløp),
            Inntekt(ARBEIDSGIVER, mars(2020), 14000.beløp),
            Inntekt(ARBEIDSGIVER, april(2020), 12000.beløp),
            Inntekt(ARBEIDSGIVER, mai(2020), 12000.beløp)
        )
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(63000.0), sum)
    }

    @Test
    fun `Finner ingen innteker for siste tre år når det ikke er noen inntekter`() {
        val inntekter = emptyList<Inntekt>().inntektSiste3Kalenderår(Year.of(2021))
        assertTrue(inntekter.isEmpty())
    }

    @Test
    fun `Finner ingen innteker for siste tre år når alle inntektene er fra et annet år`() {
        val inntekter = inntekterFor(januar(2018) til desember(2018))
        val inntekterSisteÅr = inntekter.inntektSiste3Kalenderår(Year.of(2021))
        assertTrue(inntekterSisteÅr.isEmpty())
    }

    @Test
    fun `Finner alle innteker for siste tre år når alle inntektene er fra samme år`() {
        val inntekter = inntekterFor(januar(2019) til desember(2021))
        val inntekterSisteÅr = inntekter.inntektSiste3Kalenderår(Year.of(2021))
        assertEquals(
            listOf(
                InntekterForBeregning.inntekterForBeregning(Year.of(2019), inntekter.subList(0, 12)),
                InntekterForBeregning.inntekterForBeregning(Year.of(2020), inntekter.subList(12, 24)),
                InntekterForBeregning.inntekterForBeregning(Year.of(2021), inntekter.subList(24, 36))
            ),
            inntekterSisteÅr
        )
    }

    @Test
    fun `Finner kun innteker for siste tre år`() {
        val inntekter = inntekterFor(januar(2018) til desember(2022))
        val inntekterSisteÅr = inntekter.inntektSiste3Kalenderår(Year.of(2021))
        assertEquals(
            listOf(
                InntekterForBeregning.inntekterForBeregning(Year.of(2019), inntekter.subList(12, 24)),
                InntekterForBeregning.inntekterForBeregning(Year.of(2020), inntekter.subList(24, 36)),
                InntekterForBeregning.inntekterForBeregning(Year.of(2021), inntekter.subList(36, 48))
            ),
            inntekterSisteÅr
        )
    }

    private fun inntekterFor(range: ClosedRange<YearMonth>) = range.inntekter()

    private infix fun YearMonth.til(other: YearMonth) = this..other

    private fun ClosedRange<YearMonth>.inntekter() =
        (0..this.start.until(this.endInclusive, ChronoUnit.MONTHS))
            .map { this.start.plusMonths(it) }
            .map { Inntekt(ARBEIDSGIVER, it, Beløp(1.0)) }
}
