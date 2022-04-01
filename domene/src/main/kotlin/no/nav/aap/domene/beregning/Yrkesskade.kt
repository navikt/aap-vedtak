package no.nav.aap.domene.beregning

import no.nav.aap.domene.entitet.Grunnlagsfaktor

class Yrkesskade(
    private val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double
) {

        internal fun beregnEndeligGrunnlagsfaktor(
            grunnlagsfaktorForNedsattArbeidsevne: Grunnlagsfaktor,
            grunnlagsfaktorForYrkesskade: Grunnlagsfaktor
        ): Grunnlagsfaktor {
            return if (gradAvNedsattArbeidsevneKnyttetTilYrkesskade > 70) {
                maxOf(grunnlagsfaktorForNedsattArbeidsevne, grunnlagsfaktorForYrkesskade)
            } else {
                val gradAvNedsattArbeidsevneKnyttetTilNedsattArbeidsevne = 100 - gradAvNedsattArbeidsevneKnyttetTilYrkesskade
                val andelAvEndeligGrunnlagsfaktorSomIkkeSkalJusteres = grunnlagsfaktorForNedsattArbeidsevne * gradAvNedsattArbeidsevneKnyttetTilNedsattArbeidsevne / 100
                val andelAvEndeligGrunnlagsfaktorSomErJustert = maxOf(grunnlagsfaktorForNedsattArbeidsevne, grunnlagsfaktorForYrkesskade) * gradAvNedsattArbeidsevneKnyttetTilYrkesskade / 100
                andelAvEndeligGrunnlagsfaktorSomIkkeSkalJusteres + andelAvEndeligGrunnlagsfaktorSomErJustert
            }
        }

}