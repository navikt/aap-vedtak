package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Paragraf_11_5
import no.nav.aap.dto.DtoLøsningParagraf_11_5

class LøsningParagraf_11_5(private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad) : Hendelse() {
    class NedsattArbeidsevnegrad(
        private val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        private val nedsettelseSkyldesSykdomEllerSkade: Boolean,
    ) {

        internal fun erOppfylt() = kravOmNedsattArbeidsevneErOppfylt && nedsettelseSkyldesSykdomEllerSkade

        internal fun toDto() = DtoLøsningParagraf_11_5(
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    }

    internal fun vurderNedsattArbeidsevne(vilkår: Paragraf_11_5) {
        vilkår.vurderNedsattArbeidsevne(this, nedsattArbeidsevnegrad)
    }

    internal fun toDto() = nedsattArbeidsevnegrad.toDto()
}
