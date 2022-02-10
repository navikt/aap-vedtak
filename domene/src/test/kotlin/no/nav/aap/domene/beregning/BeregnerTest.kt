package no.nav.aap.domene.beregning

import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Year

internal class BeregnerTest{

    @Test
    fun `ingen inntekt`(){
        val inntekter = listOf<Inntekt>()
        val beregner = Beregner(1.januar, Year.of(2020), Grunnbeløp())
        val justertInntekt = beregner.justertInntekt(inntekter)

        assertEquals(Beløp(0.0), justertInntekt)
    }

    @Test
    fun `justert inntekt for 2020`(){
        val inntekter = listOf(Inntekt(Arbeidsgiver(), januar(2020), Beløp(1000.0)))
        val beregner = Beregner(1.januar(2022), Year.of(2020), Grunnbeløp())
        val justertInntekt = beregner.justertInntekt(inntekter)

        assertEquals(Beløp(1054.9909273893686), justertInntekt)
    }
}
