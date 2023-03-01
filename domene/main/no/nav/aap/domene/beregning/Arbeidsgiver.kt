package no.nav.aap.domene.beregning

internal class Arbeidsgiver(private val ident: String){
    internal fun toDto() = ident

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Arbeidsgiver

        if (ident != other.ident) return false

        return true
    }

    override fun hashCode(): Int {
        return ident.hashCode()
    }

    override fun toString(): String {
        return "Arbeidsgiver(ident='$ident')"
    }

}
