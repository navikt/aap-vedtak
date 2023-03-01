package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_11_5Yrkesskade : Behov {
    override fun toDto(ident: String) = BehovModellApi.Behov_11_5YrkesskadeModellApi(ident)
}
