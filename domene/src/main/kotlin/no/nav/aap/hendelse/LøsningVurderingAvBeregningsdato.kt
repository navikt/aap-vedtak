package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningVurderingAvBeregningsdato
import java.time.LocalDate
import java.time.LocalDateTime

internal class LøsningVurderingAvBeregningsdato(
    private val vurdertAv: String,
    private val tidspunktForVurdering: LocalDateTime,
    internal val beregningsdato: LocalDate
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningVurderingAvBeregningsdato>.toDto() = map(LøsningVurderingAvBeregningsdato::toDto)
        internal fun gjenopprett(dtoLøsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato) =
            LøsningVurderingAvBeregningsdato(
                vurdertAv = dtoLøsningVurderingAvBeregningsdato.vurdertAv,
                tidspunktForVurdering = dtoLøsningVurderingAvBeregningsdato.tidspunktForVurdering,
                beregningsdato = dtoLøsningVurderingAvBeregningsdato.beregningsdato
            )
    }

    internal fun vurdertAv() = vurdertAv
    private fun toDto() = DtoLøsningVurderingAvBeregningsdato(vurdertAv, tidspunktForVurdering, beregningsdato)
}
