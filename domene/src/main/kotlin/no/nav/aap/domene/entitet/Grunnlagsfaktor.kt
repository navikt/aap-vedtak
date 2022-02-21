package no.nav.aap.domene.entitet

import no.nav.aap.domene.beregning.Beløp
import kotlin.math.round

internal class Grunnlagsfaktor(verdi: Number) : Comparable<Grunnlagsfaktor> {

    private val verdi: Double = round(verdi.toDouble() * 1_000_000) / 1_000_000

    internal operator fun plus(addend: Grunnlagsfaktor) = Grunnlagsfaktor(this.verdi + addend.verdi)

    internal operator fun times(faktor: Beløp): Beløp = faktor * verdi

    internal operator fun div(nevner: Int) = Grunnlagsfaktor(verdi / nevner)

    override fun compareTo(other: Grunnlagsfaktor) = this.verdi.compareTo(other.verdi)

    internal companion object {
        internal fun Iterable<Grunnlagsfaktor>.summer() = Grunnlagsfaktor(sumOf { it.verdi })
    }

    internal fun toDto() = verdi

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Grunnlagsfaktor

        if (verdi != other.verdi) return false

        return true
    }

    override fun hashCode() = verdi.hashCode()
}
