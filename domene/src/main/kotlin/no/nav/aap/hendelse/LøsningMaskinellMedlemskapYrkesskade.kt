package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningMaskinellMedlemskapYrkesskade

class LøsningMaskinellMedlemskapYrkesskade(private val erMedlem: ErMedlem) : Hendelse() {
    enum class ErMedlem {
        JA, NEI, UAVKLART
    }

    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun erIkkeMedlem() = erMedlem == ErMedlem.NEI
    internal fun toDto() = DtoLøsningMaskinellMedlemskapYrkesskade(erMedlem.name)
}
