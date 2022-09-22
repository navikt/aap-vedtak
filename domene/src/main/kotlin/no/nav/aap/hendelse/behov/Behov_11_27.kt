package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

internal class Behov_11_27 : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoBehov_11_27(ident)
}
