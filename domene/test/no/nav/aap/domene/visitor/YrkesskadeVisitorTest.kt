package no.nav.aap.domene.visitor

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.hendelse.LøsningParagraf_11_22
import no.nav.aap.modellapi.InntektsgrunnlagForÅrModellApi
import no.nav.aap.modellapi.YrkesskadeModellApi
import no.nav.aap.oktober
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Year
import java.util.*

internal class YrkesskadeVisitorTest {

    @Test
    fun `Henter yrkesskade fra løsning`() {
        val visitor = YrkesskadeVisitor()
        LøsningParagraf_11_22(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = (1 oktober 2022).atTime(12, 0),
            erOppfylt = true,
            andelNedsattArbeidsevne = 50,
            år = Year.of(2022),
            antattÅrligArbeidsinntekt = 109784.beløp,
        ).accept(visitor)

        val expected = YrkesskadeModellApi(
            gradAvNedsattArbeidsevneKnyttetTilYrkesskade = 50.0,
            inntektsgrunnlag = InntektsgrunnlagForÅrModellApi(
                år = Year.of(2022),
                beløpFørJustering = 109784.0,
                beløpJustertFor6G = 109784.0,
                erBeløpJustertFor6G = false,
                grunnlagsfaktor = 1.0,
            ),
        )

        assertEquals(expected, visitor.yrkesskade.toDto())
    }

    @Test
    fun `Feiler ved henting av yrkesskade hvis løsning ikke finnes`() {
        val visitor = YrkesskadeVisitor()

        assertThrows<UninitializedPropertyAccessException> { visitor.yrkesskade }
    }
}
