package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_2.*
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.LøsningMaskinellParagraf_11_2.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_11_2
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_2 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_2>
) :
    Vilkårsvurdering<Paragraf_11_2>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_2,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val maskinelleLøsninger = mutableListOf<LøsningMaskinellParagraf_11_2>()
    private val totrinnskontroller =
        mutableListOf<Totrinnskontroll<LøsningManuellParagraf_11_2, KvalitetssikringParagraf_11_2>>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_2.() -> T): T = this.block()

    private object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_2>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2): VilkårsvurderingModellApi {
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }
    }

    private object AvventerMaskinellVurdering : Tilstand.AvventerMaskinellVurdering<Paragraf_11_2>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
            //send ut behov for innhenting av maskinell medlemskapsvurdering
            hendelse.opprettBehov(Behov_11_2())
        }

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_2, løsning: LøsningMaskinellParagraf_11_2) {
            vilkårsvurdering.maskinelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltMaskineltKvalitetssikret, løsning)
                løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltMaskineltKvalitetssikret, løsning)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )
    }

    private object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_2>() {
        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_2, løsning: LøsningManuellParagraf_11_2) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManueltAvventerKvalitetssikring, løsning)
                else -> vilkårsvurdering.tilstand(
                    IkkeOppfyltManueltAvventerKvalitetssikring,
                    løsning
                )
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private object OppfyltMaskineltKvalitetssikret : Tilstand.OppfyltMaskineltKvalitetssikret<Paragraf_11_2>() {

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private object IkkeOppfyltMaskineltKvalitetssikret : Tilstand.IkkeOppfyltMaskineltKvalitetssikret<Paragraf_11_2>() {

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private object OppfyltManueltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_2>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_2,
            kvalitetssikring: KvalitetssikringParagraf_11_2
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

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private object OppfyltManueltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private object IkkeOppfyltManueltAvventerKvalitetssikring :
        Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_2>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_2,
            kvalitetssikring: KvalitetssikringParagraf_11_2
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

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private object IkkeOppfyltManueltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = Paragraf_11_2ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningManuellParagraf_11_2::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_2::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_2,
            modellApi: Paragraf_11_2ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private fun settMaskinellLøsning(vilkårsvurdering: Paragraf_11_2ModellApi) {
        maskinelleLøsninger.addAll(vilkårsvurdering.løsning_11_2_maskinell.map {
            LøsningMaskinellParagraf_11_2(
                løsningId = it.løsningId,
                tidspunktForVurdering = it.tidspunktForVurdering,
                erMedlem = enumValueOf(it.erMedlem)
            )
        })
    }

    private fun gjenopprettTotrinnskontroller(modellApi: Paragraf_11_2ModellApi){
        totrinnskontroller.addAll(
            modellApi.totrinnskontroller.gjenopprett(
                LøsningParagraf_11_2ModellApi::toLøsning,
                KvalitetssikringParagraf_11_2ModellApi::toKvalitetssikring,
            )
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_2(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

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
