package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningParagraf_11_19
import java.time.LocalDate
import java.time.LocalDateTime

internal class LøsningParagraf_11_19(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    internal val beregningsdato: LocalDate
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningParagraf_11_19>.toDto() = map(LøsningParagraf_11_19::toDto)
        internal fun gjenopprett(dtoLøsningParagraf1119: DtoLøsningParagraf_11_19) =
            LøsningParagraf_11_19(
                vurdertAv = dtoLøsningParagraf1119.vurdertAv,
                tidspunktForVurdering = dtoLøsningParagraf1119.tidspunktForVurdering,
                beregningsdato = dtoLøsningParagraf1119.beregningsdato
            )
    }

    internal fun vurdertAv() = vurdertAv
    private fun toDto() = DtoLøsningParagraf_11_19(vurdertAv, tidspunktForVurdering, beregningsdato)
}
