package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_8_48AndreLedd : Behov {
    override fun toDto(ident: String): BehovModellApi = BehovModellApi.Behov_8_48AndreLeddModellApi(ident)
}
