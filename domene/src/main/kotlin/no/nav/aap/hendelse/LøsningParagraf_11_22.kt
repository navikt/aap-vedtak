package no.nav.aap.hendelse

import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.InntektsgrunnlagForÅr
import no.nav.aap.domene.beregning.Yrkesskade
import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_22ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_22ModellApi
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
) : Hendelse(), Løsning<LøsningParagraf_11_22, KvalitetssikringParagraf_11_22> {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_22>.toDto() = map(LøsningParagraf_11_22::toDto)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_22, KvalitetssikringParagraf_11_22>,
        kvalitetssikring: KvalitetssikringParagraf_11_22
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    override fun accept(visitor: VilkårsvurderingVisitor) {
        visitor.visitLøsningParagraf_11_22(
            løsning = this,
            løsningId = løsningId,
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering
        )
    }

    internal fun vurdertAv() = vurdertAv

    internal fun yrkesskade() = Yrkesskade(
        andelNedsattArbeidsevne = andelNedsattArbeidsevne.toDouble(),
        inntektsgrunnlag = InntektsgrunnlagForÅr.inntektsgrunnlagForÅr(år, antattÅrligArbeidsinntekt)
    )

    internal fun erOppfylt() = erOppfylt

    override fun toDto() = LøsningParagraf_11_22ModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.toDto()
    )
}

internal class KvalitetssikringParagraf_11_22(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningParagraf_11_22, KvalitetssikringParagraf_11_22> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_22>.toDto() = map(KvalitetssikringParagraf_11_22::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_22, KvalitetssikringParagraf_11_22>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringParagraf_11_22ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
