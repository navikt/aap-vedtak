package no.nav.aap.domene

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.*
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erAlleOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erNoenIkkeOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.DtoSakstype
import no.nav.aap.hendelse.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

internal abstract class Sakstype private constructor(
    protected val type: Type,
    private var aktiv: Boolean,
    private val vilkårsvurderinger: List<Vilkårsvurdering>
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

    internal fun håndterLøsning(løsning: LøsningParagraf_11_2) {
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

    internal fun håndterLøsning(løsning: LøsningParagraf_11_22) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) {
        vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
    }

    internal fun erAlleOppfylt() = vilkårsvurderinger.erAlleOppfylt()
    internal fun erNoenIkkeOppfylt() = vilkårsvurderinger.erNoenIkkeOppfylt()

    internal class Standard private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering>
    ) : Sakstype(
        type = Type.STANDARD,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {
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
                    Paragraf_11_29()
                )

                return Standard(vilkårsvurderinger)
            }

            internal fun gjenopprettStandard(vilkårsvurderinger: List<Vilkårsvurdering>) = Standard(vilkårsvurderinger)
        }
    }

    internal class Yrkesskade private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering>
    ) : Sakstype(
        type = Type.YRKESSKADE,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {
        internal companion object {
            internal fun opprettYrkesskade(): Yrkesskade {
                val vilkårsvurderinger = listOf(
                    MedlemskapYrkesskade(),
                    Paragraf_11_3(),
                    Paragraf_11_4FørsteLedd(),
                    Paragraf_11_4AndreOgTredjeLedd(),
                    Paragraf_11_5_yrkesskade(),
                    Paragraf_11_6(),
                    Paragraf_11_12FørsteLedd(),
                    Paragraf_11_22(),
                    Paragraf_11_29()
                )

                return Yrkesskade(vilkårsvurderinger)
            }

            internal fun gjenopprettStandard(vilkårsvurderinger: List<Vilkårsvurdering>) =
                Yrkesskade(vilkårsvurderinger)
        }
    }

    internal class Student private constructor(
        vilkårsvurderinger: List<Vilkårsvurdering>
    ) : Sakstype(
        type = Type.STUDENT,
        aktiv = true,
        vilkårsvurderinger = vilkårsvurderinger
    ) {
        internal companion object {
            internal fun opprettStudent(): Student {
                val vilkårsvurderinger = listOf(Paragraf_11_14())
                return Student(vilkårsvurderinger)
            }

            internal fun gjenopprettStudent(vilkårsvurderinger: List<Vilkårsvurdering>) = Student(vilkårsvurderinger)
        }
    }

    private fun toDto() = DtoSakstype(
        type = type.name,
        aktiv = aktiv,
        vilkårsvurderinger = vilkårsvurderinger.toDto()
    )

    internal companion object {
        private val log = LoggerFactory.getLogger("sakstype")

        internal fun Iterable<Sakstype>.toDto() = map(Sakstype::toDto)

        internal fun gjenopprett(dtoSakstype: DtoSakstype): Sakstype {
            val vilkårsvurderinger =
                dtoSakstype.vilkårsvurderinger.mapNotNull(Vilkårsvurdering::gjenopprett).toMutableList()
            return when (enumValueOf<Type>(dtoSakstype.type)) {
                Type.STANDARD -> Standard.gjenopprettStandard(vilkårsvurderinger)
                Type.YRKESSKADE -> Yrkesskade.gjenopprettStandard(vilkårsvurderinger)
                Type.STUDENT -> Student.gjenopprettStudent(vilkårsvurderinger)
            }
        }
    }
}
