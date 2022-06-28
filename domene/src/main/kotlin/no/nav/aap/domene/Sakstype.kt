package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.*
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erAlleOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erNoenIkkeOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.DtoSakstype
import no.nav.aap.hendelse.*
import java.time.LocalDate
import java.util.*

internal abstract class Sakstype private constructor(
    protected val type: Type,
    private var aktiv: Boolean,
    protected val vilkårsvurderinger: List<Vilkårsvurdering<*>>
) {

    internal enum class Type {
        STANDARD,
        YRKESSKADE,
        STUDENT
    }

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        vilkårsvurderinger.forEach { it.håndterSøknad(søknad, fødselsdato, vurderingsdato) }
    }

    internal fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningMaskinellParagraf_11_2) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningManuellParagraf_11_2) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_3) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5_yrkesskade) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal open fun håndterLøsning(løsning: LøsningParagraf_11_22) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal open fun håndterLøsning(løsning: LøsningParagraf_11_19) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    abstract fun opprettVedtak(
        inntektshistorikk: Inntektshistorikk,
        beregningsdato: LocalDate,
        fødselsdato: Fødselsdato
    ): Vedtak

    internal fun erAlleOppfylt() = vilkårsvurderinger.erAlleOppfylt()
    internal fun erNoenIkkeOppfylt() = vilkårsvurderinger.erNoenIkkeOppfylt()

    internal fun beregningsdato() = vilkårsvurderinger.firstNotNullOf { it.beregningsdato() }

    internal class Standard private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering<*>>
    ) : Sakstype(
        type = Type.STANDARD,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {

        override fun opprettVedtak(
            inntektshistorikk: Inntektshistorikk,
            beregningsdato: LocalDate,
            fødselsdato: Fødselsdato
        ): Vedtak {
            val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(beregningsdato, fødselsdato, null)
            return Vedtak(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        }

        internal companion object {
            internal fun opprettStandard(): Standard {
                val vilkårsvurderinger = listOf(
                    Paragraf_11_2(),
                    Paragraf_11_3(),
                    Paragraf_11_4FørsteLedd(),
                    Paragraf_11_4AndreOgTredjeLedd(),
                    Paragraf_11_5(),
                    Paragraf_11_6(),
                    Paragraf_11_12FørsteLedd(),
                    Paragraf_11_19(),
                    Paragraf_11_29(),
                )

                return Standard(vilkårsvurderinger)
            }

            internal fun gjenopprettStandard(vilkårsvurderinger: List<Vilkårsvurdering<*>>) =
                Standard(vilkårsvurderinger)
        }
    }

    internal class Yrkesskade private constructor(
        private val paragraf1122: Paragraf_11_22,
        vilkårsvurderinger: List<Vilkårsvurdering<*>>
    ) : Sakstype(
        type = Type.YRKESSKADE,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {

        override fun opprettVedtak(
            inntektshistorikk: Inntektshistorikk,
            beregningsdato: LocalDate,
            fødselsdato: Fødselsdato
        ): Vedtak {
            val inntektsgrunnlag =
                inntektshistorikk.finnInntektsgrunnlag(beregningsdato, fødselsdato, paragraf1122.yrkesskade())
            return Vedtak(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        }

        internal companion object {
            internal fun opprettYrkesskade(): Yrkesskade {
                val paragraf1122 = Paragraf_11_22()
                val vilkårsvurderinger = listOf(
                    MedlemskapYrkesskade(),
                    Paragraf_11_3(),
                    Paragraf_11_4FørsteLedd(),
                    Paragraf_11_4AndreOgTredjeLedd(),
                    Paragraf_11_5_yrkesskade(),
                    Paragraf_11_6(),
                    Paragraf_11_12FørsteLedd(),
                    paragraf1122,
                    Paragraf_11_19(),
                    Paragraf_11_29(),
                )

                return Yrkesskade(paragraf1122, vilkårsvurderinger)
            }

            internal fun gjenopprettYrkesskade(vilkårsvurderinger: List<Vilkårsvurdering<*>>) =
                Yrkesskade(vilkårsvurderinger.filterIsInstance<Paragraf_11_22>().first(), vilkårsvurderinger)
        }
    }

    internal class Student private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering<*>>
    ) : Sakstype(
        type = Type.STUDENT,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {

        override fun opprettVedtak(
            inntektshistorikk: Inntektshistorikk,
            beregningsdato: LocalDate,
            fødselsdato: Fødselsdato
        ): Vedtak {
            val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(beregningsdato, fødselsdato, null)
            return Vedtak(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        }

        internal companion object {
            internal fun opprettStudent(): Student {
                val vilkårsvurderinger = listOf(
                    Paragraf_11_14(),
                    Paragraf_11_19(),
                )
                return Student(vilkårsvurderinger)
            }

            internal fun gjenopprettStudent(vilkårsvurderinger: List<Vilkårsvurdering<*>>) =
                Student(vilkårsvurderinger)
        }
    }

    private fun toDto() = DtoSakstype(
        type = type.name,
        aktiv = aktiv,
        vilkårsvurderinger = vilkårsvurderinger.toDto()
    )

    internal companion object {
        internal fun Iterable<Sakstype>.toDto() = map(Sakstype::toDto)

        internal fun gjenopprett(dtoSakstype: DtoSakstype): Sakstype {
            val vilkårsvurderinger =
                dtoSakstype.vilkårsvurderinger.mapNotNull(Vilkårsvurdering.Companion::gjenopprett).toMutableList()
            return when (enumValueOf<Type>(dtoSakstype.type)) {
                Type.STANDARD -> Standard.gjenopprettStandard(vilkårsvurderinger)
                Type.YRKESSKADE -> Yrkesskade.gjenopprettYrkesskade(vilkårsvurderinger)
                Type.STUDENT -> Student.gjenopprettStudent(vilkårsvurderinger)
            }
        }
    }
}
