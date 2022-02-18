package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.beløpJustertFor6G
import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.justerInntekt
import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.finnBeregningsfaktor
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import java.time.LocalDate
import java.time.Year

internal object Grunnbeløp {
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
            fun Iterable<Element>.justerInntekt(beregningsdato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) =
                grunnlagsfaktor * finnGrunnbeløpForDato(beregningsdato).beløp

            private fun Iterable<Element>.finnGrunnbeløpForDato(dato: LocalDate) = this
                .sortedByDescending { it.dato }
                .first { dato >= it.dato }

            private fun Iterable<Element>.finnGrunnbeløpForÅr(år: Year) = this
                .sortedByDescending { it.dato }
                .first { år >= Year.from(it.dato) }

            fun Iterable<Element>.beløpJustertFor6G(år: Year, beløpFørJustering: Beløp): Beløp {
                val grunnbeløpForInntektsår = finnGrunnbeløpForÅr(år)
                return minOf(beløpFørJustering, grunnbeløpForInntektsår.gjennomsnittBeløp * 6)
            }

            fun Iterable<Element>.finnBeregningsfaktor(år: Year, beløp: Beløp): Grunnlagsfaktor {
                return Grunnlagsfaktor(beløp / finnGrunnbeløpForÅr(år).gjennomsnittBeløp)
            }
        }
    }

    internal fun beløpJustertFor6G(år: Year, beløpFørJustering: Beløp) =
        grunnbeløp.beløpJustertFor6G(år, beløpFørJustering)

    internal fun justerInntekt(beregningsdato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) =
        grunnbeløp.justerInntekt(beregningsdato, grunnlagsfaktor)

    internal fun finnBeregningsfaktor(år: Year, beløp: Beløp) =
        grunnbeløp.finnBeregningsfaktor(år, beløp)
}
