package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Beløp.Companion.summerBeløp
import no.nav.aap.dto.DtoInntekt
import no.nav.aap.visitor.BeregningVisitor
import no.nav.aap.visitor.SøkerVisitor
import java.time.Year
import java.time.YearMonth

class Inntekt(
    private val arbeidsgiver: Arbeidsgiver,
    private val inntekstmåned: YearMonth,
    private val beløp: Beløp
) {
    internal fun accept(visitor: BeregningVisitor) = visitor.visitInntektshistorikk(arbeidsgiver, inntekstmåned, beløp)

    internal companion object {
        internal fun Iterable<Inntekt>.inntektSiste3Kalenderår(år: Year) = this
            .filter { Year.from(it.inntekstmåned) in år.minusYears(2)..år }
            .groupBy { Year.from(it.inntekstmåned) }
            .map { (år, inntekter) -> InntekterForBeregning.inntekterForBeregning(år, inntekter) }

        internal fun Iterable<Inntekt>.summerInntekt() = map { it.beløp }.summerBeløp()

        internal fun Iterable<Inntekt>.toDto() = map(Inntekt::toDto)

        internal fun gjenopprett(inntekter: Iterable<DtoInntekt>) = inntekter.map {
            Inntekt(
                arbeidsgiver = Arbeidsgiver(it.arbeidsgiver),
                inntekstmåned = it.inntekstmåned,
                beløp = it.beløp.beløp
            )
        }
    }

    private fun toDto() = DtoInntekt(
        arbeidsgiver = arbeidsgiver.toDto(),
        inntekstmåned = inntekstmåned,
        beløp = beløp.toDto()
    )
}
