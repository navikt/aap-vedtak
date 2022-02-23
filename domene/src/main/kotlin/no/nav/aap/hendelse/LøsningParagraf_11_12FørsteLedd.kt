package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_12_ledd1

class LøsningParagraf_11_12FørsteLedd(private val erOppfylt: Boolean) : Hendelse() {
    internal fun erManueltOppfylt() = erOppfylt

    internal fun toDto() = DtoLøsningParagraf_11_12_ledd1(erOppfylt)
}
