package no.nav.aap.hendelse

import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr
import no.nav.aap.domene.beregning.Yrkesskade
import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_22
import no.nav.aap.dto.DtoLøsningParagraf_11_22
import java.time.LocalDateTime
import java.time.Year
import java.util.*

internal class LøsningParagraf_11_22(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erOppfylt: Boolean,
    private val andelNedsattArbeidsevne: Int,
    private val år: Year,
    private val antattÅrligArbeidsinntekt: Beløp
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_22>.toDto() = map(LøsningParagraf_11_22::toDto)
    }

    internal fun vurdertAv() = vurdertAv

    internal fun yrkesskade() = Yrkesskade(
        andelNedsattArbeidsevne = andelNedsattArbeidsevne.toDouble(),
        inntektsgrunnlag = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(år, antattÅrligArbeidsinntekt)
    )

    internal fun erOppfylt() = erOppfylt

    internal fun toDto() = DtoLøsningParagraf_11_22(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.toDto()
    )
}

class KvalitetssikringParagraf_11_22(
    private val kvalitetssikringId: UUID, 
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_22>.toDto() = map(KvalitetssikringParagraf_11_22::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = DtoKvalitetssikringParagraf_11_22(
        kvalitetssikringId = kvalitetssikringId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
