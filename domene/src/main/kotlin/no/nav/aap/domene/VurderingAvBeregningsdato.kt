package no.nav.aap.domene

import no.nav.aap.dto.DtoVurderingAvBeregningsdato
import no.nav.aap.hendelse.LøsningVurderingAvBeregningsdato
import no.nav.aap.hendelse.LøsningVurderingAvBeregningsdato.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.BehovVurderingAvBeregningsdato
import java.time.LocalDate

internal class VurderingAvBeregningsdato private constructor(
    private var tilstand: Tilstand
) {
    private val løsninger = mutableListOf<LøsningVurderingAvBeregningsdato>()

    internal constructor() : this(Tilstand.Start)

    internal fun håndterSøknad(søknad: Søknad) {
        tilstand.håndterSøknad(this, søknad)
    }

    internal fun håndterLøsning(løsning: LøsningVurderingAvBeregningsdato) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun erFerdig() = tilstand.erFerdig()

    internal fun beregningsdato() = tilstand.beregningsdato(this)

    internal sealed class Tilstand(protected val navn: Tilstandsnavn) {
        internal enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            START({ Start }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            FERDIG({ Ferdig })
        }

        internal open fun håndterSøknad(vurderingAvBeregningsdato: VurderingAvBeregningsdato, søknad: Søknad) {}
        internal open fun håndterLøsning(
            vurderingAvBeregningsdato: VurderingAvBeregningsdato,
            løsning: LøsningVurderingAvBeregningsdato
        ) {
        }

        internal open fun erFerdig() = false

        internal open fun beregningsdato(vurderingAvBeregningsdato: VurderingAvBeregningsdato): LocalDate =
            error("Kan ikke hente beregningsdato uten løsning")

        internal object Start : Tilstand(Tilstandsnavn.START) {
            override fun håndterSøknad(vurderingAvBeregningsdato: VurderingAvBeregningsdato, søknad: Søknad) {
                søknad.opprettBehov(BehovVurderingAvBeregningsdato())
                vurderingAvBeregningsdato.tilstand = SøknadMottatt
            }
        }

        internal object SøknadMottatt : Tilstand(Tilstandsnavn.SØKNAD_MOTTATT) {
            override fun håndterLøsning(
                vurderingAvBeregningsdato: VurderingAvBeregningsdato,
                løsning: LøsningVurderingAvBeregningsdato
            ) {
                vurderingAvBeregningsdato.løsninger.add(løsning)
                vurderingAvBeregningsdato.tilstand = Ferdig
            }
        }

        internal object Ferdig : Tilstand(Tilstandsnavn.FERDIG) {
            override fun beregningsdato(vurderingAvBeregningsdato: VurderingAvBeregningsdato): LocalDate =
                vurderingAvBeregningsdato.løsninger.last().beregningsdato

            override fun erFerdig() = true

            override fun toDto(vurderingAvBeregningsdato: VurderingAvBeregningsdato) = DtoVurderingAvBeregningsdato(
                tilstand = navn.name,
                løsningVurderingAvBeregningsdato = vurderingAvBeregningsdato.løsninger.toDto()
            )

            override fun gjenopprettTilstand(
                vurderingAvBeregningsdato: VurderingAvBeregningsdato,
                dtoVurderingAvBeregningsdato: DtoVurderingAvBeregningsdato
            ) {
                val dtoLøsningVurderingAvBeregningsdato =
                    requireNotNull(dtoVurderingAvBeregningsdato.løsningVurderingAvBeregningsdato)
                vurderingAvBeregningsdato.løsninger.addAll(dtoLøsningVurderingAvBeregningsdato.map {
                    LøsningVurderingAvBeregningsdato.gjenopprett(it)
                }
                )
            }
        }

        internal open fun toDto(vurderingAvBeregningsdato: VurderingAvBeregningsdato) = DtoVurderingAvBeregningsdato(
            tilstand = navn.name,
            løsningVurderingAvBeregningsdato = null
        )

        internal open fun gjenopprettTilstand(
            vurderingAvBeregningsdato: VurderingAvBeregningsdato,
            dtoVurderingAvBeregningsdato: DtoVurderingAvBeregningsdato
        ) {
        }
    }

    internal fun toDto() = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(dtoVurderingAvBeregningsdato: DtoVurderingAvBeregningsdato): VurderingAvBeregningsdato =
            enumValueOf<Tilstand.Tilstandsnavn>(dtoVurderingAvBeregningsdato.tilstand)
                .tilknyttetTilstand()
                .let(::VurderingAvBeregningsdato)
                .apply { this.tilstand.gjenopprettTilstand(this, dtoVurderingAvBeregningsdato) }
    }
}
