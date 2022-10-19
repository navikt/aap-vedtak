package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.MedlemskapYrkesskade.*
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.KvalitetssikringMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.LøsningManuellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.LøsningMaskinellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_11_2
import no.nav.aap.modellapi.MedlemskapYrkesskadeModellApi
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import java.time.LocalDate
import java.util.*

internal class MedlemskapYrkesskade private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<MedlemskapYrkesskade>
) :
    Vilkårsvurdering<MedlemskapYrkesskade>(
        vilkårsvurderingsid,
        Paragraf.MEDLEMSKAP_YRKESSKADE,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val maskinelleLøsninger = mutableListOf<LøsningMaskinellMedlemskapYrkesskade>()
    private val manuelleLøsninger = mutableListOf<LøsningManuellMedlemskapYrkesskade>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringMedlemskapYrkesskade>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: MedlemskapYrkesskade.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<MedlemskapYrkesskade>() {
        override fun håndterSøknad(
            vilkårsvurdering: MedlemskapYrkesskade,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerMaskinellVurdering : Tilstand.AvventerMaskinellVurdering<MedlemskapYrkesskade>() {
        override fun onEntry(vilkårsvurdering: MedlemskapYrkesskade, hendelse: Hendelse) {
            //send ut behov for innhenting av maskinell medlemskapsvurdering
            hendelse.opprettBehov(Behov_11_2())
        }

        override fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningMaskinellMedlemskapYrkesskade
        ) {
            vilkårsvurdering.maskinelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltMaskineltKvalitetssikret, løsning)
                løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltMaskineltKvalitetssikret, løsning)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<MedlemskapYrkesskade>() {
        override fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningManuellMedlemskapYrkesskade
        ) {
            vilkårsvurdering.manuelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManueltAvventerKvalitetssikring, løsning)
                else -> vilkårsvurdering.tilstand(IkkeOppfyltManueltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltMaskineltKvalitetssikret : Tilstand.OppfyltMaskineltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = "maskinell saksbehandling",
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltMaskineltKvalitetssikret : Tilstand.IkkeOppfyltMaskineltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = "maskinell saksbehandling",
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltManueltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<MedlemskapYrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    OppfyltManueltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltManueltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltManueltAvventerKvalitetssikring : Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<MedlemskapYrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    IkkeOppfyltManueltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltManueltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): VilkårsvurderingModellApi =
            MedlemskapYrkesskadeModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
                løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
                kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            modellApi: MedlemskapYrkesskadeModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    private fun settMaskinellLøsning(vilkårsvurdering: MedlemskapYrkesskadeModellApi) {
        maskinelleLøsninger.addAll(vilkårsvurdering.løsning_medlemskap_yrkesskade_maskinell.map {
            LøsningMaskinellMedlemskapYrkesskade(
                it.løsningId,
                enumValueOf(it.erMedlem)
            )
        })
    }

    private fun settManuellLøsning(vilkårsvurdering: MedlemskapYrkesskadeModellApi) {
        manuelleLøsninger.addAll(vilkårsvurdering.løsning_medlemskap_yrkesskade_manuell.map {
            LøsningManuellMedlemskapYrkesskade(
                it.løsningId,
                it.vurdertAv,
                it.tidspunktForVurdering,
                enumValueOf(it.erMedlem)
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: MedlemskapYrkesskadeModellApi) {
        kvalitetssikringer.addAll(vilkårsvurdering.kvalitetssikringer_medlemskap_yrkesskade.map {
            KvalitetssikringMedlemskapYrkesskade(
                it.kvalitetssikringId,
                it.løsningId,
                it.kvalitetssikretAv,
                it.tidspunktForKvalitetssikring,
                it.erGodkjent,
                it.begrunnelse
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            MedlemskapYrkesskade(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MASKINELL_VURDERING -> AvventerMaskinellVurdering
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET -> OppfyltMaskineltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET -> IkkeOppfyltMaskineltKvalitetssikret
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltManueltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltManueltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltManueltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltManueltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_2")
        }
    }
}
