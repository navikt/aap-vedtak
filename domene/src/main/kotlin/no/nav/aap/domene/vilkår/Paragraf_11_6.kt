package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_6.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_6
import no.nav.aap.hendelse.LøsningParagraf_11_6.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_6
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_6 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_6>
) :
    Vilkårsvurdering<Paragraf_11_6>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_6,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_6>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_6.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_6>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_6,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_6>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_6, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_6())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_6,
            løsning: LøsningParagraf_11_6
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_6>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_6): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_6, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_6_manuell)
            vilkårsvurdering.løsninger.addAll(løsning.map {
                LøsningParagraf_11_6(
                    vurdertAv = it.vurdertAv,
                    harBehovForBehandling = it.harBehovForBehandling,
                    harBehovForTiltak = it.harBehovForTiltak,
                    harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid
                )
            })
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_6>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_6): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_6, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_6_manuell)
            vilkårsvurdering.løsninger.addAll(løsning.map {
                LøsningParagraf_11_6(
                    vurdertAv = it.vurdertAv,
                    harBehovForBehandling = it.harBehovForBehandling,
                    harBehovForTiltak = it.harBehovForTiltak,
                    harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid
                )
            })
        }
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_6(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_6")
        }

    }
}
