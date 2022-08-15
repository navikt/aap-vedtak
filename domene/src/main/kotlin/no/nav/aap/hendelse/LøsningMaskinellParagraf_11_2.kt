package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningMaskinellParagraf_11_2
import java.time.LocalDateTime
import java.util.*

class LøsningMaskinellParagraf_11_2(
    private val løsningId: UUID,
    private val tidspunktForVurdering: LocalDateTime,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI, UAVKLART
    }

    internal companion object {
        internal fun Iterable<LøsningMaskinellParagraf_11_2>.toDto() = map(LøsningMaskinellParagraf_11_2::toDto)
    }

    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun erIkkeMedlem() = erMedlem == ErMedlem.NEI
    internal fun toDto() = DtoLøsningMaskinellParagraf_11_2(løsningId, tidspunktForVurdering, erMedlem.name)
}
