package no.nav.aap.hendelse

import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.beregning.Inntektshistorikk

internal class LøsningInntekter(
    private val inntekter: List<Inntekt>
) : Hendelse() {

    internal fun lagreInntekter(inntektshistorikk: Inntektshistorikk) {
        inntektshistorikk.leggTilInntekter(inntekter)
    }
}
