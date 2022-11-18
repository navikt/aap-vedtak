package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_29ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_29ModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_29(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erOppfylt: Boolean
) : Hendelse(), Løsning<LøsningParagraf_11_29, KvalitetssikringParagraf_11_29> {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_29>.toDto() = map(LøsningParagraf_11_29::toDto)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_29, KvalitetssikringParagraf_11_29>,
        kvalitetssikring: KvalitetssikringParagraf_11_29
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    override fun toDto() = LøsningParagraf_11_29ModellApi(løsningId, vurdertAv, tidspunktForVurdering, erOppfylt)
}

internal class KvalitetssikringParagraf_11_29(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningParagraf_11_29, KvalitetssikringParagraf_11_29> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_29>.toDto() = map(KvalitetssikringParagraf_11_29::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_29, KvalitetssikringParagraf_11_29>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringParagraf_11_29ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}
