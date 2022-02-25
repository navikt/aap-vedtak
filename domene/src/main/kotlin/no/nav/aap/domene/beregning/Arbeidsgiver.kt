package no.nav.aap.domene.beregning

class Arbeidsgiver(private val ident: String = "HAHA"){
    internal fun toDto() = ident
    internal fun toFrontendArbeidsgiver() = ident
}
