package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.tidslinje.Tidsperiode.Companion.DAGER_I_EN_PERIODE
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Year

internal class TidslinjeTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Når det mottas et meldekort legges dagene til i tidslinjen og utbetaling beregnes`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)
        val tidslinje = Tidslinje()

        val meldekort = Meldekort(
            (1..DAGER_I_EN_PERIODE).map { (1 januar 2017).plusDays(it) }.map { Meldekort.Meldekortdag(it) },
            Barnehage(emptyList()),
            emptyList()
        )

        tidslinje.håndterMeldekort(meldekort, grunnlag)

        assertEquals(14100.beløp, tidslinje.summerTidslinje())
    }

    @Test
    fun `Når det mottas et meldekort med barn legges barnetillegg til på dagene`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)
        val tidslinje = Tidslinje()

        val meldekort = Meldekort(
            (1..DAGER_I_EN_PERIODE).map { (1 januar 2017).plusDays(it) }.map { Meldekort.Meldekortdag(it) },
            Barnehage(listOf(Barnehage.Barn(Fødselsdato(1 januar 2012)))),
            emptyList()
        )

        tidslinje.håndterMeldekort(meldekort, grunnlag)

        assertEquals((14100 + 270).beløp, tidslinje.summerTidslinje())
    }

    @Test
    fun `Når det mottas to påfølgende meldekort opprettes to tidsperioder`() {
        val inntekter = listOf(
            Inntekt(ARBEIDSGIVER, januar(2014), 540527.beløp),
            Inntekt(ARBEIDSGIVER, januar(2015), 459248.beløp),
            Inntekt(ARBEIDSGIVER, januar(2016), 645246.beløp)
        )
        val grunnlag = inntekter.inntektsgrunnlag(1 januar 2017)
        val tidslinje = Tidslinje()

        val meldekort = Meldekort(
            (1..DAGER_I_EN_PERIODE).map { (1 januar 2017).plusDays(it) }.map { Meldekort.Meldekortdag(it) },
            Barnehage(emptyList()),
            emptyList()
        )

        val meldekort2 = Meldekort(
            (1..DAGER_I_EN_PERIODE).map { (15 januar 2017).plusDays(it) }.map { Meldekort.Meldekortdag(it) },
            Barnehage(emptyList()),
            emptyList()
        )

        tidslinje.håndterMeldekort(meldekort, grunnlag)
        tidslinje.håndterMeldekort(meldekort2, grunnlag)

        assertEquals(14100.beløp * 2, tidslinje.summerTidslinje())
    }

    private fun Iterable<Inntekt>.inntektsgrunnlag(
        beregningsdato: LocalDate,
        fødselsdato: Fødselsdato = Fødselsdato(1 januar 1970)
    ) =
        Inntektsgrunnlag.inntektsgrunnlag(
            beregningsdato,
            this.inntektSiste3Kalenderår(Year.from(beregningsdato).minusYears(1)),
            fødselsdato
        )
}
