package no.nav.aap.domene.visitor

import no.nav.aap.hendelse.LøsningParagraf_11_19
import no.nav.aap.oktober
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class BeregningsdatoVisitorTest {

    @Test
    fun `Henter beregningsdato fra løsning`() {
        val visitor = BeregningsdatoVisitor()
        LøsningParagraf_11_19(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = (1 oktober 2022).atTime(12, 0),
            beregningsdato = 15 september 2022
        ).accept(visitor)

        assertEquals(15 september 2022, visitor.beregningsdato)
    }

    @Test
    fun `Feiler ved henting av beregningsdato hvis løsning ikke finnes`() {
        val visitor = BeregningsdatoVisitor()

        assertThrows<UninitializedPropertyAccessException> { visitor.beregningsdato }
    }
}
