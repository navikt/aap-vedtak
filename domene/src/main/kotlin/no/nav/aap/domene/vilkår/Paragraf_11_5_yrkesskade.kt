package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_5_yrkesskade.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_5_yrkesskade
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_5_yrkesskade
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_5_yrkesskade")

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
    private lateinit var løsning: LøsningParagraf_11_5_yrkesskade

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
            løsning: LøsningParagraf_11_5_yrkesskade
        ) {
            vilkårsvurdering.løsning = løsning
            if (løsning.erNedsattMedMinst30Prosent()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_5_yrkesskade>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsning.toDto()
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5_yrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            val vurdertAv = requireNotNull(dtoVilkårsvurdering.vurdertAv)
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_5_yrkesskade_manuell)
            vilkårsvurdering.løsning = LøsningParagraf_11_5_yrkesskade(
                vurdertAv = vurdertAv,
                arbeidsevneErNedsattMedMinst50Prosent = løsning.arbeidsevneErNedsattMedMinst50Prosent,
                arbeidsevneErNedsattMedMinst30Prosent = løsning.arbeidsevneErNedsattMedMinst30Prosent
            )
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_5_yrkesskade>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5_yrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsning.toDto()
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5_yrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            val vurdertAv = requireNotNull(dtoVilkårsvurdering.vurdertAv)
            val løsning = requireNotNull(dtoVilkårsvurdering.løsning_11_5_yrkesskade_manuell)
            vilkårsvurdering.løsning = LøsningParagraf_11_5_yrkesskade(
                vurdertAv = vurdertAv,
                arbeidsevneErNedsattMedMinst50Prosent = løsning.arbeidsevneErNedsattMedMinst50Prosent,
                arbeidsevneErNedsattMedMinst30Prosent = løsning.arbeidsevneErNedsattMedMinst30Prosent
            )
        }
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_5_yrkesskade =
            Paragraf_11_5_yrkesskade(
                vilkårsvurdering.vilkårsvurderingsid,
                tilknyttetTilstand(enumValueOf(vilkårsvurdering.tilstand))
            )
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_5_yrkesskade")
        }
    }
}
