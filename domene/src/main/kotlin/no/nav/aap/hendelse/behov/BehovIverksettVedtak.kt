package no.nav.aap.hendelse.behov

import no.nav.aap.domene.Vedtak
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.DtoBehov

internal class BehovIverksettVedtak(
    private val vedtak: Vedtak,
) : Behov {
    override fun toDto(ident: String) = DtoBehov.DtoIverksettVedtak(vedtak.toDto())
}
