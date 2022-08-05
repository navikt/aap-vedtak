package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.hendelse.LøsningParagraf_11_4AndreOgTredjeLedd.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_4AndreOgTredjeLedd
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_4AndreOgTredjeLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_4AndreOgTredjeLedd>
) :
    Vilkårsvurdering<Paragraf_11_4AndreOgTredjeLedd>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_4,
        Ledd.LEDD_2 + Ledd.LEDD_3,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_4AndreOgTredjeLedd>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_4AndreOgTredjeLedd.() -> T) = this.block()

    private fun vurderAlder(hendelse: Hendelse, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        if (fødselsdato.erUnder62(vurderingsdato)) {
            tilstand(IkkeRelevant, hendelse)
        } else {
            tilstand(SøknadMottatt, hendelse)
        }
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.vurderAlder(søknad, fødselsdato, vurderingsdato)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_4AndreOgTredjeLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            løsning: LøsningParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            løsning_11_4_ledd2_ledd3_manuell = null
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto()
        )
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto()
        )
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_RELEVANT,
            løsning_11_4_ledd2_ledd3_manuell = null
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_4AndreOgTredjeLedd(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT -> IkkeOppfylt
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_4AndreOgTredjeLedd")
        }
    }
}
