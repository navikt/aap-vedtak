package no.nav.aap.domene

interface Lytter {
    fun oppdaterSøker(søker: Søker) {}
    fun sendOppgave(oppgave: Oppgave) {}
    fun finalize() {}
}

interface Oppgave
