package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_4AndreOgTredjeLeddModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_4AndreOgTredjeLedd(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erOppfylt: Boolean
) : Hendelse(), Løsning<LøsningParagraf_11_4AndreOgTredjeLedd, KvalitetssikringParagraf_11_4AndreOgTredjeLedd> {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_4AndreOgTredjeLedd>.toDto() =
            map(LøsningParagraf_11_4AndreOgTredjeLedd::toDto)
    }

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_4AndreOgTredjeLedd, KvalitetssikringParagraf_11_4AndreOgTredjeLedd>,
        kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    override fun toDto() = LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )
}

internal class KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(),
    Kvalitetssikring<LøsningParagraf_11_4AndreOgTredjeLedd, KvalitetssikringParagraf_11_4AndreOgTredjeLedd> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_4AndreOgTredjeLedd>.toDto() =
            map(KvalitetssikringParagraf_11_4AndreOgTredjeLedd::toDto)
    }

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_4AndreOgTredjeLedd, KvalitetssikringParagraf_11_4AndreOgTredjeLedd>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    override fun toDto() = KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

