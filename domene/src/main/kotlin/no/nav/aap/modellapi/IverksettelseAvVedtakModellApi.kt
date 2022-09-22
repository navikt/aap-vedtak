package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.Behov.Companion.toDto
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.IverksettelseAvVedtak

data class IverksettelseAvVedtakModellApi(
    val iverksattAv: String,
) {
    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterIverksettelse(løsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    private fun toLøsning() = IverksettelseAvVedtak(
        iverksattAv = iverksattAv,
    )
}