package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

class Behov_22_13 : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoBehov_22_13(ident)
}
