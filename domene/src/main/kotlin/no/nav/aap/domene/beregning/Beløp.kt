package no.nav.aap.domene.beregning

class Beløp(
    private val beløp: Double
) : Comparable<Beløp> {
    internal companion object {
        internal fun Iterable<Beløp>.summerBeløp() = Beløp(sumOf { it.beløp })
    }

    internal operator fun times(nevner: Int) = Beløp(beløp * nevner)
    internal operator fun times(nevner: Beløp) = Beløp(beløp * nevner.beløp)

    internal operator fun div(nevner: Int) = Beløp(beløp / nevner)
    internal operator fun div(nevner: Beløp) = Beløp(beløp / nevner.beløp)

    override fun compareTo(other: Beløp) = beløp.compareTo(other.beløp)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Beløp

        if (beløp != other.beløp) return false

        return true
    }

    override fun hashCode(): Int {
        return beløp.hashCode()
    }

    override fun toString() = "Beløp($beløp)"
}
