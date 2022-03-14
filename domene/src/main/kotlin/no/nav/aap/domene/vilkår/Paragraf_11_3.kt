package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_3
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_3
import org.slf4j.LoggerFactory
import java.time.LocalDate
private val log = LoggerFactory.getLogger("Paragraf_11_3")

internal class Paragraf_11_3 private constructor(private var tilstand: Tilstand) :
    Vilkårsvurdering(Paragraf.PARAGRAF_11_3, Ledd.LEDD_1 + Ledd.LEDD_2 + Ledd.LEDD_3) {
    private lateinit var løsning: LøsningParagraf_11_3

    internal constructor() : this(Tilstand.IkkeVurdert)

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_3) {
        tilstand.håndterLøsning(this, løsning)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            OPPFYLT({ Oppfylt }),
            IKKE_OPPFYLT({ IkkeOppfylt }),
        }

        internal open fun onEntry(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_3,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_3,
            løsning: LøsningParagraf_11_3
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_3,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }
        }

        object SøknadMottatt : Tilstand(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {
                hendelse.opprettBehov(Behov_11_3())
            }

            override fun håndterLøsning(
                vilkårsvurdering: Paragraf_11_3,
                løsning: LøsningParagraf_11_3
            ) {
                vilkårsvurdering.løsning = løsning
                if (løsning.erManueltOppfylt()) {
                    vilkårsvurdering.tilstand(Oppfylt, løsning)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
                }
            }
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_3_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_3, vilkårsvurdering: DtoVilkårsvurdering) {
                val løsning113Manuell = requireNotNull(vilkårsvurdering.løsning_11_3_manuell)
                paragraf.løsning = LøsningParagraf_11_3(løsning113Manuell.erOppfylt)
            }
        }

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_3_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_3, vilkårsvurdering: DtoVilkårsvurdering) {
                val løsning113Manuell = requireNotNull(vilkårsvurdering.løsning_11_3_manuell)
                paragraf.løsning = LøsningParagraf_11_3(løsning113Manuell.erOppfylt)
            }
        }

        internal open fun gjenopprettTilstand(paragraf: Paragraf_11_3, vilkårsvurdering: DtoVilkårsvurdering) {}

        internal open fun toDto(paragraf: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
            paragraf = paragraf.paragraf.name,
            ledd = paragraf.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name
        )
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_3 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let(::Paragraf_11_3)
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}
