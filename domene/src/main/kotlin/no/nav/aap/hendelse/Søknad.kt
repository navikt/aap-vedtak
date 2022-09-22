package no.nav.aap.hendelse

import no.nav.aap.domene.Søker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident

internal class Søknad(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val erStudent: Boolean = false,
    private val harTidligereYrkesskade: HarYrkesskade = HarYrkesskade.NEI
) : Hendelse() {
    internal fun opprettSøker() = Søker(personident, fødselsdato)
    internal fun erStudent() = erStudent
    internal fun harTidligereYrkesskade() = harTidligereYrkesskade != HarYrkesskade.NEI
    internal fun harSøktUføretrygd() = false
    internal fun erArbeidssøker() = false

    internal enum class HarYrkesskade {
        JA, NEI, VET_IKKE
    }
}
