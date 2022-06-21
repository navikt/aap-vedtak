package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningVurderingAvBeregningsdato
import java.time.LocalDate

internal class LøsningVurderingAvBeregningsdato(
    private val vurdertAv: String,
    internal val beregningsdato: LocalDate
) : Hendelse() {

    internal fun vurdertAv() = vurdertAv
    internal fun toDto() = DtoLøsningVurderingAvBeregningsdato(vurdertAv, beregningsdato)

    internal companion object {
        internal fun gjenopprett(dtoLøsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato) =
            LøsningVurderingAvBeregningsdato(
                vurdertAv = dtoLøsningVurderingAvBeregningsdato.vurdertAv,
                beregningsdato = dtoLøsningVurderingAvBeregningsdato.beregningsdato
            )
    }
}
