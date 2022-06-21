package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningManuellMedlemskapYrkesskade

internal class LøsningManuellMedlemskapYrkesskade(
    private val vurdertAv: String,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun toDto() = DtoLøsningManuellMedlemskapYrkesskade(vurdertAv, erMedlem.name)
}
