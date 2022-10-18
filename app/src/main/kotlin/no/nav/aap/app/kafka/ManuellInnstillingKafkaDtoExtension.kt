package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.Innstilling_11_6
import no.nav.aap.modellapi.InnstillingParagraf_11_6ModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun Innstilling_11_6.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun Innstilling_11_6.toModellApi() = InnstillingParagraf_11_6ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)
