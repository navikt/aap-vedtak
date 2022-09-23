package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_11_22 : Behov {
    override fun toDto(ident: String) = BehovModellApi.Behov_11_22_ModellApi(ident)
}
