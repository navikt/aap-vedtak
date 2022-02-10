package no.nav.aap.domene

import no.nav.aap.domene.Beløp.Companion.summerBeløp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BeløpTest {
    @Test
    fun `summer tom liste`() {
        val sum = emptyList<Beløp>().summerBeløp()
        assertEquals(0.0, sum)
    }

    @Test
    fun `summer liste med ett beløp på 0`() {
        val sum = listOf(Beløp(0.0)).summerBeløp()
        assertEquals(0.0, sum)
    }

    @Test
    fun `summer liste med ett beløp på 1`() {
        val sum = listOf(Beløp(1.0)).summerBeløp()
        assertEquals(1.0, sum)
    }

    @Test
    fun `summer liste med to beløp på 1`() {
        val sum = listOf(Beløp(1.0), Beløp(1.0)).summerBeløp()
        assertEquals(2.0, sum)
    }
}
