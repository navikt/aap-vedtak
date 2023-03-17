package vedtak.kafka

import no.nav.aap.dto.kafka.Innstilling_11_6KafkaDto
import no.nav.aap.modellapi.InnstillingParagraf_11_6ModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun Innstilling_11_6KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun Innstilling_11_6KafkaDto.toModellApi() = InnstillingParagraf_11_6ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)
