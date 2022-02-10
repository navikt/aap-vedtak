package no.nav.aap.domene

import no.nav.aap.domene.Beløp.Companion.summerBeløp
import no.nav.aap.domene.Inntekt.Companion.inntektSiste3Kalenderår
import no.nav.aap.domene.Inntekt.Companion.inntektSisteKalenderår
import no.nav.aap.domene.Inntekt.Companion.summerInntekt
import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    private val saker: MutableList<Sak> = mutableListOf()

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak()
        saker.add(sak)
        sak.håndterSøknad(søknad, fødselsdato)
    }

    fun håndterLøsning(løsning: LøsningParagraf_11_2) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    fun håndterLøsning(løsning: LøsningParagraf_11_5) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    fun toFrontendSaker() =
        saker.toFrontendSak(
            personident = personident,
            fødselsdato = fødselsdato
        )

    companion object {
        fun Iterable<Søker>.toFrontendSaker() = flatMap(Søker::toFrontendSaker)

        fun Iterable<Søker>.toFrontendSaker(personident: Personident) = this
            .filter { it.personident == personident }
            .flatMap(Søker::toFrontendSaker)
    }
}

internal class Inntektshistorikk(
    private val inntekter: List<Inntekt>
) {
    private companion object {
        private const val ANTALL_ÅR_FOR_GJENNOMSNITT = 3
    }

    internal fun beregnGrunnlag(beregningsdato: LocalDate): Grunnlagsberegning {
        val fjor = Year.from(beregningsdato).minusYears(1)
        val inntekterSisteKalenderår = inntektSisteKalenderår(fjor)
        val sumInntekterSisteKalenderår = inntekterSisteKalenderår.summerInntekt()
        val inntekterSiste3Kalenderår = inntektSiste3Kalenderår(fjor)
        val sumInntekterSiste3Kalenderår = inntekterSiste3Kalenderår.summerInntekt()

        val grunnlag = maxOf(sumInntekterSisteKalenderår, sumInntekterSiste3Kalenderår / ANTALL_ÅR_FOR_GJENNOMSNITT)

        return Grunnlagsberegning(
            grunnlag = grunnlag,
            inntekterSisteKalenderår = inntekterSisteKalenderår,
            inntekterSiste3Kalenderår = inntekterSiste3Kalenderår
        )
    }

    private fun inntektSisteKalenderår(år: Year): List<Inntekt> = inntekter.inntektSisteKalenderår(år)

    private fun inntektSiste3Kalenderår(år: Year): List<Inntekt> = inntekter.inntektSiste3Kalenderår(år)

}

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

internal class Inntekt(
    private val arbeidsgiver: Arbeidsgiver,
    private val inntekstmåned: YearMonth,
    private val beløp: Beløp
) {
    companion object {
        internal fun Iterable<Inntekt>.inntektSisteKalenderår(år: Year) = filter { Year.from(it.inntekstmåned) == år }

        internal fun Iterable<Inntekt>.inntektSiste3Kalenderår(år: Year) =
            filter { Year.from(it.inntekstmåned) in år.minusYears(2)..år }

        internal fun Iterable<Inntekt>.summerInntekt() = map { it.beløp }.summerBeløp()
    }

}

internal class Beløp(
    private val beløp: Double
) : Comparable<Beløp> {
    companion object {
        fun Iterable<Beløp>.summerBeløp() = Beløp(sumOf { it.beløp })
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

internal class Arbeidsgiver {

}
