package no.nav.aap.domene.beregning

import no.nav.aap.domene.entitet.Grunnlagsfaktor
import no.nav.aap.dto.DtoYrkesskade
import no.nav.aap.visitor.SøkerVisitor

internal class Yrkesskade(
    private val andelNedsattArbeidsevne: Double,
    private val inntektsgrunnlag: InntektsgrunnlagForÅr
) {

    fun accept(visitor: SøkerVisitor) {
        visitor.preVisitYrkesskade(andelNedsattArbeidsevne)
        inntektsgrunnlag.accept(visitor)
        visitor.postVisitYrkesskade(andelNedsattArbeidsevne)
    }

    internal fun beregnEndeligGrunnlagsfaktor(
        grunnlagsfaktorForNedsattArbeidsevne: Grunnlagsfaktor
    ): Grunnlagsfaktor {
        return if (andelNedsattArbeidsevne > 70) {
            maxOf(grunnlagsfaktorForNedsattArbeidsevne, inntektsgrunnlag.grunnlagsfaktor())
        } else {
            val gradAvNedsattArbeidsevneKnyttetTilNedsattArbeidsevne = 100 - andelNedsattArbeidsevne
            val andelAvEndeligGrunnlagsfaktorSomIkkeSkalJusteres =
                grunnlagsfaktorForNedsattArbeidsevne * gradAvNedsattArbeidsevneKnyttetTilNedsattArbeidsevne / 100
            val andelAvEndeligGrunnlagsfaktorSomErJustert = maxOf(
                grunnlagsfaktorForNedsattArbeidsevne,
                inntektsgrunnlag.grunnlagsfaktor()
            ) * andelNedsattArbeidsevne / 100
            andelAvEndeligGrunnlagsfaktorSomIkkeSkalJusteres + andelAvEndeligGrunnlagsfaktorSomErJustert
        }
    }

    internal fun toDto() = DtoYrkesskade(
        gradAvNedsattArbeidsevneKnyttetTilYrkesskade = andelNedsattArbeidsevne,
        inntektsgrunnlag = inntektsgrunnlag.toDto()
    )

    internal companion object {
        internal fun gjenopprett(dtoYrkesskade: DtoYrkesskade) = Yrkesskade(
            andelNedsattArbeidsevne = dtoYrkesskade.gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
            inntektsgrunnlag = InntektsgrunnlagForÅr.gjenopprett(dtoYrkesskade.inntektsgrunnlag)
        )
    }
}
