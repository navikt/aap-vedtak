package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.Behov
import no.nav.aap.hendelse.LøsningSykepengedager
import java.time.LocalDate

data class SykepengedagerModellApi(
    val sykepengedager: Sykepengedager?
) {
    data class Sykepengedager(
        val gjenståendeSykedager: Int,
        val foreløpigBeregnetSluttPåSykepenger: LocalDate,
        val kilde: String,
    )

    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    internal fun toLøsning(): LøsningSykepengedager {
        return if (sykepengedager == null) {
            LøsningSykepengedager(sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke())
        } else {
            LøsningSykepengedager(sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = sykepengedager.gjenståendeSykedager,
                foreløpigBeregnetSluttPåSykepenger = sykepengedager.foreløpigBeregnetSluttPåSykepenger,
                kilde = LøsningSykepengedager.Kilde.valueOf(sykepengedager.kilde)
            ))
        }
    }
}