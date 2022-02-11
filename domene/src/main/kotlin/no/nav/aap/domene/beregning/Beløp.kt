package no.nav.aap.domene.beregning

class Beløp(verdi: Number) : Comparable<Beløp> {

    private val verdi: Double = verdi.toDouble()

    companion object {
        internal fun Iterable<Beløp>.summerBeløp() = Beløp(sumOf { it.verdi })
        val Number.beløp get() = Beløp(this)
    }

    internal operator fun plus(nevner: Beløp) = Beløp(this.verdi + nevner.verdi)

    internal operator fun times(nevner: Number) = Beløp(verdi * nevner.toDouble())
    internal operator fun times(nevner: Beløp) = this * nevner.verdi

    internal operator fun div(nevner: Number) = Beløp(verdi / nevner.toDouble())
    internal operator fun div(nevner: Beløp) = this / nevner.verdi

    override fun compareTo(other: Beløp) = verdi.compareTo(other.verdi)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Beløp

        if (verdi != other.verdi) return false

        return true
    }

    override fun hashCode() = verdi.hashCode()

    override fun toString() = "Beløp($verdi)"
}
