package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_2

class LøsningManuellParagraf_11_2(
    private val vurdertAv: String,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI
    }

    internal companion object {
        internal fun Iterable<LøsningManuellParagraf_11_2>.toDto() = map(LøsningManuellParagraf_11_2::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun toDto(): DtoLøsningParagraf_11_2 = DtoLøsningParagraf_11_2(vurdertAv, erMedlem.name)
}
