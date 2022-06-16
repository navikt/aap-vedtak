package no.nav.aap.hendelse

class LøsningParagraf_11_4AndreOgTredjeLedd(
    private val vurdertAv: String,
    private val erOppfylt: Boolean
) : Hendelse() {

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt
}
