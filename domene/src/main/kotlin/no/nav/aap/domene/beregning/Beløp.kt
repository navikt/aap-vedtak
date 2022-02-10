package no.nav.aap.domene.beregning

class Beløp(
    private val beløp: Double
) : Comparable<Beløp> {
    internal companion object {
        internal fun Iterable<Beløp>.summerBeløp() = Beløp(sumOf { it.beløp })
    }

    internal operator fun div(nevner: Int) = Beløp(beløp / nevner)

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
}
