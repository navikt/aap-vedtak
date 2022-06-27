package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_4_ledd2_ledd3
import java.time.LocalDateTime

internal class LøsningParagraf_11_4AndreOgTredjeLedd(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    private val erOppfylt: Boolean
) : Hendelse() {

    internal companion object {
        internal fun Iterable<LøsningParagraf_11_4AndreOgTredjeLedd>.toDto() = map(LøsningParagraf_11_4AndreOgTredjeLedd::toDto)
    }

    internal fun vurdertAv() = vurdertAv
    internal fun erManueltOppfylt() = erOppfylt

    private fun toDto() = DtoLøsningParagraf_11_4_ledd2_ledd3(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )
}
