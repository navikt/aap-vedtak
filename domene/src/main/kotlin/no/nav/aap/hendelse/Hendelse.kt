package no.nav.aap.hendelse

open class Hendelse {
    private val behov = mutableListOf<Behov>()

    internal fun opprettBehov(behov: Behov) {
        this.behov.add(behov)
    }

    fun behov(): List<Behov> = behov.toList()
}

interface Behov
