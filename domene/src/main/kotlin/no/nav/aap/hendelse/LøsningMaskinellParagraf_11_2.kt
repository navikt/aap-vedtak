package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_2

class LøsningMaskinellParagraf_11_2(
    private val vurdertAv: String,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI, UAVKLART
    }

    internal companion object {
        internal fun Iterable<LøsningMaskinellParagraf_11_2>.toDto() = map(LøsningMaskinellParagraf_11_2::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun erIkkeMedlem() = erMedlem == ErMedlem.NEI
    internal fun toDto(): DtoLøsningParagraf_11_2 = DtoLøsningParagraf_11_2(vurdertAv, erMedlem.name)
}
