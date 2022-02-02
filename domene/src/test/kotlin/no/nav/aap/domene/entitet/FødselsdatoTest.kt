package no.nav.aap.domene.entitet

import no.nav.aap.desember
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FødselsdatoTest {
    @Test
    fun `Er mellom 18 og 67 år på 18-årsdagen`() {
        val `18-årsdagen` = 1.januar(2022)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertTrue(fødselsdato.erMellom18Og67År(`18-årsdagen`))
    }

    @Test
    fun `Er ikke mellom 18 og 67 år dagen før 18-årsdagen`() {
        val `dagen før 18-årsdagen` = 31.desember(2021)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertFalse(fødselsdato.erMellom18Og67År(`dagen før 18-årsdagen`))
    }

    @Test
    fun `Er mellom 18 og 67 år på 67-årsdagen`() {
        val `67-årsdagen` = 1.januar(2071)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertTrue(fødselsdato.erMellom18Og67År(`67-årsdagen`))
    }

    @Test
    fun `Er ikke mellom 18 og 67 år dagen etter 67-årsdagen`() {
        val `dagen etter 67-årsdagen` = 2.januar(2071)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertFalse(fødselsdato.erMellom18Og67År(`dagen etter 67-årsdagen`))
    }
}
