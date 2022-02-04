package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import java.time.LocalDate

internal abstract class Vilkårsvurdering(
    private val paragraf: Paragraf,
    private val ledd: List<Ledd>
) {
    internal constructor(
        paragraf: Paragraf,
        ledd: Ledd
    ) : this(paragraf, listOf(ledd))

    internal enum class Paragraf {
        PARAGRAF_11_2, PARAGRAF_11_4, PARAGRAF_11_5
    }

    internal enum class Ledd {
        LEDD_1, LEDD_2;

        operator fun plus(other: Ledd) = listOf(this, other)
    }

    internal abstract fun erOppfylt(): Boolean
    internal abstract fun erIkkeOppfylt(): Boolean

    internal open fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_2) {}
    internal open fun håndterLøsning(løsning: LøsningParagraf_11_5) {}

    private fun toFrontendVilkårsvurdering() =
        FrontendVilkårsvurdering(
            paragraf = paragraf.name,
            ledd = ledd.map(Ledd::name),
            tilstand = toFrontendTilstand(),
            harÅpenOppgave = toFrontendHarÅpenOppgave()
        )

    protected abstract fun toFrontendTilstand(): String
    protected open fun toFrontendHarÅpenOppgave(): Boolean = false

    internal companion object {
        internal fun Iterable<Vilkårsvurdering>.erAlleOppfylt() = all(Vilkårsvurdering::erOppfylt)
        internal fun Iterable<Vilkårsvurdering>.erNoenIkkeOppfylt() = any(Vilkårsvurdering::erIkkeOppfylt)

        internal fun Iterable<Vilkårsvurdering>.toFrontendVilkårsvurdering() =
            map(Vilkårsvurdering::toFrontendVilkårsvurdering)
    }
}
