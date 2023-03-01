package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringMedlemskapYrkesskadeModellApi
import no.nav.aap.modellapi.LøsningManuellMedlemskapYrkesskadeModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningManuellMedlemskapYrkesskade(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erMedlem: ErMedlem
) : Hendelse(), Løsning<LøsningManuellMedlemskapYrkesskade, KvalitetssikringMedlemskapYrkesskade> {
    internal enum class ErMedlem {
        JA, NEI
    }

    internal companion object {
        internal fun Iterable<LøsningManuellMedlemskapYrkesskade>.toDto() =
            map(LøsningManuellMedlemskapYrkesskade::toDto)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningManuellMedlemskapYrkesskade, KvalitetssikringMedlemskapYrkesskade>,
        kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    override fun toDto() =
        LøsningManuellMedlemskapYrkesskadeModellApi(løsningId, vurdertAv, tidspunktForVurdering, erMedlem.name)
}

internal class KvalitetssikringMedlemskapYrkesskade(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningManuellMedlemskapYrkesskade, KvalitetssikringMedlemskapYrkesskade> {

    internal companion object {
        internal fun Iterable<KvalitetssikringMedlemskapYrkesskade>.toDto() =
            map(KvalitetssikringMedlemskapYrkesskade::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningManuellMedlemskapYrkesskade, KvalitetssikringMedlemskapYrkesskade>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringMedlemskapYrkesskadeModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

