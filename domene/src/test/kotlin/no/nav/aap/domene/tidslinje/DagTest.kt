package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DagTest {

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats`() {
        val dag = Dag(3 januar 2022, 400000.beløp, 0.beløp)
        assertEquals(1015.38.beløp, dag.getDagsats())
    }

    @Test
    fun `På helg er dagsats 0`() {
        val dag = Dag(2 januar 2022, 400000.beløp, 0.beløp)
        assertEquals(0.beløp, dag.getDagsats())
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats - dagsats økes med barnetillegg`() {
        val dag = Dag(3 januar 2022, 400000.beløp, 27.beløp * 13)
        assertEquals((1015.38 + 27 * 13).beløp, dag.getDagsats())
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val dag = Dag(3 januar 2022, 400000.beløp, 27.beløp * 14)
        assertEquals(1384.62.beløp, dag.getDagsats())
    }
}
