package no.nav.aap.domene

import no.nav.aap.domene.Barnehage.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.entitet.Fødselsdato
import java.time.LocalDate

internal class Barnehage(
    private val barn: List<Barn>
) {

    private companion object {
        private val BARNETILLEGG = 27.beløp
    }

    internal fun barnetilleggForDag(dato: LocalDate) = BARNETILLEGG * barn.antallBarnUnder18År(dato)

    internal class Barn(
        private val fødselsdato: Fødselsdato
    ) {
        internal companion object {
            internal fun Iterable<Barn>.antallBarnUnder18År(dato: LocalDate) =
                count { it.fødselsdato.erUnder18År(dato) }
        }
    }
}
