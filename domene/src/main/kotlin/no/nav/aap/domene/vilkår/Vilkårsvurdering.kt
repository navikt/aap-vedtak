package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.frontendView.FrontendVilkårsvurdering
import no.nav.aap.hendelse.*
import org.slf4j.LoggerFactory
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
        PARAGRAF_11_14,
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
    protected abstract fun toDto(): DtoVilkårsvurdering

    internal companion object {
        private val log = LoggerFactory.getLogger("Vilkårsvurdering")

        internal fun Iterable<Vilkårsvurdering>.erAlleOppfylt() = all(Vilkårsvurdering::erOppfylt)
        internal fun Iterable<Vilkårsvurdering>.erNoenIkkeOppfylt() = any(Vilkårsvurdering::erIkkeOppfylt)
        internal fun Iterable<Vilkårsvurdering>.toDto() = map(Vilkårsvurdering::toDto)
        internal fun Iterable<Vilkårsvurdering>.toFrontendVilkårsvurdering() =
            map(Vilkårsvurdering::toFrontendVilkårsvurdering)

        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Vilkårsvurdering? =
            when (enumValueOf<Paragraf>(vilkårsvurdering.paragraf)) {
                Paragraf.PARAGRAF_11_2 -> Paragraf_11_2.gjenopprett(vilkårsvurdering)
                Paragraf.PARAGRAF_11_3 -> Paragraf_11_3.gjenopprett(vilkårsvurdering)
                Paragraf.PARAGRAF_11_4 -> {
                    vilkårsvurdering.ledd.map { enumValueOf<Ledd>(it) }.let { ledd ->
                        when (ledd) {
                            listOf(Ledd.LEDD_1) -> Paragraf_11_4FørsteLedd.gjenopprett(vilkårsvurdering)
                            listOf(Ledd.LEDD_2, Ledd.LEDD_3) -> Paragraf_11_4AndreOgTredjeLedd.gjenopprett(vilkårsvurdering)
                            else -> null.also { log.warn("Paragraf ${vilkårsvurdering.paragraf} Ledd $ledd not implemented") }
                        }
                    }
                }
                Paragraf.PARAGRAF_11_5 -> Paragraf_11_5.gjenopprett(vilkårsvurdering)
                Paragraf.PARAGRAF_11_6 -> Paragraf_11_6.gjenopprett(vilkårsvurdering)
                Paragraf.PARAGRAF_11_12 -> {
                    vilkårsvurdering.ledd.map { enumValueOf<Ledd>(it) }.let { ledd ->
                        when (ledd) {
                            listOf(Ledd.LEDD_1) -> Paragraf_11_12FørsteLedd.gjenopprett(vilkårsvurdering)
                            else -> null.also { log.warn("Paragraf ${vilkårsvurdering.paragraf} Ledd $ledd not implemented") }
                        }
                    }
                }
                Paragraf.PARAGRAF_11_14 -> Paragraf_11_14.gjenopprett(vilkårsvurdering)
                Paragraf.PARAGRAF_11_29 -> Paragraf_11_29.gjenopprett(vilkårsvurdering)
            }
    }
}
