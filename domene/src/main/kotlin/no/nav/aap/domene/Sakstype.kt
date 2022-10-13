package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.*
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.domene.visitor.BeregningsdatoVisitor
import no.nav.aap.domene.visitor.SakstypeVisitor
import no.nav.aap.domene.visitor.YrkesskadeVisitor
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.SakstypeModellApi
import no.nav.aap.modellapi.VilkårsvurderingModellApi
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

    internal fun <T> håndter(løsning: T, håndter: Vilkårsvurdering<*>.(T) -> Unit) {
        vilkårsvurderinger.forEach { it.håndter(løsning) }
    }

    internal abstract fun accept(visitor: SakstypeVisitor)

    abstract fun opprettVedtak(
        inntektshistorikk: Inntektshistorikk,
        fødselsdato: Fødselsdato,
        virkningsdato: LocalDate,
    ): Vedtak

    internal class Standard private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering<*>>
    ) : Sakstype(
        type = Type.STANDARD,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {

        override fun accept(visitor: SakstypeVisitor) {
            visitor.preVisitStandard(this)
            vilkårsvurderinger.forEach { it.accept(visitor) }
            visitor.postVisitStandard(this)
        }

        override fun opprettVedtak(
            inntektshistorikk: Inntektshistorikk,
            fødselsdato: Fødselsdato,
            virkningsdato: LocalDate,
        ): Vedtak {
            val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(
                beregningsdato = BeregningsdatoVisitor().apply(::accept).beregningsdato,
                fødselsdato = fødselsdato,
                yrkesskade = null
            )
            return Vedtak(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                vedtaksdato = LocalDate.now(),
                virkningsdato = virkningsdato,
            )
        }

        internal companion object {
            internal fun opprettStandard(): Standard {
                val vilkårsvurderinger = listOf(
                    Paragraf_8_48(),
                    Paragraf_11_2(),
                    Paragraf_11_3(),
                    Paragraf_11_4FørsteLedd(),
                    Paragraf_11_4AndreOgTredjeLedd(),
                    Paragraf_11_5(),
                    Paragraf_11_6(),
                    Paragraf_11_19(),
                    Paragraf_11_27FørsteLedd(),
                    // TODO: Implementere 11-29 ordentlig
//                    Paragraf_11_29(),
                    Paragraf_22_13(),
                )

                return Standard(vilkårsvurderinger)
            }

            internal fun gjenopprettStandard(vilkårsvurderinger: List<Vilkårsvurdering<*>>) =
                Standard(vilkårsvurderinger)
        }
    }

    internal class Yrkesskade private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering<*>>
    ) : Sakstype(
        type = Type.YRKESSKADE,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {

        override fun accept(visitor: SakstypeVisitor) {
            visitor.preVisitYrkesskade(this)
            vilkårsvurderinger.forEach { it.accept(visitor) }
            visitor.postVisitYrkesskade(this)
        }

        override fun opprettVedtak(
            inntektshistorikk: Inntektshistorikk,
            fødselsdato: Fødselsdato,
            virkningsdato: LocalDate,
        ): Vedtak {
            val yrkesskade = YrkesskadeVisitor().apply(::accept).yrkesskade
            val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(
                beregningsdato = BeregningsdatoVisitor().apply(::accept).beregningsdato,
                fødselsdato = fødselsdato,
                yrkesskade = yrkesskade
            )
            return Vedtak(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                vedtaksdato = LocalDate.now(),
                virkningsdato = virkningsdato
            )
        }

        internal companion object {
            internal fun opprettYrkesskade(): Yrkesskade {
                val vilkårsvurderinger = listOf(
                    MedlemskapYrkesskade(),
                    Paragraf_11_3(),
                    Paragraf_11_4FørsteLedd(),
                    Paragraf_11_4AndreOgTredjeLedd(),
                    Paragraf_11_5Yrkesskade(),
                    Paragraf_11_6(),
                    Paragraf_22_13(),
                    Paragraf_11_22(),
                    Paragraf_11_19(),
                    Paragraf_11_29(),
                )

                return Yrkesskade(vilkårsvurderinger)
            }

            internal fun gjenopprettYrkesskade(vilkårsvurderinger: List<Vilkårsvurdering<*>>) =
                Yrkesskade(vilkårsvurderinger)
        }
    }

    internal class Student private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering<*>>
    ) : Sakstype(
        type = Type.STUDENT,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {

        override fun accept(visitor: SakstypeVisitor) {
            visitor.preVisitStudent(this)
            vilkårsvurderinger.forEach { it.accept(visitor) }
            visitor.postVisitStudent(this)
        }

        override fun opprettVedtak(
            inntektshistorikk: Inntektshistorikk,
            fødselsdato: Fødselsdato,
            virkningsdato: LocalDate,
        ): Vedtak {
            val inntektsgrunnlag = inntektshistorikk.finnInntektsgrunnlag(
                beregningsdato = BeregningsdatoVisitor().apply(::accept).beregningsdato,
                fødselsdato = fødselsdato,
                yrkesskade = null
            )
            return Vedtak(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                inntektsgrunnlag = inntektsgrunnlag,
                vedtaksdato = LocalDate.now(),
                virkningsdato = virkningsdato
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

    private fun toDto() = SakstypeModellApi(
        type = type.name,
        aktiv = aktiv,
        vilkårsvurderinger = vilkårsvurderinger.toDto()
    )

    internal companion object {
        internal fun Iterable<Sakstype>.toDto() = map(Sakstype::toDto)

        internal fun gjenopprett(sakstypeModellApi: SakstypeModellApi): Sakstype {
            val vilkårsvurderinger =
                sakstypeModellApi.vilkårsvurderinger.map(VilkårsvurderingModellApi::gjenopprett).toMutableList()
            return when (enumValueOf<Type>(sakstypeModellApi.type)) {
                Type.STANDARD -> Standard.gjenopprettStandard(vilkårsvurderinger)
                Type.YRKESSKADE -> Yrkesskade.gjenopprettYrkesskade(vilkårsvurderinger)
                Type.STUDENT -> Student.gjenopprettStudent(vilkårsvurderinger)
            }
        }
    }
}
