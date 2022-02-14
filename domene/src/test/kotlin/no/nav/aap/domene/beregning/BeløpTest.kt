package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BeløpTest {
    @Test
    fun `Hvis vi summerer en tom liste med Beløp, får vi 0`() {
        val sum = emptyList<Beløp>().summerBeløp()
        assertEquals(Beløp(0.0), sum)
    }

    @Test
    fun `Hvis vi summerer en liste med ett 0-beløp, blir summen 0`() {
        val sum = listOf(Beløp(0.0)).summerBeløp()
        assertEquals(Beløp(0.0), sum)
    }

    @Test
    fun `Hvis vi summerer en liste med ett beløp på 1, blir summen 1`() {
        val sum = listOf(Beløp(1.0)).summerBeløp()
        assertEquals(Beløp(1.0), sum)
    }

    @Test
    fun `Hvis vi summerer en liste med to beløp på 1, får vi 2`() {
        val sum = listOf(Beløp(1.0), Beløp(1.0)).summerBeløp()
        assertEquals(Beløp(2.0), sum)
    }
}
