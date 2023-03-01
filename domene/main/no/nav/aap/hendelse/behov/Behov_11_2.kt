package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_11_2 : Behov {
    override fun toDto(ident: String) = BehovModellApi.BehovMedlemModellApi(ident)
}
