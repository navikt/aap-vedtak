package no.nav.aap.hendelse

class LøsningParagraf_11_2(private val medlemskap: Medlemskap) : Hendelse() {
    class Medlemskap(private val svar: Svar) {
        enum class Svar {
            JA, VET_IKKE, NEI
        }

        internal fun erMedlem() = svar == Svar.JA
        internal fun erIkkeMedlem() = svar == Svar.NEI
    }

    internal fun erMedlem() = medlemskap.erMedlem()
    internal fun erIkkeMedlem() = medlemskap.erIkkeMedlem()
}
