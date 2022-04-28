package no.nav.aap.visitor

import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

internal interface BeregningVisitor {
    fun preVisitInntektsgrunnlag(beregningsdato: LocalDate, sisteKalenderår: Year) {}
    fun postVisitInntektsgrunnlag(beregningsdato: LocalDate, sisteKalenderår: Year) {}

    fun preVisitYrkesskade(andelNedsattArbeidsevne: Double) {}
    fun postVisitYrkesskade(andelNedsattArbeidsevne: Double) {}

    fun visitInntektshistorikk(arbeidsgiver: Arbeidsgiver, inntekstmåned: YearMonth, beløp: Beløp) {}
    fun visitInntektsgrunnlagForÅr(
        år: Year, beløpFørJustering: Beløp, beløpJustertFor6G: Beløp,
        erBeløpJustertFor6G: Boolean, grunnlagsfaktor: Grunnlagsfaktor
    ) {}

    fun preVisitInntekterForBeregning() {}
    fun postVisitInntekterForBeregning() {}

    fun preVisitInntektshistorikk() {}
    fun postVisitInntektshistorikk() {}

    fun visitBeløp(verdi: Double) {}
}
