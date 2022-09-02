package no.nav.aap.dto

import no.nav.aap.domene.Søker
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.LøsningSykepengedager
import java.time.LocalDate

data class SykepengedagerModellApi(
    val gjenståendeSykedager: Int,
    val foreløpigBeregnetSluttPåSykepenger: LocalDate,
    val kilde: String,
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningSykepengedager(
        personident = Personident("slette?"),
        gjenståendeSykedager = gjenståendeSykedager,
        foreløpigBeregnetSluttPåSykepenger = foreløpigBeregnetSluttPåSykepenger,
        kilde = LøsningSykepengedager.Kilde.valueOf(kilde)
    )
}