package no.nav.aap.hendelse

import no.nav.aap.domene.Søker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import java.time.LocalDateTime
import java.util.*

internal class Søknad(
    private val søknadId: UUID,
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val søknadstidspunkt: LocalDateTime = LocalDateTime.now(),
    private val erStudent: Boolean = false,
    private val harTidligereYrkesskade: HarYrkesskade = HarYrkesskade.NEI
) : Hendelse() {
    internal fun opprettSøker() = Søker(personident, fødselsdato)

    internal fun søknadId() = søknadId
    internal fun erStudent() = erStudent
    internal fun harTidligereYrkesskade() = harTidligereYrkesskade != HarYrkesskade.NEI
    internal fun harSøktUføretrygd() = false
    internal fun erArbeidssøker() = false
    internal fun søknadstidspunkt() = søknadstidspunkt

    internal enum class HarYrkesskade {
        JA, NEI, VET_IKKE
    }
}
