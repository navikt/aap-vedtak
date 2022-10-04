package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.hendelse.LøsningSykepengedager
import no.nav.aap.hendelse.behov.Behov.Companion.toDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SykepengedagerModellApi(
    val løsningId: UUID,
    val tidspunktForVurdering: LocalDateTime,
    val sykepengedager: Sykepengedager?
) {
    constructor(
        sykepengedager: Sykepengedager?
    ) : this(
        løsningId = UUID.randomUUID(),
        tidspunktForVurdering = LocalDateTime.now(),
        sykepengedager = sykepengedager
    )

    data class Sykepengedager(
        val gjenståendeSykedager: Int,
        val foreløpigBeregnetSluttPåSykepenger: LocalDate,
        val kilde: String,
    )

    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>> {
        val modellSøker = Søker.gjenopprett(søker)
        val løsning = toLøsning()
        modellSøker.håndterLøsning(løsning)
        return modellSøker.toDto() to løsning.behov().toDto(søker.personident)
    }

    internal fun toLøsning(): LøsningSykepengedager {
        return if (sykepengedager == null) {
            LøsningSykepengedager(
                løsningId = løsningId,
                tidspunktForVurdering = tidspunktForVurdering,
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        } else {
            LøsningSykepengedager(
                løsningId = løsningId,
                tidspunktForVurdering = tidspunktForVurdering,
                sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                    gjenståendeSykedager = sykepengedager.gjenståendeSykedager,
                    foreløpigBeregnetSluttPåSykepenger = sykepengedager.foreløpigBeregnetSluttPåSykepenger,
                    kilde = LøsningSykepengedager.Kilde.valueOf(sykepengedager.kilde)
                )
            )
        }
    }
}
