package no.nav.aap.domene.entitet

class Personident(
    private val ident: String
) {
    internal fun toFrontendPersonident() = ident

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Personident

        if (ident != other.ident) return false

        return true
    }

    override fun hashCode(): Int {
        return ident.hashCode()
    }
}
