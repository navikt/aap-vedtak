package no.nav.aap.hendelse.behov

import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

internal class Behov_11_4AndreOgTredjeLedd : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoBehov_11_4AndreOgTredjeLedd(ident)
}
