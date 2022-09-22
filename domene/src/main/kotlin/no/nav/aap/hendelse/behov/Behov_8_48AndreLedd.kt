package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

internal class Behov_8_48AndreLedd : Behov {
    override fun toDto(ident: String): DtoBehov = DtoBehov.DtoBehov_8_48AndreLedd(ident)
}
