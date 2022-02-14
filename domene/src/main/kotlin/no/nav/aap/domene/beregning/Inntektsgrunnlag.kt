package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr.Companion.beregnGrunnlag
import java.time.LocalDate
import java.time.Year

internal class Inntektsgrunnlag(
    private val sisteKalenderår: Year,
    private val inntekterSiste3Kalenderår: List<InntektsgrunnlagForÅr>
) {
    internal fun grunnlagForDag(dato: LocalDate): Beløp {
        return inntekterSiste3Kalenderår.beregnGrunnlag(sisteKalenderår, dato)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Inntektsgrunnlag

        if (inntekterSiste3Kalenderår != other.inntekterSiste3Kalenderår) return false

        return true
    }

    override fun hashCode() = inntekterSiste3Kalenderår.hashCode()

    override fun toString() =
        "Inntektsgrunnlag(inntekterSiste3Kalenderår=$inntekterSiste3Kalenderår)"
}

internal class InntektsgrunnlagForÅr(
    private val år: Year,
    private val inntekter: List<Inntekt>
) {
    private val beløpFørJustering: Beløp = inntekter.summerInntekt()
    private val beløpJustertFor6G: Beløp = Grunnbeløp.beløpJustertFor6G(år, beløpFørJustering)
    private val erBeløpJustertFor6G: Boolean = beløpFørJustering != beløpJustertFor6G
    private val beregningsfaktor: Double = Grunnbeløp.finnBeregningsfaktor(år, beløpJustertFor6G)

    internal companion object {
        private const val ANTALL_ÅR_FOR_GJENNOMSNITT = 3

        internal fun Iterable<InntektsgrunnlagForÅr>.beregnGrunnlag(sisteKalenderår: Year, dato: LocalDate): Beløp {
            val sum1År = finnSisteKalenderår(sisteKalenderår).summerGrunnlagForDag(dato)
            val gjennomsnitt3År = summerGrunnlagForDag(dato) / ANTALL_ÅR_FOR_GJENNOMSNITT
            return maxOf(sum1År, gjennomsnitt3År)
        }

        private fun Iterable<InntektsgrunnlagForÅr>.summerGrunnlagForDag(dato: LocalDate) =
            map { it.grunnlagForDag(dato) }.fold(0.beløp, Beløp::plus)

        private fun Iterable<InntektsgrunnlagForÅr>.finnSisteKalenderår(sisteKalenderår: Year): List<InntektsgrunnlagForÅr> =
            singleOrNull { it.år == sisteKalenderår }?.let { listOf(it) } ?: emptyList()
    }

    private fun grunnlagForDag(dato: LocalDate) = Grunnbeløp.justerInntekt(dato, beregningsfaktor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InntektsgrunnlagForÅr

        if (år != other.år) return false
        if (inntekter != other.inntekter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = år.hashCode()
        result = 31 * result + inntekter.hashCode()
        return result
    }
}
