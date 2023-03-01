package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_22_13 : Behov {
    override fun toDto(ident: String) = BehovModellApi.Behov_22_13_ModellApi(ident)
}
