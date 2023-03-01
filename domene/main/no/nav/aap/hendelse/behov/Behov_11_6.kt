package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_11_6 : Behov {
    override fun toDto(ident: String) = BehovModellApi.Behov_11_6_ModellApi(ident)
}
