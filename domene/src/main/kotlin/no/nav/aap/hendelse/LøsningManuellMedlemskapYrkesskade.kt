package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningManuellMedlemskapYrkesskade
import java.time.LocalDateTime

internal class LøsningManuellMedlemskapYrkesskade(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI
    }

    internal companion object {
        internal fun Iterable<LøsningManuellMedlemskapYrkesskade>.toDto() = map(LøsningManuellMedlemskapYrkesskade::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun toDto() = DtoLøsningManuellMedlemskapYrkesskade(vurdertAv, tidspunktForVurdering, erMedlem.name)
}
