package no.nav.aap.modellapi

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov.Companion.toDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SøknadModellApi(
    val personident: String,
    val fødselsdato: LocalDate,
    val søknadstidspunkt: LocalDateTime = LocalDateTime.now(),
    val erStudent: Boolean = false,
    val harTidligereYrkesskade: String = "NEI",
) {
    fun håndter(): Pair<SøkerModellApi, List<BehovModellApi>> {
        val søknad = Søknad(
            søknadId = UUID.randomUUID(), //TODO: Hente søknadId fra kafkameldingen. Mulig den mangler der også
            personident = Personident(personident),
            fødselsdato = Fødselsdato(fødselsdato),
            søknadstidspunkt = søknadstidspunkt,
            erStudent = erStudent,
            harTidligereYrkesskade = enumValueOf(harTidligereYrkesskade)
        )
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)
        return søker.toDto() to søknad.behov().toDto(personident)
    }
}
