package no.nav.aap.hendelse

import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_2
import no.nav.aap.dto.DtoKvalitetssikringParagraf_11_6
import no.nav.aap.dto.DtoLøsningParagraf_11_6
import java.time.LocalDateTime

internal class LøsningParagraf_11_6(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val harBehovForBehandling: Boolean,
    private val harBehovForTiltak: Boolean,
    private val harMulighetForÅKommeIArbeid: Boolean
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_6>.toDto() = map(LøsningParagraf_11_6::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt(): Boolean =
        harBehovForBehandling && harBehovForTiltak && harMulighetForÅKommeIArbeid

    internal fun toDto() = DtoLøsningParagraf_11_6(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )
}

class KvalitetssikringParagraf_11_6(
    private val kvalitetssikretAv: String,
    private val erGodkjent: Boolean,
    private val begrunnelse: String
) : Hendelse() {

    internal companion object {
        internal fun Iterable<KvalitetssikringParagraf_11_6>.toDto() = map(KvalitetssikringParagraf_11_6::toDto)
    }

    internal fun erGodkjent() = erGodkjent
    internal fun kvalitetssikretAv() = kvalitetssikretAv
    internal fun toDto() = DtoKvalitetssikringParagraf_11_6(kvalitetssikretAv, erGodkjent, begrunnelse)
}

