package no.nav.aap.hendelse.behov

import no.nav.aap.modellapi.BehovModellApi

internal class Behov_11_5_yrkesskade : Behov {
    override fun toDto(ident: String) = BehovModellApi.Behov_11_5_YrkesskadeModellApi(ident)
}
