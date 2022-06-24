package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_6

internal class LøsningParagraf_11_6(
    private val vurdertAv: String,
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
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )
}
