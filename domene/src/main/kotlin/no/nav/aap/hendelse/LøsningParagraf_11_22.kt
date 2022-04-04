package no.nav.aap.hendelse

import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.dto.DtoLøsningParagraf_11_22
import java.time.Year

class LøsningParagraf_11_22(
    private val andelNedsattArbeidsevne: Int,
    private val år: Year,
    private val antattÅrligArbeidsinntekt: Beløp
) : Hendelse() {

    internal fun toDto() = DtoLøsningParagraf_11_22(
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.toDto()
    )
}
