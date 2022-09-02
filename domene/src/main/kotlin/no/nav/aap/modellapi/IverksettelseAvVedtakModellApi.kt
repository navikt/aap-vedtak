package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.IverksettelseAvVedtak

data class IverksettelseAvVedtakModellApi(
    val iverksattAv: String,
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterIverksettelse(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = IverksettelseAvVedtak(
        iverksattAv = iverksattAv,
    )
}