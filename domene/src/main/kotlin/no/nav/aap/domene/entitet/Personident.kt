package no.nav.aap.domene.entitet

import no.nav.aap.visitor.SøkerVisitor

class Personident(
    private val ident: String
) {

    internal fun accept(visitor: SøkerVisitor) = visitor.visitPersonident(ident)

    internal fun toDto() = ident

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
