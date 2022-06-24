package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_3

internal class LøsningParagraf_11_3(
    private val vurdertAv: String,
    private val erOppfylt: Boolean
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_3>.toDto() = map(LøsningParagraf_11_3::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    internal fun toDto() = DtoLøsningParagraf_11_3(vurdertAv, erOppfylt)
}
