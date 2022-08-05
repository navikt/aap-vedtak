package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_5_yrkesskade.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_5Yrkesskade
import no.nav.aap.hendelse.LøsningParagraf_11_5Yrkesskade.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_5_yrkesskade
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_5_yrkesskade private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_5_yrkesskade>
) :
    Vilkårsvurdering<Paragraf_11_5_yrkesskade>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_5_YRKESSKADE,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_5Yrkesskade>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_5_yrkesskade.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_5_yrkesskade>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5_yrkesskade,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering =
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_5_yrkesskade>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_5_yrkesskade, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_5_yrkesskade())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_5_yrkesskade,
            løsning: LøsningParagraf_11_5Yrkesskade
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erNedsattMedMinst30Prosent()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_5_yrkesskade>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto()
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5_yrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_5_yrkesskade_manuell)
            vilkårsvurdering.løsninger.addAll(løsning.map {
                LøsningParagraf_11_5Yrkesskade(
                    vurdertAv = it.vurdertAv,
                    tidspunktForVurdering = it.tidspunktForVurdering,
                    arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                    arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent
                )
            })
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_5_yrkesskade>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto()
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5_yrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_5_yrkesskade_manuell)
            vilkårsvurdering.løsninger.addAll(løsning.map {
                LøsningParagraf_11_5Yrkesskade(
                    vurdertAv = it.vurdertAv,
                    tidspunktForVurdering = it.tidspunktForVurdering,
                    arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                    arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent
                )
            })
        }
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_5_yrkesskade(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_5_yrkesskade")
        }
    }
}
