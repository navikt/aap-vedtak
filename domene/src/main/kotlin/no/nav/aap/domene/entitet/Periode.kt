package no.nav.aap.domene.entitet

import java.time.LocalDate

internal class Periode private constructor(
    private val fom: LocalDate,
    private val tom: LocalDate
) {
    internal fun fom() = fom
    internal fun tom() = tom

    internal companion object {
        internal infix fun LocalDate.til(tom: LocalDate) = Periode(this, tom)
    }
}
