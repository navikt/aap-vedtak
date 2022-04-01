package no.nav.aap.domene.beregning

import no.nav.aap.domene.entitet.Grunnlagsfaktor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class YrkesskadeTest {

    @Test
    fun `Hvis begge grunnlag er like, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(3)
        val grunnlagsfaktorForYrkesskade = Grunnlagsfaktor(3)

        val yrkesskade = Yrkesskade(100.0)

        assertEquals(Grunnlagsfaktor(3), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne, grunnlagsfaktorForYrkesskade))
    }

    @Test
    fun `Hvis begge grunnlag er like og grad under 70 prosent, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(3)
        val grunnlagsfaktorForYrkesskade = Grunnlagsfaktor(3)

        val yrkesskade = Yrkesskade(50.0)

        assertEquals(Grunnlagsfaktor(3), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne, grunnlagsfaktorForYrkesskade))
    }

    @Test
    fun `Hvis begge grunnlag er forskjellig og grad er 50 prosent, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(2)
        val grunnlagsfaktorForYrkesskade = Grunnlagsfaktor(4)

        val yrkesskade = Yrkesskade(50.0)

        assertEquals(Grunnlagsfaktor(3), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne, grunnlagsfaktorForYrkesskade))
    }

    @Test
    fun `Hvis begge grunnlag er forskjellig og grad er 70 prosent, er resultatet det samme`() {
        val grunnlagsfaktorForNedsattArbeidsevne = Grunnlagsfaktor(2)
        val grunnlagsfaktorForYrkesskade = Grunnlagsfaktor(4)

        val yrkesskade = Yrkesskade(70.0)

        assertEquals(Grunnlagsfaktor(3.4), yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktorForNedsattArbeidsevne, grunnlagsfaktorForYrkesskade))
    }

}