package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_3.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_3
import no.nav.aap.hendelse.LøsningParagraf_11_3
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_3
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_3 private constructor(
    vilkårsvurderingsid: UUID, tilstand: Tilstand<Paragraf_11_3>
) : Vilkårsvurdering<Paragraf_11_3>(
    vilkårsvurderingsid, Paragraf.PARAGRAF_11_3, Ledd.LEDD_1 + Ledd.LEDD_2 + Ledd.LEDD_3, tilstand
) {
    private val totrinnskontroller =
        mutableListOf<Totrinnskontroll<LøsningParagraf_11_3, KvalitetssikringParagraf_11_3>>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_3.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_3>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_3, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerManuellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_3>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_3())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_3, løsning: LøsningParagraf_11_3
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): VilkårsvurderingModellApi = Paragraf_11_3ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_11_3::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_3::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_3,
            modellApi: Paragraf_11_3ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_3>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_3,
            kvalitetssikring: KvalitetssikringParagraf_11_3
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): VilkårsvurderingModellApi = Paragraf_11_3ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_11_3::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_3::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_3,
            modellApi: Paragraf_11_3ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_3>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_3): VilkårsvurderingModellApi = Paragraf_11_3ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_11_3::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_3::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_3,
            modellApi: Paragraf_11_3ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object IkkeOppfyltAvventerKvalitetssikring : Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_3>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_3,
            kvalitetssikring: KvalitetssikringParagraf_11_3
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): VilkårsvurderingModellApi = Paragraf_11_3ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_11_3::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_3::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_3,
            modellApi: Paragraf_11_3ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_3>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_3): VilkårsvurderingModellApi = Paragraf_11_3ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_11_3::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_11_3::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_3,
            modellApi: Paragraf_11_3ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private fun gjenopprettTotrinnskontroller(modellApi: Paragraf_11_3ModellApi) {
        totrinnskontroller.addAll(
            modellApi.totrinnskontroller.gjenopprett(
                LøsningParagraf_11_3ModellApi::toLøsning,
                KvalitetssikringParagraf_11_3ModellApi::toKvalitetssikring,
            )
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_3(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_3")
        }
    }
}
