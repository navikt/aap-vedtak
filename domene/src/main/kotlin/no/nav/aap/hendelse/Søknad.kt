package no.nav.aap.hendelse

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.Lytter
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.Søker

class Søknad(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    fun opprettSøker(lytter: Lytter = object : Lytter {}) = Søker(personident, fødselsdato, lytter)
}
