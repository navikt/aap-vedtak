package no.nav.aap.domene

import no.nav.aap.domene.beregning.Beløp

internal class Vedtak(
    private val innvilget: Boolean,
    private val beregning: Beregning
) {

}

internal class Beregning(
    private val grunnlag: Beløp,

){

}
