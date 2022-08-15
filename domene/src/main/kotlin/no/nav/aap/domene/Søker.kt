package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toDto
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.dto.DtoSøker
import no.nav.aap.hendelse.*

class Søker private constructor(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val saker: MutableList<Sak>
) {
    constructor(personident: Personident, fødselsdato: Fødselsdato) : this(personident, fødselsdato, mutableListOf())

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak()
        saker.add(sak)
        sak.håndterSøknad(søknad, fødselsdato)
    }

    internal fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    fun håndterLøsning(løsning: LøsningMaskinellParagraf_11_2) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningManuellParagraf_11_2) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_3) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5Yrkesskade) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_22) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_19) {
        saker.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningInntekter) {
        saker.forEach { it.håndterLøsning(løsning, fødselsdato) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringMedlemskapYrkesskade) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_2) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_3) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_6) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_12FørsteLedd) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_19) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_22) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_29) {
        saker.forEach { it.håndterKvalitetssikring(kvalitetssikring) }
    }

    fun toDto() = DtoSøker(
        personident = personident.toDto(),
        fødselsdato = fødselsdato.toDto(),
        saker = saker.toDto()
    )

    companion object {
        fun gjenopprett(søker: DtoSøker): Søker = Søker(
            personident = Personident(søker.personident),
            fødselsdato = Fødselsdato(søker.fødselsdato),
            saker = søker.saker.map(Sak::gjenopprett).toMutableList(),
        )
    }
}
