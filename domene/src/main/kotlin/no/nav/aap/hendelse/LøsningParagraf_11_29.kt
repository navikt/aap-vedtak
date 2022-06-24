package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_29

internal class LøsningParagraf_11_29(
    private val vurdertAv: String,
    private val erOppfylt: Boolean
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_29>.toDto() = map(LøsningParagraf_11_29::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    private fun toDto() = DtoLøsningParagraf_11_29(vurdertAv, erOppfylt)
}
