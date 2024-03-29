package no.nav.aap.hendelse.behov

import no.nav.aap.domene.Vedtak
import no.nav.aap.modellapi.BehovModellApi

internal class BehovIverksettVedtak(
    private val vedtak: Vedtak,
) : Behov {
    override fun toDto(ident: String) = BehovModellApi.BehovIverksettVedtakModellApi(vedtak.toDto())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BehovIverksettVedtak

        if (vedtak != other.vedtak) return false

        return true
    }

    override fun hashCode() = vedtak.hashCode()

    override fun toString() = vedtak.toString()
}
