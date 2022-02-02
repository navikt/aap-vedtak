package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.OppgavesvarParagraf_11_2
import no.nav.aap.hendelse.OppgavesvarParagraf_11_5
import no.nav.aap.hendelse.Søknad

class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val lytter: Lytter
) {
    private val saker: MutableList<Sak> = mutableListOf()

    init {
        lytter.oppdaterSøker(this)
    }

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak(lytter)
        saker.add(sak)
        sak.håndterSøknad(søknad, fødselsdato)
    }

    fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_2) {
        saker.forEach { it.håndterOppgavesvar(oppgavesvar) }
    }

    fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_5) {
        saker.forEach { it.håndterOppgavesvar(oppgavesvar) }
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
