package no.nav.aap.hendelse

import no.nav.aap.modellapi.KvalitetssikringMedlemskapYrkesskadeModellApi
import no.nav.aap.modellapi.LøsningManuellMedlemskapYrkesskadeModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningManuellMedlemskapYrkesskade(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erMedlem: ErMedlem
) : Hendelse() {
    internal enum class ErMedlem {
        JA, NEI
    }

    internal companion object {
        internal fun Iterable<LøsningManuellMedlemskapYrkesskade>.toDto() =
            map(LøsningManuellMedlemskapYrkesskade::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    internal fun toDto() =
        LøsningManuellMedlemskapYrkesskadeModellApi(løsningId, vurdertAv, tidspunktForVurdering, erMedlem.name)
}

internal class KvalitetssikringMedlemskapYrkesskade(
    private val kvalitetssikringId: UUID, 
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringMedlemskapYrkesskade>.toDto() =
            map(KvalitetssikringMedlemskapYrkesskade::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = KvalitetssikringMedlemskapYrkesskadeModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

