package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_29

class LøsningParagraf_11_29(private val erOppfylt: Boolean) : Hendelse() {
    internal fun erManueltOppfylt() = erOppfylt

    internal fun toDto() = DtoLøsningParagraf_11_29(erOppfylt)
}
