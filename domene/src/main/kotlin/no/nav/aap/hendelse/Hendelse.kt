package no.nav.aap.hendelse

import no.nav.aap.hendelse.behov.Behov

internal open class Hendelse {
    private val behov = mutableListOf<Behov>()

    internal fun kansellerAlleBehov() = behov.clear()

    internal fun opprettBehov(behov: Behov) {
        this.behov.add(behov)
    }

    internal fun behov(): List<Behov> = behov.toList()
}
