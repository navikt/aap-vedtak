package no.nav.aap.domene.beregning

internal class Grunnlagsberegning(
    private val grunnlag: Beløp,
    private val inntekterSisteKalenderår: List<Inntekt>,
    private val inntekterSiste3Kalenderår: List<Inntekt>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Grunnlagsberegning

        if (grunnlag != other.grunnlag) return false
        if (inntekterSisteKalenderår != other.inntekterSisteKalenderår) return false
        if (inntekterSiste3Kalenderår != other.inntekterSiste3Kalenderår) return false

        return true
    }

    override fun hashCode(): Int {
        var result = grunnlag.hashCode()
        result = 31 * result + inntekterSisteKalenderår.hashCode()
        result = 31 * result + inntekterSiste3Kalenderår.hashCode()
        return result
    }
}
