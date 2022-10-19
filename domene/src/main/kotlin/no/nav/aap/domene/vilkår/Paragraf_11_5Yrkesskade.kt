package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_5Yrkesskade.AvventerManuellVurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5Yrkesskade
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5Yrkesskade.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_5Yrkesskade
import no.nav.aap.hendelse.LøsningParagraf_11_5Yrkesskade.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_5Yrkesskade
import no.nav.aap.modellapi.Paragraf_11_5YrkesskadeModellApi
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_5Yrkesskade private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_5Yrkesskade>
) :
    Vilkårsvurdering<Paragraf_11_5Yrkesskade>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_5_YRKESSKADE,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_5Yrkesskade>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_5Yrkesskade>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_5Yrkesskade.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_5Yrkesskade>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerManuellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5Yrkesskade): VilkårsvurderingModellApi =
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_5Yrkesskade>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_5Yrkesskade, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_5Yrkesskade())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            løsning: LøsningParagraf_11_5Yrkesskade
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erNedsattMedMinst30Prosent()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5Yrkesskade): VilkårsvurderingModellApi =
            Paragraf_11_5YrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_5_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            modellApi: Paragraf_11_5YrkesskadeModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_5Yrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5Yrkesskade): VilkårsvurderingModellApi =
            Paragraf_11_5YrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_5_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            modellApi: Paragraf_11_5YrkesskadeModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_5Yrkesskade>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5Yrkesskade): VilkårsvurderingModellApi =
            Paragraf_11_5YrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_5_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            modellApi: Paragraf_11_5YrkesskadeModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltAvventerKvalitetssikring : Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_5Yrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5Yrkesskade): VilkårsvurderingModellApi =
            Paragraf_11_5YrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_5_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            modellApi: Paragraf_11_5YrkesskadeModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_5Yrkesskade>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5Yrkesskade): VilkårsvurderingModellApi =
            Paragraf_11_5YrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_5_yrkesskade_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_5_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_5Yrkesskade,
            modellApi: Paragraf_11_5YrkesskadeModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: Paragraf_11_5YrkesskadeModellApi) {
        løsninger.addAll(vilkårsvurdering.løsning_11_5_yrkesskade_manuell.map {
            LøsningParagraf_11_5Yrkesskade(
                løsningId = it.løsningId,
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent
            )

        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: Paragraf_11_5YrkesskadeModellApi) {
        kvalitetssikringer.addAll(vilkårsvurdering.kvalitetssikringer_11_5_yrkesskade.map {
            KvalitetssikringParagraf_11_5Yrkesskade(
                kvalitetssikringId = it.kvalitetssikringId,
                løsningId = it.løsningId,
                kvalitetssikretAv = it.kvalitetssikretAv,
                tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                erGodkjent = it.erGodkjent,
                begrunnelse = it.begrunnelse
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_5Yrkesskade(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_5_yrkesskade")
        }
    }
}
