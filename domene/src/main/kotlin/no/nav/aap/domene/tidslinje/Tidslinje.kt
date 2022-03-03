package no.nav.aap.domene.tidslinje

import no.nav.aap.domene.Barnehage
import no.nav.aap.domene.tidslinje.Tidsperiode.Companion.summerDagsatser
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.tidslinje.Meldekort.Dag.Companion.tilTidsperiodedager
import no.nav.aap.hendelse.Hendelse
import java.time.LocalDate

internal class Tidslinje {

    private val tidsperioder: MutableList<Tidsperiode> = mutableListOf()

    internal fun håndterMeldekort(meldekort: Meldekort, grunnlag: Inntektsgrunnlag) {
        //Finn eller opprett Tidsperiode?

        tidsperioder.add(meldekort.opprettTidsperiode(grunnlag))

        //Håndter meldekort i Tidsperiode
    }

    internal fun summerTidslinje() = tidsperioder.summerDagsatser()

}

class Meldekort(
    private val dager: List<Dag>,
    private val barnehage: Barnehage,
    private val institusjonsopphold: List<Institusjonsopphold>
) : Hendelse() {
    class Dag(
        private val dato: LocalDate
    ) {
        internal companion object {
            internal fun Iterable<Dag>.tilTidsperiodedager(
                grunnlag: Inntektsgrunnlag,
                barnehage: Barnehage
            ): List<no.nav.aap.domene.tidslinje.Dag> {
                return map { Dag(it.dato, grunnlag.grunnlagForDag(it.dato), barnehage.barnetilleggForDag(it.dato)) }
            }
        }
    }

    class Institusjonsopphold(
        //Kan godt være begrenset av meldekortets varighet
        private val fom: LocalDate,
        private val tom: LocalDate,
        private val type: Type
    ) {
        enum class Type {
            OPPHOLD_I_INSTITUSJON,
            STRAFFEGJENNOMFØRING
        }

    }

    internal fun opprettTidsperiode(grunnlag: Inntektsgrunnlag): Tidsperiode {
        val tidsperiodedager = dager.tilTidsperiodedager(grunnlag, barnehage)
        return Tidsperiode(tidsperiodedager)
    }
}
