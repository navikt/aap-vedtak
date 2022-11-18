package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_2ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_2ModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningManuellParagraf_11_2(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erMedlem: ErMedlem
) : Hendelse(), Løsning<LøsningManuellParagraf_11_2, KvalitetssikringParagraf_11_2> {
    internal enum class ErMedlem {
        JA, NEI
    }

    internal companion object {
        internal fun Iterable<LøsningManuellParagraf_11_2>.toDto() = map(LøsningManuellParagraf_11_2::toDto)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningManuellParagraf_11_2, KvalitetssikringParagraf_11_2>,
        kvalitetssikring: KvalitetssikringParagraf_11_2
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erMedlem() = erMedlem == ErMedlem.JA
    override fun toDto(): LøsningParagraf_11_2ModellApi =
        LøsningParagraf_11_2ModellApi(løsningId, vurdertAv, tidspunktForVurdering, erMedlem.name)
}

internal class KvalitetssikringParagraf_11_2(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningManuellParagraf_11_2, KvalitetssikringParagraf_11_2> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_2>.toDto() = map(KvalitetssikringParagraf_11_2::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningManuellParagraf_11_2, KvalitetssikringParagraf_11_2>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringParagraf_11_2ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
