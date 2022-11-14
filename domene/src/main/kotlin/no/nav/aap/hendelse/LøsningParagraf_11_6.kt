package no.nav.aap.hendelse

import no.nav.aap.domene.vilkår.Kvalitetssikring
import no.nav.aap.domene.vilkår.Løsning
import no.nav.aap.domene.vilkår.Totrinnskontroll
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_6ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_6ModellApi
import java.time.LocalDateTime
import java.util.*

internal class LøsningParagraf_11_6(
    private val løsningId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val harBehovForBehandling: Boolean,
    private val harBehovForTiltak: Boolean,
    private val harMulighetForÅKommeIArbeid: Boolean,
    private val individuellBegrunnelse: String?,
) : Hendelse(), Løsning<LøsningParagraf_11_6, KvalitetssikringParagraf_11_6> {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_6>.toDto() = map(LøsningParagraf_11_6::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt(): Boolean =
        harBehovForBehandling || harBehovForTiltak || harMulighetForÅKommeIArbeid

    override fun matchMedKvalitetssikring(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_6, KvalitetssikringParagraf_11_6>,
        kvalitetssikring: KvalitetssikringParagraf_11_6
    ) {
        kvalitetssikring.matchMedLøsning(totrinnskontroll, løsningId)
    }

    override fun toDto() = LøsningParagraf_11_6ModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
        individuellBegrunnelse = individuellBegrunnelse,
    )
}

internal class KvalitetssikringParagraf_11_6(
    private val kvalitetssikringId: UUID,
    private val løsningId: UUID,
    private val kvalitetssikretAv: String,
    private val tidspunktForKvalitetssikring: LocalDateTime,
    private val erGodkjent: Boolean,
    private val begrunnelse: String?,
) : Hendelse(), Kvalitetssikring<LøsningParagraf_11_6, KvalitetssikringParagraf_11_6> {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_6>.toDto() = map(KvalitetssikringParagraf_11_6::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv

    override fun matchMedLøsning(
        totrinnskontroll: Totrinnskontroll<LøsningParagraf_11_6, KvalitetssikringParagraf_11_6>,
        løsningId: UUID
    ) {
        if (this.løsningId == løsningId) {
            totrinnskontroll.leggTilKvalitetssikring(this)
        }
    }

    override fun toDto() = KvalitetssikringParagraf_11_6ModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

