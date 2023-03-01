package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal interface Behov {
    fun toDto(ident: String): BehovModellApi

    companion object {
        internal fun Iterable<Behov>.toDto(ident: String) = map { it.toDto(ident) }
    }
}
