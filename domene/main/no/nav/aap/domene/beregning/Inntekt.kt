package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import no.nav.aap.modellapi.InntektModellApi
import java.time.Year
import java.time.YearMonth

internal class Inntekt(
    private val arbeidsgiver: Arbeidsgiver,
    private val inntekstmåned: YearMonth,
    private val beløp: Beløp
) {
    internal companion object {
        internal fun Iterable<Inntekt>.inntektSiste3Kalenderår(år: Year) = this
            .filter { Year.from(it.inntekstmåned) in år.minusYears(2)..år }
            .groupBy { Year.from(it.inntekstmåned) }
            .map { (år, inntekter) -> InntekterForBeregning.inntekterForBeregning(år, inntekter) }

        internal fun Iterable<Inntekt>.summerInntekt() = map { it.beløp }.summerBeløp()

        internal fun Iterable<Inntekt>.toDto() = map(Inntekt::toDto)

        internal fun gjenopprett(inntekter: Iterable<InntektModellApi>) = inntekter.map {
            Inntekt(
                arbeidsgiver = Arbeidsgiver(it.arbeidsgiver),
                inntekstmåned = it.inntekstmåned,
                beløp = it.beløp.beløp
            )
        }
    }

    private fun toDto() = InntektModellApi(
        arbeidsgiver = arbeidsgiver.toDto(),
        inntekstmåned = inntekstmåned,
        beløp = beløp.toDto()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Inntekt

        if (arbeidsgiver != other.arbeidsgiver) return false
        if (inntekstmåned != other.inntekstmåned) return false
        if (beløp != other.beløp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = arbeidsgiver.hashCode()
        result = 31 * result + inntekstmåned.hashCode()
        result = 31 * result + beløp.hashCode()
        return result
    }

    override fun toString(): String {
        return "Inntekt(arbeidsgiver=$arbeidsgiver, inntekstmåned=$inntekstmåned, beløp=$beløp)"
    }

}
