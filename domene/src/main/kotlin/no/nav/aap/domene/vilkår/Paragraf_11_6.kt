package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_6.AvventerInnstilling
import no.nav.aap.domene.vilkår.Paragraf_11_6.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_6
import no.nav.aap.hendelse.LøsningParagraf_11_6
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_6
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6.Companion.toDto
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_6 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_6>,
    innstillinger: List<InnstillingParagraf_11_6>,
    totrinnskontroller: List<Totrinnskontroll<LøsningParagraf_11_6, KvalitetssikringParagraf_11_6>>,
) :
    Vilkårsvurdering<Paragraf_11_6>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_6,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val innstillinger = innstillinger.toMutableList()
    private val totrinnskontroller = totrinnskontroller.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, listOf(), listOf())

    override fun <T> callWithReceiver(block: Paragraf_11_6.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_6>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_6,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerInnstilling, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerInnstilling : Tilstand.AvventerInnstilling<Paragraf_11_6>() {

        override fun håndterInnstilling(
            vilkårsvurdering: Paragraf_11_6,
            løsning: InnstillingParagraf_11_6
        ) {
            vilkårsvurdering.innstillinger.add(løsning)
            vilkårsvurdering.tilstand(AvventerManuellVurdering, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): Paragraf_11_6ModellApi =
            vilkårsvurdering.toParagraf_11_6ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_6>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_6, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_6())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_6,
            løsning: LøsningParagraf_11_6
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): Paragraf_11_6ModellApi =
            vilkårsvurdering.toParagraf_11_6ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_6>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_6,
            kvalitetssikring: KvalitetssikringParagraf_11_6
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): Paragraf_11_6ModellApi =
            vilkårsvurdering.toParagraf_11_6ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_6>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_6): Paragraf_11_6ModellApi =
            vilkårsvurdering.toParagraf_11_6ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltAvventerKvalitetssikring : Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_6>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_6,
            kvalitetssikring: KvalitetssikringParagraf_11_6
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): Paragraf_11_6ModellApi =
            vilkårsvurdering.toParagraf_11_6ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_6>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_6): Paragraf_11_6ModellApi =
            vilkårsvurdering.toParagraf_11_6ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_11_6ModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_11_6ModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        innstillinger_11_6 = innstillinger.toDto(),
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_11_6::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_11_6::toDto
        ),
    )

    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            innstillinger: List<InnstillingParagraf_11_6ModellApi>,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_6ModellApi, KvalitetssikringParagraf_11_6ModellApi>>
        ) =
            Paragraf_11_6(
                vilkårsvurderingsid = vilkårsvurderingsid,
                tilstand = tilknyttetTilstand(tilstandsnavn),
                innstillinger = gjenopprettInnstillinger(innstillinger),
                totrinnskontroller = gjenopprettTotrinnskontroller(totrinnskontroller)
            )

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_INNSTILLING -> AvventerInnstilling
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_6")
        }

        private fun gjenopprettInnstillinger(innstillinger: List<InnstillingParagraf_11_6ModellApi>) =
            innstillinger.map(InnstillingParagraf_11_6ModellApi::toInnstilling)

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_6ModellApi, KvalitetssikringParagraf_11_6ModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_11_6ModellApi::toLøsning,
                KvalitetssikringParagraf_11_6ModellApi::toKvalitetssikring
            )
    }
}
