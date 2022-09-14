package no.nav.aap.hendelse

import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_19ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_19ModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_19(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    internal val beregningsdato: LocalDate
) : Hendelse() {
    internal fun accept(visitor: VilkårsvurderingVisitor) {
        visitor.visitLøsningParagraf_11_19(this, løsningId, vurdertAv, tidspunktForVurdering, beregningsdato)
    }

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_19>.toDto() = map(LøsningParagraf_11_19::toDto)
        internal fun gjenopprett(dtoLøsningParagraf1119: LøsningParagraf_11_19ModellApi) =
            LøsningParagraf_11_19(
                løsningId = dtoLøsningParagraf1119.løsningId,
                vurdertAv = dtoLøsningParagraf1119.vurdertAv,
                tidspunktForVurdering = dtoLøsningParagraf1119.tidspunktForVurdering,
                beregningsdato = dtoLøsningParagraf1119.beregningsdato
            )
    }

    internal fun vurdertAv() = vurdertAv
    private fun toDto() = LøsningParagraf_11_19ModellApi(løsningId, vurdertAv, tidspunktForVurdering, beregningsdato)
}

class KvalitetssikringParagraf_11_19(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_19>.toDto() = map(KvalitetssikringParagraf_11_19::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = KvalitetssikringParagraf_11_19ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
