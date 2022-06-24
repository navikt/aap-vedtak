package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningVurderingAvBeregningsdato
import java.time.LocalDate

internal class LøsningVurderingAvBeregningsdato(
    private val vurdertAv: String,
    internal val beregningsdato: LocalDate
) : Hendelse() {
    internal companion object {
        internal fun Iterable<LøsningVurderingAvBeregningsdato>.toDto() = map(LøsningVurderingAvBeregningsdato::toDto)
        internal fun gjenopprett(dtoLøsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato) =
            LøsningVurderingAvBeregningsdato(
                vurdertAv = dtoLøsningVurderingAvBeregningsdato.vurdertAv,
                beregningsdato = dtoLøsningVurderingAvBeregningsdato.beregningsdato
            )
    }

    internal fun vurdertAv() = vurdertAv
    private fun toDto() = DtoLøsningVurderingAvBeregningsdato(vurdertAv, beregningsdato)
}
