package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toDto
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.dto.DtoSøker
import no.nav.aap.hendelse.*
import no.nav.aap.visitor.SøkerVisitor

class Søker private constructor(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val saker: MutableList<Sak>
) {
    constructor(personident: Personident, fødselsdato: Fødselsdato) : this(personident, fødselsdato, mutableListOf())

    internal fun accept(visitor: SøkerVisitor) {
        visitor.preVisitSøker()
        personident.accept(visitor)
        fødselsdato.accept(visitor)
        saker.forEach { it.accept(visitor) }
        visitor.postVisitSøker()
    }

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak()
        saker.add(sak)
        sak.håndterSøknad(søknad, fødselsdato)
    }

    fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_2) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_3) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_5) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_5_yrkesskade) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_6) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_22) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningParagraf_11_29) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningVurderingAvBeregningsdato) = saker.forEach { it.håndterLøsning(løsning) }
    fun håndterLøsning(løsning: LøsningInntekter) = saker.forEach { it.håndterLøsning(løsning, fødselsdato) }

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
