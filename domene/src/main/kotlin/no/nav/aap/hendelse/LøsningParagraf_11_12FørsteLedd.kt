package no.nav.aap.hendelse

class LøsningParagraf_11_12FørsteLedd(private val erOppfylt: Boolean) : Hendelse() {
    internal fun erManueltOppfylt() = erOppfylt
}
