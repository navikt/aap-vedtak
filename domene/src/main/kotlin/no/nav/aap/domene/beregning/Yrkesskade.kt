package no.nav.aap.domene.beregning

import no.nav.aap.domene.entitet.Grunnlagsfaktor
import no.nav.aap.dto.DtoYrkesskade

internal class Yrkesskade(
    private val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
    private val inntektsgrunnlag: InntektsgrunnlagForÅr
) {

        internal fun beregnEndeligGrunnlagsfaktor(
            grunnlagsfaktorForNedsattArbeidsevne: Grunnlagsfaktor
        ): Grunnlagsfaktor {
            return if (gradAvNedsattArbeidsevneKnyttetTilYrkesskade > 70) {
                maxOf(grunnlagsfaktorForNedsattArbeidsevne, inntektsgrunnlag.grunnlagsfaktor())
            } else {
                val gradAvNedsattArbeidsevneKnyttetTilNedsattArbeidsevne = 100 - gradAvNedsattArbeidsevneKnyttetTilYrkesskade
                val andelAvEndeligGrunnlagsfaktorSomIkkeSkalJusteres = grunnlagsfaktorForNedsattArbeidsevne * gradAvNedsattArbeidsevneKnyttetTilNedsattArbeidsevne / 100
                val andelAvEndeligGrunnlagsfaktorSomErJustert = maxOf(grunnlagsfaktorForNedsattArbeidsevne, inntektsgrunnlag.grunnlagsfaktor()) * gradAvNedsattArbeidsevneKnyttetTilYrkesskade / 100
                andelAvEndeligGrunnlagsfaktorSomIkkeSkalJusteres + andelAvEndeligGrunnlagsfaktorSomErJustert
            }
        }

    internal fun toDto() = DtoYrkesskade(
        gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
        inntektsgrunnlag = inntektsgrunnlag.toDto()
    )

    internal companion object{
        internal fun gjenopprett(dtoYrkesskade: DtoYrkesskade) = Yrkesskade(
            gradAvNedsattArbeidsevneKnyttetTilYrkesskade = dtoYrkesskade.gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
            inntektsgrunnlag = InntektsgrunnlagForÅr.gjenopprett(dtoYrkesskade.inntektsgrunnlag)
        )
    }
}
