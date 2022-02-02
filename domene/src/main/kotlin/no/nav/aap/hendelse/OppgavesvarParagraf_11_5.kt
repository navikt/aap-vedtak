package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Paragraf_11_5

class OppgavesvarParagraf_11_5(private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad) {
    class NedsattArbeidsevnegrad(private val grad: Int) {
        internal fun erNedsattMedMinstHalvparten() = grad >= 50
    }

    internal fun vurderNedsattArbeidsevne(vilkår: Paragraf_11_5) {
        vilkår.vurderNedsattArbeidsevne(this, nedsattArbeidsevnegrad)
    }
}
