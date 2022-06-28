package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

class Behov_11_19 : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoBehov_11_19(ident)
}
