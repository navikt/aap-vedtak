package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.toDto
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr.Companion.toDto
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr.Companion.totalBeregningsfaktor
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import no.nav.aap.domene.entitet.Grunnlagsfaktor.Companion.summer
import no.nav.aap.dto.DtoInntektsgrunnlag
import no.nav.aap.dto.DtoInntektsgrunnlagForÅr
import java.time.LocalDate
import java.time.Year

internal class Inntektsgrunnlag(
    private val beregningsdato: LocalDate,
    private val inntekterSiste3Kalenderår: List<InntektsgrunnlagForÅr>,
    private val fødselsdato: Fødselsdato
) {
    private val sisteKalenderår = Year.from(beregningsdato).minusYears(1)

    //Dette tallet representerer hele utregningen av 11-19
    private val grunnlagsfaktor: Grunnlagsfaktor = inntekterSiste3Kalenderår.totalBeregningsfaktor(sisteKalenderår)

    internal fun grunnlagForDag(dato: LocalDate) =
        Grunnbeløp.justerInntekt(dato, fødselsdato.justerGrunnlagsfaktorForAlder(dato, grunnlagsfaktor))

    internal fun toDto() = DtoInntektsgrunnlag(
        beregningsdato = beregningsdato,
        inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.toDto(),
        fødselsdato = fødselsdato.toDto(),
        sisteKalenderår = sisteKalenderår,
        grunnlagsfaktor = grunnlagsfaktor.toDto()
    )

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
    private val grunnlagsfaktor: Grunnlagsfaktor = Grunnbeløp.finnBeregningsfaktor(år, beløpJustertFor6G)

    internal companion object {
        private const val ANTALL_ÅR_FOR_GJENNOMSNITT = 3

        internal fun Iterable<InntektsgrunnlagForÅr>.totalBeregningsfaktor(sisteKalenderår: Year): Grunnlagsfaktor {
            val sum1År = finnSisteKalenderår(sisteKalenderår).summerBeregningsfaktor()
            val gjennomsnitt3År = summerBeregningsfaktor() / ANTALL_ÅR_FOR_GJENNOMSNITT
            return maxOf(sum1År, gjennomsnitt3År)
        }

        private fun Iterable<InntektsgrunnlagForÅr>.summerBeregningsfaktor() = map { it.grunnlagsfaktor }.summer()

        private fun Iterable<InntektsgrunnlagForÅr>.finnSisteKalenderår(sisteKalenderår: Year): List<InntektsgrunnlagForÅr> =
            singleOrNull { it.år == sisteKalenderår }?.let { listOf(it) } ?: emptyList()

        internal fun Iterable<InntektsgrunnlagForÅr>.toDto() = map(InntektsgrunnlagForÅr::toDto)
    }

    private fun grunnlagForDag(dato: LocalDate, fødselsdato: Fødselsdato) =
        Grunnbeløp.justerInntekt(dato, fødselsdato.justerGrunnlagsfaktorForAlder(dato, grunnlagsfaktor))

    private fun toDto() = DtoInntektsgrunnlagForÅr(
        år = år,
        inntekter = inntekter.toDto(),
        beløpFørJustering = beløpFørJustering.toDto(),
        beløpJustertFor6G = beløpJustertFor6G.toDto(),
        erBeløpJustertFor6G = erBeløpJustertFor6G,
        beregningsfaktor = grunnlagsfaktor.toDto()
    )

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
