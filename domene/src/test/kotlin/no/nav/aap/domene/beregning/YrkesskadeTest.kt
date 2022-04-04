package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Year

internal class YrkesskadeTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis begge grunnlag er like, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(3)
        val grunnlagsfaktorForYrkesskade = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2015), 89502.beløp * 3)

        val yrkesskade = Yrkesskade(100.0, grunnlagsfaktorForYrkesskade)

        assertEquals(Grunnlagsfaktor(3), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne))
    }

    @Test
    fun `Hvis begge grunnlag er like og grad under 70 prosent, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(3)
        val grunnlagsfaktorForYrkesskade = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2015), 89502.beløp * 3)

        val yrkesskade = Yrkesskade(50.0, grunnlagsfaktorForYrkesskade)

        assertEquals(Grunnlagsfaktor(3), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne))
    }

    @Test
    fun `Hvis begge grunnlag er forskjellig og grad er 50 prosent, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(2)
        val grunnlagsfaktorForYrkesskade = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2015), 89502.beløp * 4)

        val yrkesskade = Yrkesskade(50.0, grunnlagsfaktorForYrkesskade)

        assertEquals(Grunnlagsfaktor(3), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne))
    }

    @Test
    fun `Hvis begge grunnlag er forskjellig og grad er 70 prosent, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(2)
        val grunnlagsfaktorForYrkesskade = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(Year.of(2015), 89502.beløp * 4)

        val yrkesskade = Yrkesskade(70.0, grunnlagsfaktorForYrkesskade)

        assertEquals(
            Grunnlagsfaktor(3.4),
            yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne)
        )
    }

}