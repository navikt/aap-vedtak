package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Paragraf_11_5
import no.nav.aap.dto.DtoLøsningParagraf_11_5
import java.time.LocalDateTime

internal class LøsningParagraf_11_5(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad
) : Hendelse() {
    class NedsattArbeidsevnegrad(
        private val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        private val nedsettelseSkyldesSykdomEllerSkade: Boolean,
    ) {

        internal fun erOppfylt() = kravOmNedsattArbeidsevneErOppfylt && nedsettelseSkyldesSykdomEllerSkade

        internal fun toDto(vurdertAv: String, tidspunktForVurdering: LocalDateTime) = DtoLøsningParagraf_11_5(
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    }

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_5>.toDto() = map(LøsningParagraf_11_5::toDto)
    }

    internal fun vurdertAv() = vurdertAv

    internal fun vurderNedsattArbeidsevne(vilkår: Paragraf_11_5.SøknadMottatt, vilkårsvurdering: Paragraf_11_5) {
        vilkår.vurderNedsattArbeidsevne(vilkårsvurdering, this, nedsattArbeidsevnegrad)
    }

    internal fun toDto() = nedsattArbeidsevnegrad.toDto(vurdertAv, tidspunktForVurdering)
}
