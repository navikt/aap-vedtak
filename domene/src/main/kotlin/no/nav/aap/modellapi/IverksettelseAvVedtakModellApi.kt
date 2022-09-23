package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.IverksettelseAvVedtak
import no.nav.aap.hendelse.behov.Behov.Companion.toDto

data class IverksettelseAvVedtakModellApi(
    val iverksattAv: String,
) {
    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterIverksettelse(løsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    private fun toLøsning() = IverksettelseAvVedtak(
        iverksattAv = iverksattAv,
    )
}