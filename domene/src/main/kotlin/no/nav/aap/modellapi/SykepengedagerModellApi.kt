package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.Behov.Companion.toDto
import no.nav.aap.hendelse.DtoBehov
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


    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<DtoBehov>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    internal fun toLøsning(): LøsningSykepengedager {
        return if (sykepengedager == null) {
            LøsningSykepengedager(sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke())
        } else {
            LøsningSykepengedager(
                sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                    gjenståendeSykedager = sykepengedager.gjenståendeSykedager,
                    foreløpigBeregnetSluttPåSykepenger = sykepengedager.foreløpigBeregnetSluttPåSykepenger,
                    kilde = LøsningSykepengedager.Kilde.valueOf(sykepengedager.kilde)
                )
            )
        }
    }
}
