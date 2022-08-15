package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningMaskinellMedlemskapYrkesskade
import java.util.*

internal class LøsningMaskinellMedlemskapYrkesskade(
    private val løsningId: UUID,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI, UAVKLART
    }

    internal companion object {
        internal fun Iterable<LøsningMaskinellMedlemskapYrkesskade>.toDto() =
            map(LøsningMaskinellMedlemskapYrkesskade::toDto)
    }

    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun erIkkeMedlem() = erMedlem == ErMedlem.NEI
    internal fun toDto() = DtoLøsningMaskinellMedlemskapYrkesskade(løsningId, erMedlem.name)
}
