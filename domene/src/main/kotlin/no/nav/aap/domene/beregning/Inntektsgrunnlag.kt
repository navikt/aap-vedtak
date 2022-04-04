package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
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

internal class Inntektsgrunnlag private constructor(
    private val beregningsdato: LocalDate,
    private val inntekterSiste3Kalenderår: List<InntektsgrunnlagForÅr>,
    private val årligArbeidsinntektVedYrkesskade: Yrkesskade? = null,
    private val fødselsdato: Fødselsdato,
    private val sisteKalenderår: Year,
    //Dette tallet representerer hele utregningen av 11-19
    private val grunnlagsfaktor: Grunnlagsfaktor
) {

    internal fun grunnlagForDag(dato: LocalDate) =
        Grunnbeløp.justerInntekt(dato, fødselsdato.justerGrunnlagsfaktorForAlder(dato, grunnlagsfaktor))

    internal fun toDto() = DtoInntektsgrunnlag(
        beregningsdato = beregningsdato,
        inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.toDto(),
        årligArbeidsinntektVedYrkesskade = årligArbeidsinntektVedYrkesskade?.toDto(),
        fødselsdato = fødselsdato.toDto(),
        sisteKalenderår = sisteKalenderår,
        grunnlagsfaktor = grunnlagsfaktor.toDto()
    )

    internal companion object {
        internal fun inntektsgrunnlag(
            beregningsdato: LocalDate,
            inntekterSiste3Kalenderår: List<InntektsgrunnlagForÅr>,
            fødselsdato: Fødselsdato,
            årligArbeidsinntektVedYrkesskade: Yrkesskade? = null,
        ): Inntektsgrunnlag {
            val sisteKalenderår = Year.from(beregningsdato).minusYears(1)
            return Inntektsgrunnlag(
                beregningsdato = beregningsdato,
                inntekterSiste3Kalenderår = inntekterSiste3Kalenderår,
                årligArbeidsinntektVedYrkesskade = årligArbeidsinntektVedYrkesskade,
                fødselsdato = fødselsdato,
                sisteKalenderår = sisteKalenderår,
                grunnlagsfaktor = inntekterSiste3Kalenderår.totalBeregningsfaktor(sisteKalenderår)
            )
        }

        internal fun gjenopprett(dtoInntektsgrunnlag: DtoInntektsgrunnlag) = Inntektsgrunnlag(
            beregningsdato = dtoInntektsgrunnlag.beregningsdato,
            inntekterSiste3Kalenderår = InntektsgrunnlagForÅr.gjenopprett(dtoInntektsgrunnlag.inntekterSiste3Kalenderår),
            årligArbeidsinntektVedYrkesskade = dtoInntektsgrunnlag.årligArbeidsinntektVedYrkesskade?.let(
                Yrkesskade.Companion::gjenopprett),
            fødselsdato = Fødselsdato(dtoInntektsgrunnlag.fødselsdato),
            sisteKalenderår = dtoInntektsgrunnlag.sisteKalenderår,
            grunnlagsfaktor = Grunnlagsfaktor(dtoInntektsgrunnlag.grunnlagsfaktor)
        )
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

internal class InntektsgrunnlagForÅr private constructor(
    private val år: Year,
    private val inntekter: List<Inntekt>,
    private val beløpFørJustering: Beløp = inntekter.summerInntekt(),
    private val beløpJustertFor6G: Beløp = Grunnbeløp.beløpJustertFor6G(år, beløpFørJustering),
    private val erBeløpJustertFor6G: Boolean = beløpFørJustering != beløpJustertFor6G,
    private val grunnlagsfaktor: Grunnlagsfaktor = Grunnbeløp.finnBeregningsfaktor(år, beløpJustertFor6G)
) {

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

        internal fun gjenopprett(inntekterSiste3Kalenderår: Iterable<DtoInntektsgrunnlagForÅr>) =
            inntekterSiste3Kalenderår.map {
                InntektsgrunnlagForÅr(
                    år = it.år,
                    inntekter = Inntekt.gjenopprett(it.inntekter),
                    beløpFørJustering = it.beløpFørJustering.beløp,
                    beløpJustertFor6G = it.beløpJustertFor6G.beløp,
                    erBeløpJustertFor6G = it.erBeløpJustertFor6G,
                    grunnlagsfaktor = Grunnlagsfaktor(it.grunnlagsfaktor)
                )
            }

        internal fun gjenopprett(inntektsgrunnlagForYrkesskade: DtoInntektsgrunnlagForÅr) =
                InntektsgrunnlagForÅr(
                    år = inntektsgrunnlagForYrkesskade.år,
                    inntekter = Inntekt.gjenopprett(inntektsgrunnlagForYrkesskade.inntekter),
                    beløpFørJustering = inntektsgrunnlagForYrkesskade.beløpFørJustering.beløp,
                    beløpJustertFor6G = inntektsgrunnlagForYrkesskade.beløpJustertFor6G.beløp,
                    erBeløpJustertFor6G = inntektsgrunnlagForYrkesskade.erBeløpJustertFor6G,
                    grunnlagsfaktor = Grunnlagsfaktor(inntektsgrunnlagForYrkesskade.grunnlagsfaktor)
                )


        internal fun inntektsgrunnlagForÅr(
            år: Year,
            inntekter: List<Inntekt>
        ): InntektsgrunnlagForÅr {
            val beløpFørJustering = inntekter.summerInntekt()
            val beløpJustertFor6G = Grunnbeløp.beløpJustertFor6G(år, beløpFørJustering)
            return InntektsgrunnlagForÅr(
                år = år,
                inntekter = inntekter,
                beløpFørJustering = beløpFørJustering,
                beløpJustertFor6G = beløpJustertFor6G,
                erBeløpJustertFor6G = beløpFørJustering != beløpJustertFor6G,
                grunnlagsfaktor = Grunnbeløp.finnBeregningsfaktor(år, beløpJustertFor6G)
            )
        }
    }

    internal fun grunnlagsfaktor() = grunnlagsfaktor

    internal fun toDto() = DtoInntektsgrunnlagForÅr(
        år = år,
        inntekter = inntekter.toDto(),
        beløpFørJustering = beløpFørJustering.toDto(),
        beløpJustertFor6G = beløpJustertFor6G.toDto(),
        erBeløpJustertFor6G = erBeløpJustertFor6G,
        grunnlagsfaktor = grunnlagsfaktor.toDto()
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
