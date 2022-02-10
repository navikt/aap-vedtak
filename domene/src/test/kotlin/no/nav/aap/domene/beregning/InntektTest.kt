package no.nav.aap.domene.beregning

import no.nav.aap.desember
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSisteKalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import no.nav.aap.februar
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Year
import java.time.YearMonth
import java.time.temporal.ChronoUnit

internal class InntektTest {
    @Test
    fun `summer tom liste`() {
        val sum = emptyList<Inntekt>().summerInntekt()
        assertEquals(Beløp(0.0), sum)
    }

    @Test
    fun `summer liste med ett beløp på 0`() {
        val inntekter = inntekterFor(februar(2021) til januar(2021))
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(0.0), sum)
    }

    @Test
    fun `summer liste med ett beløp på 1`() {
        val inntekter = inntekterFor(januar(2021) til januar(2021))
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(1.0), sum)
    }

    @Test
    fun `summer liste med to beløp på 1`() {
        val inntekter = inntekterFor(januar(2021) til februar(2021))
        val sum = inntekter.summerInntekt()
        assertEquals(Beløp(2.0), sum)
    }

    @Test
    fun `Finner ingen innteker for siste år når det ikke er noen inntekter`() {
        val inntekter = emptyList<Inntekt>().inntektSisteKalenderår(Year.of(2021))
        assertTrue(inntekter.isEmpty())
    }

    @Test
    fun `Finner ingen innteker for siste år når alle inntektene er fra et annet år`() {
        val inntekter = inntekterFor(januar(2020) til desember(2020))
        val inntekterSisteÅr = inntekter.inntektSisteKalenderår(Year.of(2021))
        assertTrue(inntekterSisteÅr.isEmpty())
    }

    @Test
    fun `Finner alle innteker for siste år når alle inntektene er fra samme år`() {
        val inntekter = inntekterFor(januar(2021) til desember(2021))
        val inntekterSisteÅr = inntekter.inntektSisteKalenderår(Year.of(2021))
        assertEquals(inntekter, inntekterSisteÅr)
    }

    @Test
    fun `Finner kun innteker for siste år`() {
        val inntekter = inntekterFor(januar(2020) til desember(2022))
        val inntekterSisteÅr = inntekter.inntektSisteKalenderår(Year.of(2021))
        val expected = inntekter.subList(12, 24)
        assertEquals(expected, inntekterSisteÅr)
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
        assertEquals(inntekter, inntekterSisteÅr.values.flatten())
    }

    @Test
    fun `Finner kun innteker for siste tre år`() {
        val inntekter = inntekterFor(januar(2018) til desember(2022))
        val inntekterSisteÅr = inntekter.inntektSiste3Kalenderår(Year.of(2021))
        val expected = inntekter.subList(12, 48)
        assertEquals(expected, inntekterSisteÅr.values.flatten())
    }


    private fun inntekterFor(range: ClosedRange<YearMonth>) = range.inntekter()

    private infix fun YearMonth.til(other: YearMonth) = this..other

    private fun ClosedRange<YearMonth>.inntekter() =
        (0..this.start.until(this.endInclusive, ChronoUnit.MONTHS))
            .map { this.start.plusMonths(it) }
            .map { Inntekt(Arbeidsgiver(), it, Beløp(1.0)) }
}
