package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

class Behov_11_5_yrkesskade : Behov{
    override fun toDto(ident: String) = DtoBehov.DtoBehov_11_5_yrkesskade(ident)
}
