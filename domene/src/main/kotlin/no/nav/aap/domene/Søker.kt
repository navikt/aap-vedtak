package no.nav.aap.domene

import no.nav.aap.domene.Beløp.Companion.summerBeløp
import no.nav.aap.domene.Inntekt.Companion.inntektSiste3år
import no.nav.aap.domene.Inntekt.Companion.inntektSisteÅr
import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import kotlin.math.max

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

    fun beregnGrunnlag(beregningsdato: LocalDate) {}

    private fun inntektSisteÅr(år: Year): List<Inntekt> = inntekter.inntektSisteÅr(år)

    private fun inntektSiste3år(år: Year): List<Inntekt> = inntekter.inntektSiste3år(år)

}

internal class Grunnlagsberegning(
    private val grunnlag: Beløp,
    private val inntektSisteÅr: List<Inntekt>,
    private val inntektSiste3år: List<Inntekt>
)

internal class Inntekt(
    private val arbeidsgiver: Arbeidsgiver,
    private val inntekstmåned: YearMonth,
    private val beløp: Beløp
) {
    companion object {
        internal fun Iterable<Inntekt>.inntektSisteÅr(år: Year) = filter { it.inntekstmåned.year == år.value }
        internal fun Iterable<Inntekt>.inntektSiste3år(år: Year) = (0..2).map { år.minusYears(it.toLong()) }.flatMap { inntektSisteÅr(it) }
        internal fun Iterable<Inntekt>.summerInntekt() = map { it.beløp }.summerBeløp()
    }

}

internal class Beløp(
    private val beløp: Double
) {
    companion object {
        fun Iterable<Beløp>.summerBeløp() = sumOf { it.beløp }
    }
}

internal class Arbeidsgiver {

}
