package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.*
import java.time.LocalDate

internal abstract class Vilkårsvurdering(
    protected val paragraf: Paragraf,
    protected val ledd: List<Ledd>
) {
    internal constructor(
        paragraf: Paragraf,
        ledd: Ledd
    ) : this(paragraf, listOf(ledd))

    internal enum class Paragraf {
        PARAGRAF_11_2,
        PARAGRAF_11_3,
        PARAGRAF_11_4,
        PARAGRAF_11_5,
        PARAGRAF_11_6,
        PARAGRAF_11_12,
        PARAGRAF_11_29
    }

    internal enum class Ledd {
        LEDD_1,
        LEDD_2,
        LEDD_3;

        operator fun plus(other: Ledd) = listOf(this, other)
    }

    internal abstract fun erOppfylt(): Boolean
    internal abstract fun erIkkeOppfylt(): Boolean

    internal open fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_2) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_3) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_5) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_6) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_29) {}

    private fun toFrontendVilkårsvurdering() =
        FrontendVilkårsvurdering(
            paragraf = paragraf.name,
            ledd = ledd.map(Ledd::name),
            tilstand = toFrontendTilstand(),
            harÅpenOppgave = toFrontendHarÅpenOppgave()
        )

    protected abstract fun toFrontendTilstand(): String
    protected open fun toFrontendHarÅpenOppgave(): Boolean = false
    protected open fun toDto(): DtoVilkårsvurdering = DtoVilkårsvurdering(
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = toFrontendTilstand(), // todo:
        null,
        null,
    )

    internal companion object {
        internal fun Iterable<Vilkårsvurdering>.erAlleOppfylt() = all(Vilkårsvurdering::erOppfylt)
        internal fun Iterable<Vilkårsvurdering>.erNoenIkkeOppfylt() = any(Vilkårsvurdering::erIkkeOppfylt)
        internal fun Iterable<Vilkårsvurdering>.toDto() = map(Vilkårsvurdering::toDto)
        internal fun Iterable<Vilkårsvurdering>.toFrontendVilkårsvurdering() =
            map(Vilkårsvurdering::toFrontendVilkårsvurdering)

        internal fun create(vilkårsvurdering: DtoVilkårsvurdering): Vilkårsvurdering? =
            when (Paragraf.valueOf(vilkårsvurdering.paragraf)) {
                Paragraf.PARAGRAF_11_2 -> Paragraf_11_2.create(vilkårsvurdering)
                else -> null.also { println("Paragraf ${vilkårsvurdering.paragraf} not implemented") }
            }
    }
}
