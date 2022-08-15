package no.nav.aap.hendelse

import no.nav.aap.dto.DtoKvalitetssikringMedlemskapYrkesskade
import no.nav.aap.dto.DtoLøsningManuellMedlemskapYrkesskade
import java.time.LocalDateTime
import java.util.*

internal class LøsningManuellMedlemskapYrkesskade(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erMedlem: ErMedlem
) : Hendelse() {
    enum class ErMedlem {
        JA, NEI
    }

    internal companion object {
        internal fun Iterable<LøsningManuellMedlemskapYrkesskade>.toDto() =
            map(LøsningManuellMedlemskapYrkesskade::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun toDto() =
        DtoLøsningManuellMedlemskapYrkesskade(løsningId, vurdertAv, tidspunktForVurdering, erMedlem.name)
}

class KvalitetssikringMedlemskapYrkesskade(
    private val kvalitetssikringId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringMedlemskapYrkesskade>.toDto() =
            map(KvalitetssikringMedlemskapYrkesskade::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = DtoKvalitetssikringMedlemskapYrkesskade(
        kvalitetssikringId = kvalitetssikringId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

