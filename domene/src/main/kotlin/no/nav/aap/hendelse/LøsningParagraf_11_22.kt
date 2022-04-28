package no.nav.aap.hendelse

import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr
import no.nav.aap.domene.beregning.Yrkesskade
import no.nav.aap.dto.DtoLøsningParagraf_11_22
import no.nav.aap.visitor.SøkerVisitor
import java.time.Year

class LøsningParagraf_11_22(
    private val erOppfylt: Boolean,
    private val andelNedsattArbeidsevne: Int,
    private val år: Year,
    private val antattÅrligArbeidsinntekt: Beløp
) : Hendelse() {

    internal fun accept(visitor: SøkerVisitor) {
        visitor.`preVisit §11-22 løsning`(andelNedsattArbeidsevne, år)
        antattÅrligArbeidsinntekt.accept(visitor)
        visitor.`postVisit §11-22 løsning`(andelNedsattArbeidsevne, år)
    }

    internal fun yrkesskade() = Yrkesskade(
        andelNedsattArbeidsevne = andelNedsattArbeidsevne.toDouble(),
        inntektsgrunnlag = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(år, antattÅrligArbeidsinntekt)
    )

    internal fun erOppfylt() = erOppfylt

    internal fun toDto() = DtoLøsningParagraf_11_22(
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.toDto()
    )
}
