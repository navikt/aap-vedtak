package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningManuellMedlemskapYrkesskade

class LøsningManuellMedlemskapYrkesskade(private val erMedlem: ErMedlem) : Hendelse() {
    enum class ErMedlem {
        JA, NEI
    }

    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun toDto() = DtoLøsningManuellMedlemskapYrkesskade(erMedlem.name)
}
