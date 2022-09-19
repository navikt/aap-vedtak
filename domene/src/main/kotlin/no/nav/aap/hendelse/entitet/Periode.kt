package no.nav.aap.hendelse.entitet

import java.time.LocalDate

internal class Periode(
    private val fom: LocalDate,
    private val tom: LocalDate
) {
    internal fun fom() = fom
    internal fun tom() = tom
}
