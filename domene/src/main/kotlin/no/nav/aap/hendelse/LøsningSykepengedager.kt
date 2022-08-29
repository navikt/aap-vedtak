package no.nav.aap.hendelse

import no.nav.aap.domene.entitet.Personident
import java.time.LocalDate

internal class LøsningSykepengedager(
    private val personident: Personident,
    private val gjenståendeSykedager: Int,
    private val foreløpigBeregnetSluttPåSykepenger: LocalDate,
    private val kilde: Kilde,
) : Hendelse() {
    enum class Kilde {
        SPLEIS, INFOTRYGD,
    }

    internal fun gjenståendeSykedager() = gjenståendeSykedager
}
