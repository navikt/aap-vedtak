package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Inntekt.Companion.inntektSiste3Kalenderår
import java.time.LocalDate
import java.time.Year

internal class Inntektshistorikk {
    private val inntekter = mutableListOf<Inntekt>()

    internal fun leggTilInntekter(inntekter: List<Inntekt>) {
        this.inntekter.addAll(inntekter)
    }

    internal fun finnInntektsgrunnlag(beregningsdato: LocalDate): Inntektsgrunnlag {
        val sisteKalenderår = Year.from(beregningsdato).minusYears(1)
        val inntekterSiste3Kalenderår = inntekter.inntektSiste3Kalenderår(sisteKalenderår)
        return Inntektsgrunnlag(
            sisteKalenderår = sisteKalenderår,
            inntekterSiste3Kalenderår = inntekterSiste3Kalenderår
        )
    }
}
