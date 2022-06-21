package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_6
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_6
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_6")

internal class Paragraf_11_6 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand
) :
    Vilkårsvurdering<Paragraf_11_6, Paragraf_11_6.Tilstand>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_6,
        Ledd.LEDD_1,
        tilstand
    ) {
    private lateinit var løsning: LøsningParagraf_11_6

    internal constructor() : this(UUID.randomUUID(), Tilstand.IkkeVurdert)

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_6) {
        tilstand.vurderManueltOppfyltLøsning(this, løsning)
    }

    override fun onEntry(hendelse: Hendelse) {
        tilstand.onEntry(this, hendelse)
    }

    override fun onExit(hendelse: Hendelse) {
        tilstand.onExit(this, hendelse)
    }

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) : Vilkårsvurderingstilstand<Paragraf_11_6> {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            OPPFYLT({ Oppfylt }),
            IKKE_OPPFYLT({ IkkeOppfylt }),
        }

        override fun erOppfylt() = erOppfylt
        override fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_6,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun vurderManueltOppfyltLøsning(
            vilkårsvurdering: Paragraf_11_6,
            løsning: LøsningParagraf_11_6
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_6,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }

            override fun toDto(paragraf: Paragraf_11_6): DtoVilkårsvurdering =
                ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }

        object SøknadMottatt : Tilstand(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_6, hendelse: Hendelse) {
                hendelse.opprettBehov(Behov_11_6())
            }

            override fun vurderManueltOppfyltLøsning(
                vilkårsvurdering: Paragraf_11_6,
                løsning: LøsningParagraf_11_6
            ) {
                vilkårsvurdering.løsning = løsning
                if (løsning.erManueltOppfylt()) {
                    vilkårsvurdering.tilstand(Oppfylt, løsning)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
                }
            }

            override fun toDto(paragraf: Paragraf_11_6): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = null,
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT
            )
        }

        object Oppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_6): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = paragraf.løsning.vurdertAv(),
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                løsning_11_6_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_6, vilkårsvurdering: DtoVilkårsvurdering) {
                val vurdertAv = requireNotNull(vilkårsvurdering.vurdertAv)
                val løsning = requireNotNull(vilkårsvurdering.løsning_11_6_manuell)
                paragraf.løsning = LøsningParagraf_11_6(
                    vurdertAv = vurdertAv,
                    harBehovForBehandling = løsning.harBehovForBehandling,
                    harBehovForTiltak = løsning.harBehovForTiltak,
                    harMulighetForÅKommeIArbeid = løsning.harMulighetForÅKommeIArbeid
                )
            }
        }

        object IkkeOppfylt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_6): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = paragraf.løsning.vurdertAv(),
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                løsning_11_6_manuell = paragraf.løsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_6, vilkårsvurdering: DtoVilkårsvurdering) {
                val vurdertAv = requireNotNull(vilkårsvurdering.vurdertAv)
                val løsning = requireNotNull(vilkårsvurdering.løsning_11_6_manuell)
                paragraf.løsning = LøsningParagraf_11_6(
                    vurdertAv = vurdertAv,
                    harBehovForBehandling = løsning.harBehovForBehandling,
                    harBehovForTiltak = løsning.harBehovForTiltak,
                    harMulighetForÅKommeIArbeid = løsning.harMulighetForÅKommeIArbeid
                )
            }
        }
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_6 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let { tilstand -> Paragraf_11_6(vilkårsvurdering.vilkårsvurderingsid, tilstand) }
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}
