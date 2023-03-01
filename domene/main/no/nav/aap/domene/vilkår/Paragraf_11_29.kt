package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_29.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_29
import no.nav.aap.hendelse.LøsningParagraf_11_29
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_29
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_29 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_29, Paragraf_11_29ModellApi>,
    totrinnskontroller: List<Totrinnskontroll<LøsningParagraf_11_29, KvalitetssikringParagraf_11_29>>,
) :
    Vilkårsvurdering<Paragraf_11_29, Paragraf_11_29ModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_29,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val totrinnskontroller = totrinnskontroller.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList())

    override fun <T> callWithReceiver(block: Paragraf_11_29.() -> T) = this.block()

    override fun lagSnapshot(vedtak: Vedtak) {
        totrinnskontroller.lastOrNull()?.let(vedtak::leggTilTotrinnskontroll)
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_29, Paragraf_11_29ModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_29,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerManuellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_29): Paragraf_11_29ModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_29, Paragraf_11_29ModellApi>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_29, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_29())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_29,
            løsning: LøsningParagraf_11_29
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_29): Paragraf_11_29ModellApi =
            vilkårsvurdering.toParagraf_11_29ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_29, Paragraf_11_29ModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_29,
            kvalitetssikring: KvalitetssikringParagraf_11_29
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_29): Paragraf_11_29ModellApi =
            vilkårsvurdering.toParagraf_11_29ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_29, Paragraf_11_29ModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_29): Paragraf_11_29ModellApi =
            vilkårsvurdering.toParagraf_11_29ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltAvventerKvalitetssikring :
        Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_29, Paragraf_11_29ModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_29,
            kvalitetssikring: KvalitetssikringParagraf_11_29
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_29): Paragraf_11_29ModellApi =
            vilkårsvurdering.toParagraf_11_29ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltKvalitetssikret :
        Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_29, Paragraf_11_29ModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_29): Paragraf_11_29ModellApi =
            vilkårsvurdering.toParagraf_11_29ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_11_29ModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_11_29ModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_11_29::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_11_29::toDto,
        ),
    )


    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_29ModellApi, KvalitetssikringParagraf_11_29ModellApi>>
        ) = Paragraf_11_29(
            vilkårsvurderingsid = vilkårsvurderingsid,
            tilstand = tilknyttetTilstand(tilstandsnavn),
            totrinnskontroller = gjenopprettTotrinnskontroller(totrinnskontroller)
        )

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_29")
        }

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_29ModellApi, KvalitetssikringParagraf_11_29ModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_11_29ModellApi::toLøsning,
                KvalitetssikringParagraf_11_29ModellApi::toKvalitetssikring,
            )
    }
}
