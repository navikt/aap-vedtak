package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.justerInntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSisteKalenderår
import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import java.time.LocalDate
import java.time.Year

internal class Inntektshistorikk {
    private val inntekter = mutableListOf<Inntekt>()

    private companion object {
        private const val ANTALL_ÅR_FOR_GJENNOMSNITT = 3
    }

    internal fun leggTilInntekter(inntekter: List<Inntekt>) {
        this.inntekter.addAll(inntekter)
    }

    internal fun beregnGrunnlag(beregningsdato: LocalDate): Grunnlagsberegning {
        val sisteKalenderår = Year.from(beregningsdato).minusYears(1)
        val inntekterSisteKalenderår = inntektSisteKalenderår(sisteKalenderår)
        val justerteInntekterSisteKalenderår =
            justertSum(beregningsdato, mapOf(sisteKalenderår to inntekterSisteKalenderår))
        val sumInntekterSisteKalenderår = justerteInntekterSisteKalenderår.values.summerBeløp()
        val inntekterSiste3Kalenderår = inntektSiste3Kalenderår(sisteKalenderår)
        val justerteInntekterSiste3Kalenderår = justertSum(beregningsdato, inntekterSiste3Kalenderår)
        val sumInntekterSiste3Kalenderår = justerteInntekterSiste3Kalenderår.values.summerBeløp()

        val grunnlag = maxOf(sumInntekterSisteKalenderår, sumInntekterSiste3Kalenderår / ANTALL_ÅR_FOR_GJENNOMSNITT)

        return Grunnlagsberegning(
            grunnlag = grunnlag,
            inntekterSisteKalenderår = inntekterSisteKalenderår,
            inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.values.flatten()
        )
    }

    private fun inntektSisteKalenderår(år: Year): List<Inntekt> = inntekter.inntektSisteKalenderår(år)

    private fun inntektSiste3Kalenderår(år: Year): Map<Year, List<Inntekt>> = inntekter.inntektSiste3Kalenderår(år)

    private fun justertSum(beregningsdato: LocalDate, inntekter: Map<Year, List<Inntekt>>): Map<Year, Beløp> {
        return inntekter.mapValues {
            val beregner = Beregner(beregningsdato, it.key, Grunnbeløp())
            val justertBeløp = beregner.justertInntekt(it.value)
            justertBeløp
        }
    }

}

internal class Beregner(
    private val beregningsdato: LocalDate,
    private val inntektsår: Year,
    private val grunnbeløp: Grunnbeløp
) {
    internal fun justertInntekt(inntekter: List<Inntekt>): Beløp {
        val beløpFørJustering = inntekter.summerInntekt()
        return grunnbeløp.justerInntekt(beregningsdato, inntektsår, beløpFørJustering)
    }
}

internal class Grunnbeløp {
    private val grunnbeløp = listOf(
        Element(LocalDate.of(2021, 5, 1), Beløp(106399.0), Beløp(104716.0)),
        Element(LocalDate.of(2020, 5, 1), Beløp(101351.0), Beløp(100853.0)),
        Element(LocalDate.of(2019, 5, 1), Beløp(99858.0), Beløp(98866.0)),
        Element(LocalDate.of(2018, 5, 1), Beløp(96883.0), Beløp(95800.0)),
        Element(LocalDate.of(2017, 5, 1), Beløp(93634.0), Beløp(93281.0)),
        Element(LocalDate.of(2016, 5, 1), Beløp(92576.0), Beløp(91740.0)),
        Element(LocalDate.of(2015, 5, 1), Beløp(90068.0), Beløp(89502.0)),
        Element(LocalDate.of(2014, 5, 1), Beløp(88370.0), Beløp(87328.0))
    )

    private class Element(
        private val dato: LocalDate,
        private val beløp: Beløp,
        private val gjennomsnittBeløp: Beløp
    ) {
        companion object {
            fun Iterable<Element>.justerInntekt(
                beregningsdato: LocalDate,
                inntektsår: Year,
                beløpFørJustering: Beløp
            ): Beløp {
                val grunnbeløpForBeregningsdato = finnGrunnbeløpForDato(beregningsdato)
                val grunnbeløpForInntektsår = finnGrunnbeløpForÅr(inntektsår)
                val beløpEtter6GBegrensning = minOf(beløpFørJustering, grunnbeløpForInntektsår.gjennomsnittBeløp * 6)
                return beløpEtter6GBegrensning * grunnbeløpForBeregningsdato.beløp / grunnbeløpForInntektsår.gjennomsnittBeløp
            }

            private fun Iterable<Element>.finnGrunnbeløpForDato(beregningsdato: LocalDate) = this
                .sortedByDescending { it.dato }
                .first { beregningsdato >= it.dato }

            private fun Iterable<Element>.finnGrunnbeløpForÅr(inntektsår: Year) = this
                .sortedByDescending { it.dato }
                .first { inntektsår >= Year.from(it.dato) }
        }
    }

    internal fun justerInntekt(beregningsdato: LocalDate, inntektsår: Year, beløpFørJustering: Beløp) =
        grunnbeløp.justerInntekt(beregningsdato, inntektsår, beløpFørJustering)
}
