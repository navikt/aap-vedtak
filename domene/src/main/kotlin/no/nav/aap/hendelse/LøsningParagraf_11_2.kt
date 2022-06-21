package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_2

class LøsningParagraf_11_2(
    private val vurdertAv: String,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI, UAVKLART
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun erIkkeMedlem() = erMedlem == ErMedlem.NEI
    internal fun toDto(): DtoLøsningParagraf_11_2 = DtoLøsningParagraf_11_2(vurdertAv, erMedlem.name)
}
