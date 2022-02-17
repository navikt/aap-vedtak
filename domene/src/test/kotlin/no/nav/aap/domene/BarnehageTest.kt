package no.nav.aap.domene

import no.nav.aap.desember
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BarnehageTest {

    @Test
    fun `Hvis det ikke finnes noen barn, er barnetillegget 0`() {
        val barnetillegg = Barnehage(emptyList()).barnetilleggForDag(1 januar 2022)
        assertEquals(0.beløp, barnetillegg)
    }

    @Test
    fun `Hvis det finnes et barn under 18 år, er barnetillegget 27`() {
        val barnetillegg =
            Barnehage(listOf(Barnehage.Barn(Fødselsdato(2 januar 2004)))).barnetilleggForDag(1 januar 2022)
        assertEquals(27.beløp, barnetillegg)
    }

    @Test
    fun `Hvis det finnes et barn på 18 år, er barnetillegget 0`() {
        val barnetillegg =
            Barnehage(listOf(Barnehage.Barn(Fødselsdato(1 januar 2004)))).barnetilleggForDag(1 januar 2022)
        assertEquals(0.beløp, barnetillegg)
    }

    @Test
    fun `Hvis det finnes et barn over 18 år, er barnetillegget 0`() {
        val barnetillegg =
            Barnehage(listOf(Barnehage.Barn(Fødselsdato(31 desember 2003)))).barnetilleggForDag(1 januar 2022)
        assertEquals(0.beløp, barnetillegg)
    }

    @Test
    fun `Hvis det finnes to barn under 18 år, er barnetillegget 54`() {
        val barnetillegg =
            Barnehage(listOf(
                Barnehage.Barn(Fødselsdato(2 januar 2004)),
                Barnehage.Barn(Fødselsdato(2 januar 2004))
            )).barnetilleggForDag(1 januar 2022)
        assertEquals(54.beløp, barnetillegg)
    }
}
