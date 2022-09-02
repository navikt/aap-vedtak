package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt.Companion.summerInntekt
import no.nav.aap.domene.beregning.Inntekt.Companion.toDto
import no.nav.aap.domene.beregning.InntekterForBeregning.Companion.toDto
import no.nav.aap.domene.beregning.InntekterForBeregning.Companion.totalBeregningsfaktor
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr.Companion.inntektsgrunnlagForÅr
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr.Companion.totalBeregningsfaktor
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import no.nav.aap.domene.entitet.Grunnlagsfaktor.Companion.summer
import no.nav.aap.dto.InntekterForBeregningModellApi
import no.nav.aap.dto.InntektsgrunnlagModellApi
import no.nav.aap.dto.InntektsgrunnlagForÅrModellApi
import java.time.LocalDate
import java.time.Year

internal class Inntektsgrunnlag private constructor(
    private val beregningsdato: LocalDate,
    private val inntekterSiste3Kalenderår: List<InntekterForBeregning>,
    private val yrkesskade: Yrkesskade? = null,
    private val fødselsdato: Fødselsdato,
    private val sisteKalenderår: Year,
    //Dette tallet representerer hele utregningen av 11-19
    private val grunnlagsfaktor: Grunnlagsfaktor
) {

    internal fun grunnlagForDag(dato: LocalDate) =
        Grunnbeløp.justerInntekt(dato, fødselsdato.justerGrunnlagsfaktorForAlder(dato, grunnlagsfaktor))

    internal fun toDto() = InntektsgrunnlagModellApi(
        beregningsdato = beregningsdato,
        inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.toDto(),
        yrkesskade = yrkesskade?.toDto(),
        fødselsdato = fødselsdato.toDto(),
        sisteKalenderår = sisteKalenderår,
        grunnlagsfaktor = grunnlagsfaktor.toDto()
    )

    internal companion object {
        internal fun inntektsgrunnlag(
            beregningsdato: LocalDate,
            inntekterSiste3Kalenderår: List<InntekterForBeregning>,
            fødselsdato: Fødselsdato,
            yrkesskade: Yrkesskade? = null,
        ): Inntektsgrunnlag {
            val sisteKalenderår = Year.from(beregningsdato).minusYears(1)
            return Inntektsgrunnlag(
                beregningsdato = beregningsdato,
                inntekterSiste3Kalenderår = inntekterSiste3Kalenderår,
                yrkesskade = yrkesskade,
                fødselsdato = fødselsdato,
                sisteKalenderår = sisteKalenderår,
                grunnlagsfaktor = inntekterSiste3Kalenderår.totalBeregningsfaktor(sisteKalenderår, yrkesskade)
            )
        }

        internal fun gjenopprett(inntektsgrunnlagModellApi: InntektsgrunnlagModellApi) = Inntektsgrunnlag(
            beregningsdato = inntektsgrunnlagModellApi.beregningsdato,
            inntekterSiste3Kalenderår = InntekterForBeregning.gjenopprett(inntektsgrunnlagModellApi.inntekterSiste3Kalenderår),
            yrkesskade = inntektsgrunnlagModellApi.yrkesskade?.let(
                Yrkesskade.Companion::gjenopprett
            ),
            fødselsdato = Fødselsdato(inntektsgrunnlagModellApi.fødselsdato),
            sisteKalenderår = inntektsgrunnlagModellApi.sisteKalenderår,
            grunnlagsfaktor = Grunnlagsfaktor(inntektsgrunnlagModellApi.grunnlagsfaktor)
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

internal class InntekterForBeregning private constructor(
    private val inntekter: List<Inntekt>,
    private val inntektsgrunnlagForÅr: InntektsgrunnlagForÅr
) {

    internal companion object {

        internal fun Iterable<InntekterForBeregning>.totalBeregningsfaktor(
            sisteKalenderår: Year,
            yrkesskade: Yrkesskade?
        ) = map { it.inntektsgrunnlagForÅr }.totalBeregningsfaktor(sisteKalenderår, yrkesskade)

        internal fun inntekterForBeregning(
            år: Year,
            inntekter: List<Inntekt>
        ): InntekterForBeregning {
            val beløpFørJustering = inntekter.summerInntekt()
            val inntektsgrunnlagForÅr = inntektsgrunnlagForÅr(
                år = år,
                beløpFørJustering = beløpFørJustering
            )
            return InntekterForBeregning(
                inntekter = inntekter,
                inntektsgrunnlagForÅr = inntektsgrunnlagForÅr
            )
        }

        internal fun Iterable<InntekterForBeregning>.toDto() = map(InntekterForBeregning::toDto)

        internal fun gjenopprett(inntekterForBeregningModellApi: Iterable<InntekterForBeregningModellApi>) =
            inntekterForBeregningModellApi.map {
                InntekterForBeregning(
                    inntekter = Inntekt.gjenopprett(it.inntekter),
                    inntektsgrunnlagForÅr = InntektsgrunnlagForÅr.gjenopprett(it.inntektsgrunnlagForÅr)
                )
            }
    }

    internal fun toDto() = InntekterForBeregningModellApi(
        inntekter = inntekter.toDto(),
        inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InntekterForBeregning

        if (inntekter != other.inntekter) return false
        if (inntektsgrunnlagForÅr != other.inntektsgrunnlagForÅr) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inntekter.hashCode()
        result = 31 * result + inntektsgrunnlagForÅr.hashCode()
        return result
    }

    override fun toString(): String {
        return "InntekterForBeregning(inntekter=$inntekter, inntektsgrunnlagForÅr=$inntektsgrunnlagForÅr)"
    }


}

internal class InntektsgrunnlagForÅr private constructor(
    private val år: Year,
    private val beløpFørJustering: Beløp,
    private val beløpJustertFor6G: Beløp,
    private val erBeløpJustertFor6G: Boolean,
    private val grunnlagsfaktor: Grunnlagsfaktor
) {

    internal companion object {
        private const val ANTALL_ÅR_FOR_GJENNOMSNITT = 3

        internal fun Iterable<InntektsgrunnlagForÅr>.totalBeregningsfaktor(
            sisteKalenderår: Year,
            yrkesskade: Yrkesskade?
        ): Grunnlagsfaktor {
            val sum1År = finnSisteKalenderår(sisteKalenderår).summerBeregningsfaktor()
            val gjennomsnitt3År = summerBeregningsfaktor() / ANTALL_ÅR_FOR_GJENNOMSNITT
            val grunnlagsfaktor = maxOf(sum1År, gjennomsnitt3År)
            if (yrkesskade != null) {
                return yrkesskade.beregnEndeligGrunnlagsfaktor(grunnlagsfaktor)
            }
            return grunnlagsfaktor
        }

        private fun Iterable<InntektsgrunnlagForÅr>.summerBeregningsfaktor() = map { it.grunnlagsfaktor }.summer()

        private fun Iterable<InntektsgrunnlagForÅr>.finnSisteKalenderår(sisteKalenderår: Year): List<InntektsgrunnlagForÅr> =
            singleOrNull { it.år == sisteKalenderår }?.let { listOf(it) } ?: emptyList()

        internal fun gjenopprett(inntekterSiste3Kalenderår: Iterable<InntektsgrunnlagForÅrModellApi>) =
            inntekterSiste3Kalenderår.map {
                InntektsgrunnlagForÅr(
                    år = it.år,
                    beløpFørJustering = it.beløpFørJustering.beløp,
                    beløpJustertFor6G = it.beløpJustertFor6G.beløp,
                    erBeløpJustertFor6G = it.erBeløpJustertFor6G,
                    grunnlagsfaktor = Grunnlagsfaktor(it.grunnlagsfaktor)
                )
            }

        internal fun gjenopprett(inntektsgrunnlagForYrkesskade: InntektsgrunnlagForÅrModellApi) =
            InntektsgrunnlagForÅr(
                år = inntektsgrunnlagForYrkesskade.år,
                beløpFørJustering = inntektsgrunnlagForYrkesskade.beløpFørJustering.beløp,
                beløpJustertFor6G = inntektsgrunnlagForYrkesskade.beløpJustertFor6G.beløp,
                erBeløpJustertFor6G = inntektsgrunnlagForYrkesskade.erBeløpJustertFor6G,
                grunnlagsfaktor = Grunnlagsfaktor(inntektsgrunnlagForYrkesskade.grunnlagsfaktor)
            )

        internal fun inntektsgrunnlagForÅr(
            år: Year,
            beløpFørJustering: Beløp
        ): InntektsgrunnlagForÅr {
            val beløpJustertFor6G = Grunnbeløp.beløpJustertFor6G(år, beløpFørJustering)
            return InntektsgrunnlagForÅr(
                år = år,
                beløpFørJustering = beløpFørJustering,
                beløpJustertFor6G = beløpJustertFor6G,
                erBeløpJustertFor6G = beløpFørJustering != beløpJustertFor6G,
                grunnlagsfaktor = Grunnbeløp.finnGrunnlagsfaktor(år, beløpJustertFor6G)
            )
        }
    }

    internal fun grunnlagsfaktor() = grunnlagsfaktor

    internal fun toDto() = InntektsgrunnlagForÅrModellApi(
        år = år,
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
        if (beløpFørJustering != other.beløpFørJustering) return false
        if (beløpJustertFor6G != other.beløpJustertFor6G) return false
        if (erBeløpJustertFor6G != other.erBeløpJustertFor6G) return false
        if (grunnlagsfaktor != other.grunnlagsfaktor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = år.hashCode()
        result = 31 * result + beløpFørJustering.hashCode()
        result = 31 * result + beløpJustertFor6G.hashCode()
        result = 31 * result + erBeløpJustertFor6G.hashCode()
        result = 31 * result + grunnlagsfaktor.hashCode()
        return result
    }
}
