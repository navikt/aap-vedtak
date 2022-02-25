package no.nav.aap.domene.beregning

import kotlin.math.round

class Beløp(verdi: Number) : Comparable<Beløp> {

    private val verdi: Double = round(verdi.toDouble() * 100) / 100

    companion object {
        internal fun Iterable<Beløp>.summerBeløp() = sumOf { it.verdi }.beløp
        val Number.beløp get() = Beløp(this)
    }

    internal operator fun plus(addend: Beløp) = Beløp(this.verdi + addend.verdi)

    internal operator fun times(faktor: Number) = Beløp(verdi * faktor.toDouble())
    internal operator fun times(faktor: Beløp) = this * faktor.verdi

    internal operator fun div(nevner: Number): Beløp = Beløp(verdi / nevner.toDouble())
    internal operator fun div(nevner: Beløp): Double = this.verdi / nevner.verdi

    internal fun toDto() = verdi
    internal fun toFrontendBeløp() = verdi

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
