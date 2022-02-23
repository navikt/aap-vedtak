package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Paragraf_11_5
import no.nav.aap.dto.DtoLøsningParagraf_11_5

class LøsningParagraf_11_5(private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad) : Hendelse() {
    class NedsattArbeidsevnegrad(private val grad: Int) {
        internal fun erNedsattMedMinstHalvparten() = grad >= 50

        internal fun toDto() = DtoLøsningParagraf_11_5(grad)
    }

    internal fun vurderNedsattArbeidsevne(vilkår: Paragraf_11_5) {
        vilkår.vurderNedsattArbeidsevne(this, nedsattArbeidsevnegrad)
    }

    internal fun toDto() = nedsattArbeidsevnegrad.toDto()
}
