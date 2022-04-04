package no.nav.aap.domene.beregning

import no.nav.aap.august
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.mars
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Year

internal class InntektsgrunnlagTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis vi beregner grunnlag for en bruker uten inntekt, blir grunnlaget 0`() {
        val grunnlag = Inntektsgrunnlag.inntektsgrunnlag(1 januar 2022, emptyList(), Fødselsdato(1 januar 1970))
        assertEquals(322421.21.beløp, grunnlag.grunnlagForDag(1.januar))
    }

    @Test
    fun `Hvis bruker kun har inntekt i år, blir grunnlaget satt til minstegrunnlag`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2022), 1000.beløp))
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        assertEquals(322421.21.beløp, grunnlag.grunnlagForDag(1 januar 2022))
    }

    @Test
    fun `Hvis bruker kun har inntekt i forrige kalenderår`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2021), Beløp(400000.0)))
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(406428.86.beløp, grunnlagForDag)
    }

    @Test
    fun `Hvis bruker kun har inntekt over 6G forrige kalenderår, blir beløp G-regulert`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2021), Beløp(1000000.0)))
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(638394.beløp, grunnlagForDag)
    }

    @Test
    fun `Hvis bruker kun har inntekt i 2020 blir snittet lavere enn minste årlige ytelse på 2G, og grunnlaget oppjusteres`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2020), 400000.beløp))
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(322421.21.beløp, grunnlagForDag)
    }

    @Test
    fun `Bruker har inntekt rett under grenseverdien på 2G delt på 66 prosent, så vil grunnlaget oppjusteres til minste årlige ytelse på 2G`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2021), 314148.beløp))
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(322421.21.beløp, grunnlagForDag)
    }

    @Test
    fun `Bruker har inntekt rett over grenseverdien på 2G delt på 66 prosent, så grunnlaget vil ikke oppjusteres til minste årlige ytelse på 2G - Inntekten er allerede over`() {
        val inntekter = listOf(Inntekt(ARBEIDSGIVER, januar(2021), 318336.64.beløp))
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(323452.96.beløp, grunnlagForDag)
    }

    @Test
    fun `Har inntekt i 3 kalenderår`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2019), 400000.beløp), //4.0458802824
            Inntekt(ARBEIDSGIVER, januar(2020), 400000.beløp), //3.966168582
            Inntekt(ARBEIDSGIVER, januar(2021), 400000.beløp)  //3.8198556095
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        val totalGrunnlagsfaktor =
            Grunnlagsfaktor(4.0458802824) + Grunnlagsfaktor(3.966168582) + Grunnlagsfaktor(3.8198556095)
        assertEquals(totalGrunnlagsfaktor / 3 * 106399.beløp, grunnlagForDag)
    }

    @Test
    fun `Har inntekt i 3 kalenderår - høyere i 2019`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2019), 400000.beløp), //4.0458802824
            Inntekt(ARBEIDSGIVER, januar(2020), 400000.beløp), //3.966168582
            Inntekt(ARBEIDSGIVER, januar(2021), 200000.beløp)  //1.9099278047
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        val totalGrunnlagsfaktor =
            Grunnlagsfaktor(4.0458802824) + Grunnlagsfaktor(3.966168582) + Grunnlagsfaktor(1.9099278047)
        assertEquals(totalGrunnlagsfaktor / 3 * 106399.beløp, grunnlagForDag)
    }

    @Test
    fun `Har inntekt i 3 kalenderår - alle over 6G`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2019), Beløp(700000.0)),
            Inntekt(ARBEIDSGIVER, januar(2020), Beløp(800000.0)),
            Inntekt(ARBEIDSGIVER, januar(2021), Beløp(900000.0))
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2022)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 januar 2022)

        assertEquals(1915182.beløp / 3, grunnlagForDag)
    }

//    2016	645.246 kroner	550.440 kroner	550.440 kroner	561.804 kroner
//    2015	459.248 kroner	537.012 kroner	459.248 kroner	480.450 kroner
//    2014	540.527 kroner	523.968 kroner	523.968 kroner	561.804 kroner

    @Test
    fun `Har inntekt i 3 kalenderår - fra rundskriv`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 juli 2017)

//        (561804.0 + 480449.90315300215 + 561804.0) / 3
//        Summen over er lavere enn oppjustert 2016 inntekt, så beløpet for 2016 brukes

        assertEquals(561804.beløp, grunnlagForDag)
    }

    @Test
    fun `Hvis bruker har snittinntekt over 3 år som er høyere enn siste år`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), Beløp(540527.0)),
            Inntekt(ARBEIDSGIVER, januar(2015), Beløp(459248.0)),
            Inntekt(ARBEIDSGIVER, januar(2016), Beløp(445700.0))
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)
        val grunnlagForDag = grunnlag.grunnlagForDag(1 juli 2017)

        assertEquals(499051.8.beløp, grunnlagForDag)
    }

    @Test
    fun `Eksempel 1`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2018), 714273.beløp),
            Inntekt(ARBEIDSGIVER, januar(2019), 633576.beløp),
            Inntekt(ARBEIDSGIVER, januar(2020), 915454.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2021)
        val grunnlagForDag = grunnlag.grunnlagForDag(19 mars 2022)

        assertEquals(638394.beløp, grunnlagForDag)
    }

    @Test
    fun `Eksempel 2`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2017), 190402.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 268532.beløp),
            Inntekt(ARBEIDSGIVER, januar(2019), 350584.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2020)
        val grunnlagForDag = grunnlag.grunnlagForDag(31 august 2021)

        assertEquals(377296.39.beløp, grunnlagForDag)
    }

    @Test
    fun `Inntektsgrunnlaget økes ved yrkesskade`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2017), 93281.beløp * 3),
            Inntekt(ARBEIDSGIVER, januar(2018), 95800.beløp * 3),
            Inntekt(ARBEIDSGIVER, januar(2019), 98866.beløp * 3)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, januar(2015), 89502.beløp * 4))
            .inntektSiste3Kalenderår(Year.of(2015)).first()
        val yrkesskade = Yrkesskade(100.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2020, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(31 august 2021)

        assertEquals(106399.beløp * 4, grunnlagForDag)
    }

    @Test
    fun `Yrkessakde - fra eksempel med 30 prosent yrkesskade`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2016), 430661.beløp),
            Inntekt(ARBEIDSGIVER, januar(2017), 418734.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 423658.beløp)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, august(2014), 397000.beløp))
            .inntektSiste3Kalenderår(Year.of(2014)).first()
        val yrkesskade = Yrkesskade(30.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 august 2019, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(30 juli 2020)

        assertEquals(459978.68.beløp, grunnlagForDag)
    }

    @Test
    fun `Yrkessakde - fra eksempel med 70 prosent yrkesskade`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2016), 430661.beløp),
            Inntekt(ARBEIDSGIVER, januar(2017), 418734.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 423658.beløp)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, august(2014), 397000.beløp))
            .inntektSiste3Kalenderår(Year.of(2014)).first()
        val yrkesskade = Yrkesskade(70.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 august 2019, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(30 juli 2020)

        assertEquals(460419.25.beløp, grunnlagForDag)
    }

    @Test
    fun `Yrkessakde - fra eksempel med 80 prosent yrkesskade`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2016), 430661.beløp),
            Inntekt(ARBEIDSGIVER, januar(2017), 418734.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 423658.beløp)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, august(2014), 397000.beløp))
            .inntektSiste3Kalenderår(Year.of(2014)).first()
        val yrkesskade = Yrkesskade(80.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 august 2019, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(30 juli 2020)

        assertEquals(460749.65.beløp, grunnlagForDag)
    }

    @Test
    fun `Yrkessakde - fra eksempel med 30 prosent yrkesskade med høy inntekt på skadetidspunkt`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2016), 430661.beløp),
            Inntekt(ARBEIDSGIVER, januar(2017), 418734.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 423658.beløp)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, august(2014), 425000.beløp))
            .inntektSiste3Kalenderår(Year.of(2014)).first()
        val yrkesskade = Yrkesskade(30.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 august 2019, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(30 juli 2020)

        assertEquals(469727.53.beløp, grunnlagForDag)
    }

    @Test
    fun `Yrkessakde - fra eksempel med 70 prosent yrkesskade med høy inntekt på skadetidspunkt`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2016), 430661.beløp),
            Inntekt(ARBEIDSGIVER, januar(2017), 418734.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 423658.beløp)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, august(2014), 425000.beløp))
            .inntektSiste3Kalenderår(Year.of(2014)).first()
        val yrkesskade = Yrkesskade(70.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 august 2019, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(30 juli 2020)

        assertEquals(483166.57.beløp, grunnlagForDag)
    }

    @Test
    fun `Yrkessakde - fra eksempel med 80 prosent yrkesskade med høy inntekt på skadetidspunkt`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2016), 430661.beløp),
            Inntekt(ARBEIDSGIVER, januar(2017), 418734.beløp),
            Inntekt(ARBEIDSGIVER, januar(2018), 423658.beløp)
        )
        val grunnlagsfaktorForYrkesskade = listOf(Inntekt(ARBEIDSGIVER, august(2014), 425000.beløp))
            .inntektSiste3Kalenderår(Year.of(2014)).first()
        val yrkesskade = Yrkesskade(80.0, grunnlagsfaktorForYrkesskade)

        val grunnlag = inntekter.inntektsgrunnlag(1 august 2019, yrkesskade = yrkesskade)
        val grunnlagForDag = grunnlag.grunnlagForDag(30 juli 2020)

        assertEquals(493245.82.beløp, grunnlagForDag)
    }

    private fun Iterable<Inntekt>.inntektsgrunnlag(
        beregningsdato: LocalDate,
        fødselsdato: Fødselsdato = Fødselsdato(1 januar 1970),
        yrkesskade: Yrkesskade? = null
    ) =
        Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato,
            this.inntektSiste3Kalenderår(Year.from(beregningsdato).minusYears(1)),
            fødselsdato,
            yrkesskade
        )
}
