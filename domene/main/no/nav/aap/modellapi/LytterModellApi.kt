package no.nav.aap.modellapi

import java.time.Year

interface LytterModellApi {
    fun medlem(ident: String) {}
    fun behov_8_48AndreLedd(ident: String) {}
    fun behov_11_3(ident: String) {}
    fun behov_11_4FørsteLedd(ident: String) {}
    fun behov_11_4AndreOgTredjeLedd(ident: String) {}
    fun behov_11_5(ident: String) {}
    fun behov_11_6(ident: String) {}
    fun behov_11_19(ident: String) {}
    fun behov_11_22(ident: String) {}
    fun behov_11_27(ident: String) {}
    fun behov_11_29(ident: String) {}
    fun behov_22_13(ident: String) {}
    fun behovInntekter(ident: String, fom: Year, tom: Year) {}
    fun behovIverksettVedtak(vedtakModellApi: VedtakModellApi) {}
}
