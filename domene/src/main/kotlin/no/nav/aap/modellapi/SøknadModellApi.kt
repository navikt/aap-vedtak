package no.nav.aap.modellapi

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.Behov.Companion.toDto
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Søknad
import java.time.LocalDate

data class SøknadModellApi(
    val personident: String,
    val fødselsdato: LocalDate,
    val erStudent: Boolean = false,
    val harTidligereYrkesskade: String = "NEI",
) {
    fun håndter(): Pair<SøkerModellApi, List<DtoBehov>> {
        val søknad = Søknad(
            personident = Personident(personident),
            fødselsdato = Fødselsdato(fødselsdato),
            erStudent = erStudent,
            harTidligereYrkesskade = enumValueOf(harTidligereYrkesskade)
        )
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)
        return søker.toDto() to søknad.behov().toDto(personident)
    }
}
