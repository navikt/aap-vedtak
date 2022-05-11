package no.nav.aap.domene.beregning

import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.beløpJustertFor6G
import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.finnGrunnlagsfaktor
import no.nav.aap.domene.beregning.Grunnbeløp.Element.Companion.justerInntekt
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import java.time.LocalDate
import java.time.Year

internal object Grunnbeløp {
    private val grunnbeløp = listOf(
        Element(LocalDate.of(2021, 5, 1), Beløp(106399.0), Beløp(104716.0)),
        Element(LocalDate.of(2020, 5, 1), Beløp(101351.0), Beløp(100853.0)),
        Element(LocalDate.of(2019, 5, 1), Beløp(99858.0), Beløp(98866.0)),
        Element(LocalDate.of(2018, 5, 1), Beløp(96883.0), Beløp(95800.0)),
        Element(LocalDate.of(2017, 5, 1), Beløp(93634.0), Beløp(93281.0)),
        Element(LocalDate.of(2016, 5, 1), Beløp(92576.0), Beløp(91740.0)),
        Element(LocalDate.of(2015, 5, 1), Beløp(90068.0), Beløp(89502.0)),
        Element(LocalDate.of(2014, 5, 1), Beløp(88370.0), Beløp(87328.0)),
        Element(LocalDate.of(2013, 5, 1), Beløp(85245.0), Beløp(84204.0)),
        Element(LocalDate.of(2012, 5, 1), Beløp(82122.0), Beløp(81153.0)),
        Element(LocalDate.of(2011, 5, 1), Beløp(79216.0), Beløp(78024.0)),
        Element(LocalDate.of(2010, 5, 1), Beløp(75641.0), Beløp(74721.0)),
        Element(LocalDate.of(2009, 5, 1), Beløp(72881.0), Beløp(72006.0)),
        Element(LocalDate.of(2008, 5, 1), Beløp(70256.0), Beløp(69108.0)),
        Element(LocalDate.of(2007, 5, 1), Beløp(66812.0), Beløp(65505.0)),
        Element(LocalDate.of(2006, 5, 1), Beløp(62892.0), Beløp(62161.0)),
        Element(LocalDate.of(2005, 5, 1), Beløp(60699.0), Beløp(60059.0)),
        Element(LocalDate.of(2004, 5, 1), Beløp(58778.0), Beløp(58139.0)),
        Element(LocalDate.of(2003, 5, 1), Beløp(56861.0), Beløp(55964.0)),
        Element(LocalDate.of(2002, 5, 1), Beløp(54170.0), Beløp(53233.0)),
        Element(LocalDate.of(2001, 5, 1), Beløp(51360.0), Beløp(50603.0)),
        Element(LocalDate.of(2000, 5, 1), Beløp(49090.0), Beløp(48377.0)),
        Element(LocalDate.of(1999, 5, 1), Beløp(46950.0), Beløp(46423.0)),
        Element(LocalDate.of(1998, 5, 1), Beløp(45370.0), Beløp(44413.0)),
        Element(LocalDate.of(1997, 5, 1), Beløp(42500.0), Beløp(42000.0)),
        Element(LocalDate.of(1996, 5, 1), Beløp(41000.0), Beløp(40410.0)),
        Element(LocalDate.of(1995, 5, 1), Beløp(39230.0), Beløp(38847.0))
    )

    private class Element(
        private val dato: LocalDate,
        private val beløp: Beløp,
        private val gjennomsnittBeløp: Beløp
    ) {
        companion object {
            fun Iterable<Element>.justerInntekt(beregningsdato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) =
                grunnlagsfaktor * finnGrunnbeløpForDato(beregningsdato).beløp

            private fun Iterable<Element>.finnGrunnbeløpForDato(dato: LocalDate) = this
                .sortedByDescending { it.dato }
                .first { dato >= it.dato }

            private fun Iterable<Element>.finnGrunnbeløpForÅr(år: Year) = this
                .sortedByDescending { it.dato }
                .first { år >= Year.from(it.dato) }

            fun Iterable<Element>.beløpJustertFor6G(år: Year, beløpFørJustering: Beløp): Beløp {
                val grunnbeløpForInntektsår = finnGrunnbeløpForÅr(år)
                return minOf(beløpFørJustering, grunnbeløpForInntektsår.gjennomsnittBeløp * 6)
            }

            fun Iterable<Element>.finnGrunnlagsfaktor(år: Year, beløp: Beløp): Grunnlagsfaktor {
                return Grunnlagsfaktor(beløp / finnGrunnbeløpForÅr(år).gjennomsnittBeløp)
            }
        }
    }

    internal fun beløpJustertFor6G(år: Year, beløpFørJustering: Beløp) =
        grunnbeløp.beløpJustertFor6G(år, beløpFørJustering)

    internal fun justerInntekt(beregningsdato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) =
        grunnbeløp.justerInntekt(beregningsdato, grunnlagsfaktor)

    internal fun finnGrunnlagsfaktor(år: Year, beløp: Beløp) =
        grunnbeløp.finnGrunnlagsfaktor(år, beløp)
}
