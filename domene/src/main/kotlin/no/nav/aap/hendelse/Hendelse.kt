package no.nav.aap.hendelse

import java.time.Year

open class Hendelse {
    private val behov = mutableListOf<Behov>()

    internal fun kansellerAlleBehov() = behov.clear()

    internal fun opprettBehov(behov: Behov) {
        this.behov.add(behov)
    }

    fun behov(): List<Behov> = behov.toList()
}

interface Behov {
    fun toDto(ident: String): DtoBehov
}

interface Lytter {
    fun medlem(ident: String) {}
    fun behov_11_3(ident: String) {}
    fun behov_11_4FørsteLedd(ident: String) {}
    fun behov_11_4AndreOgTredjeLedd(ident: String) {}
    fun behov_11_5(ident: String) {}
    fun behov_11_6(ident: String) {}
    fun behov_11_12FørsteLedd(ident: String) {}
    fun behov_11_19(ident: String) {}
    fun behov_11_22(ident: String) {}
    fun behov_11_29(ident: String) {}
    fun behovInntekter(ident: String, fom: Year, tom: Year) {}
}

interface DtoBehov {
    fun accept(visitor: Lytter)

    class Medlem(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.medlem(ident)
        }
    }

    class DtoBehov_11_3(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_3(ident)
        }
    }

    class DtoBehov_11_4AndreOgTredjeLedd(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_4AndreOgTredjeLedd(ident)
        }
    }

    class DtoBehov_11_5(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_5(ident)
        }
    }

    class DtoBehov_11_5_yrkesskade(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_5(ident)
        }
    }

    class DtoBehov_11_6(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_6(ident)
        }
    }

    class DtoBehov_11_12FørsteLedd(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_12FørsteLedd(ident)
        }
    }

    class DtoBehov_11_22(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_22(ident)
        }
    }

    class DtoBehov_11_19(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_19(ident)
        }
    }

    class DtoBehov_11_29(private val ident: String) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behov_11_29(ident)
        }
    }

    class DtoInntekter(
        private val ident: String,
        private val fom: Year,
        private val tom: Year
    ) : DtoBehov {
        override fun accept(visitor: Lytter) {
            visitor.behovInntekter(ident, fom, tom)
        }
    }
}
