package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.MedlemskapYrkesskade.*
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.LøsningMaskinellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_11_2
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class MedlemskapYrkesskade private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>,
    maskinelleLøsninger: List<LøsningMaskinellMedlemskapYrkesskade>,
    totrinnskontroller: List<Totrinnskontroll<LøsningManuellMedlemskapYrkesskade, KvalitetssikringMedlemskapYrkesskade>>
) :
    Vilkårsvurdering<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>(
        vilkårsvurderingsid,
        Paragraf.MEDLEMSKAP_YRKESSKADE,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val maskinelleLøsninger = maskinelleLøsninger.toMutableList()
    private val totrinnskontroller = totrinnskontroller.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList(), emptyList())

    override fun <T> callWithReceiver(block: MedlemskapYrkesskade.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: MedlemskapYrkesskade,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerMaskinellVurdering :
        Tilstand.AvventerMaskinellVurdering<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
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

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object AvventerManuellVurdering :
        Tilstand.AvventerManuellVurdering<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningManuellMedlemskapYrkesskade
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManueltAvventerKvalitetssikring, løsning)
                else -> vilkårsvurdering.tilstand(IkkeOppfyltManueltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltMaskineltKvalitetssikret :
        Tilstand.OppfyltMaskineltKvalitetssikret<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltMaskineltKvalitetssikret :
        Tilstand.IkkeOppfyltMaskineltKvalitetssikret<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltManueltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    OppfyltManueltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltManueltKvalitetssikret :
        Tilstand.OppfyltManueltKvalitetssikret<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltManueltAvventerKvalitetssikring :
        Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    IkkeOppfyltManueltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltManueltKvalitetssikret :
        Tilstand.IkkeOppfyltManueltKvalitetssikret<MedlemskapYrkesskade, MedlemskapYrkesskadeModellApi>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): MedlemskapYrkesskadeModellApi =
            vilkårsvurdering.toMedlemskapYrkesskadeModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toMedlemskapYrkesskadeModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ): MedlemskapYrkesskadeModellApi =
        MedlemskapYrkesskadeModellApi(
            vilkårsvurderingsid = vilkårsvurderingsid,
            paragraf = paragraf.name,
            ledd = ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = utfall,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_medlemskap_yrkesskade_maskinell = maskinelleLøsninger.toDto(),
            totrinnskontroller = totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellMedlemskapYrkesskade::toDto,
                toKvalitetssikringDto = KvalitetssikringMedlemskapYrkesskade::toDto,
            ),
        )

    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            maskinelleLøsninger: List<LøsningMaskinellMedlemskapYrkesskadeModellApi>,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningManuellMedlemskapYrkesskadeModellApi, KvalitetssikringMedlemskapYrkesskadeModellApi>>
        ) = MedlemskapYrkesskade(
            vilkårsvurderingsid = vilkårsvurderingsid,
            tilstand = tilknyttetTilstand(tilstandsnavn),
            maskinelleLøsninger = gjenopprettMaskinelleLøsninger(maskinelleLøsninger),
            totrinnskontroller = gjenopprettTotrinnskontroller(totrinnskontroller),
        )

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

        private fun gjenopprettMaskinelleLøsninger(maskinelleLøsninger: List<LøsningMaskinellMedlemskapYrkesskadeModellApi>) =
            maskinelleLøsninger.map(LøsningMaskinellMedlemskapYrkesskadeModellApi::toLøsning)

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningManuellMedlemskapYrkesskadeModellApi, KvalitetssikringMedlemskapYrkesskadeModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningManuellMedlemskapYrkesskadeModellApi::toLøsning,
                KvalitetssikringMedlemskapYrkesskadeModellApi::toKvalitetssikring,
            )
    }
}
