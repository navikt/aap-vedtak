package no.nav.aap.hendelse

import no.nav.aap.dto.DtoLøsningVurderingAvBeregningsdato
import java.time.LocalDate

class LøsningVurderingAvBeregningsdato(
    internal val beregningsdato: LocalDate
) : Hendelse() {

    internal fun toDto() = DtoLøsningVurderingAvBeregningsdato(beregningsdato)

    internal companion object {
        internal fun create(dtoLøsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato) =
            LøsningVurderingAvBeregningsdato(dtoLøsningVurderingAvBeregningsdato.beregningsdato)
    }
}
