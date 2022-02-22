package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

class BehovVurderingAvBeregningsdato : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoBehovVurderingAvBeregningsdato(ident)
}
