package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_11_29 : Behov {
    override fun toDto(ident: String) = BehovModellApi.Behov_11_29_ModellApi(ident)
}
