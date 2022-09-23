package no.nav.aap.modellapi

import java.time.Year

interface BehovModellApi {
    fun accept(visitor: LytterModellApi)

    class BehovMedlemModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.medlem(ident)
        }
    }

    class Behov_8_48AndreLeddModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_8_48AndreLedd(ident)
        }
    }

    class Behov_11_3_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_3(ident)
        }
    }

    class Behov_11_4AndreOgTredjeLeddModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_4AndreOgTredjeLedd(ident)
        }
    }

    class Behov_11_5_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_5(ident)
        }
    }

    class Behov_11_5_YrkesskadeModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_5(ident)
        }
    }

    class Behov_11_6_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_6(ident)
        }
    }

    class Behov_11_19_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_19(ident)
        }
    }

    class Behov_11_22_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_22(ident)
        }
    }

    class Behov_11_27_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_27(ident)
        }
    }

    class Behov_11_29_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_11_29(ident)
        }
    }

    class Behov_22_13_ModellApi(private val ident: String) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behov_22_13(ident)
        }
    }

    class BehovInntekterModellApi(
        private val ident: String,
        private val fom: Year,
        private val tom: Year
    ) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behovInntekter(ident, fom, tom)
        }
    }

    class BehovIverksettVedtakModellApi(
        private val vedtakModellApi: VedtakModellApi,
    ) : BehovModellApi {
        override fun accept(visitor: LytterModellApi) {
            visitor.behovIverksettVedtak(vedtakModellApi)
        }
    }
}
