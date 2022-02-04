package no.nav.aap.hendelse

import no.nav.aap.domene.Søker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident

class Søknad(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) : Hendelse() {
    fun opprettSøker() = Søker(personident, fødselsdato)
}
