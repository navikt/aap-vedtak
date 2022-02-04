package no.nav.aap.domene

interface Lytter {
    fun oppdaterSøker(søker: Søker) {}
    fun sendOppgave(behov: Behov) {}
    fun finalize() {}
}

interface Behov
