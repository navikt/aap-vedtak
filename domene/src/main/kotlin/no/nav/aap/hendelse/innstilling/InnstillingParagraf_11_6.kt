package no.nav.aap.hendelse.innstilling

import no.nav.aap.hendelse.Hendelse
import no.nav.aap.modellapi.InnstillingParagraf_11_6ModellApi
import java.time.LocalDateTime
import java.util.*

internal class InnstillingParagraf_11_6(
    private val innstillingId: UUID,
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val harBehovForBehandling: Boolean,
    private val harBehovForTiltak: Boolean,
    private val harMulighetForÅKommeIArbeid: Boolean,
    private val individuellBegrunnelse: String,
) : Hendelse() {

    internal companion object {
        internal fun Iterable<InnstillingParagraf_11_6>.toDto() = map(InnstillingParagraf_11_6::toDto)
    }

    internal fun toDto() = InnstillingParagraf_11_6ModellApi(
        innstillingId = innstillingId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
        individuellBegrunnelse = individuellBegrunnelse,
    )
}
