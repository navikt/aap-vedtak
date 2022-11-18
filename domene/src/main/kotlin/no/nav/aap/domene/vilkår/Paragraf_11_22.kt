package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_22.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_22
import no.nav.aap.hendelse.LøsningParagraf_11_22
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_22
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_22 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_22, Paragraf_11_22ModellApi>,
    totrinnskontroller: List<Totrinnskontroll<LøsningParagraf_11_22, KvalitetssikringParagraf_11_22>>,
) :
    Vilkårsvurdering<Paragraf_11_22, Paragraf_11_22ModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_22,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val totrinnskontroller = totrinnskontroller.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList())

    override fun <T> callWithReceiver(block: Paragraf_11_22.() -> T) = this.block()

    override fun lagSnapshot(vedtak: Vedtak) {
        totrinnskontroller.lastOrNull()?.let(vedtak::leggTilTotrinnskontroll)
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_22, Paragraf_11_22ModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_22,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerManuellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): Paragraf_11_22ModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_22, Paragraf_11_22ModellApi>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_22, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_22())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_22,
            løsning: LøsningParagraf_11_22
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))

            if (løsning.erOppfylt()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): Paragraf_11_22ModellApi =
            vilkårsvurdering.toParagraf_11_22ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_22, Paragraf_11_22ModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_22,
            kvalitetssikring: KvalitetssikringParagraf_11_22
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_11_22, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_22(vilkårsvurdering)
            visitor.preVisitGjeldendeTotrinnskontroll_11_22(vilkårsvurdering.totrinnskontroller.last())
            vilkårsvurdering.totrinnskontroller.last().accept(visitor)
            visitor.postVisitGjeldendeTotrinnskontroll_11_22(vilkårsvurdering.totrinnskontroller.last())
            visitor.postVisitParagraf_11_22(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): Paragraf_11_22ModellApi =
            vilkårsvurdering.toParagraf_11_22ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_22, Paragraf_11_22ModellApi>() {
        override fun accept(vilkårsvurdering: Paragraf_11_22, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_22(vilkårsvurdering)
            visitor.preVisitGjeldendeTotrinnskontroll_11_22(vilkårsvurdering.totrinnskontroller.last())
            vilkårsvurdering.totrinnskontroller.last().accept(visitor)
            visitor.postVisitGjeldendeTotrinnskontroll_11_22(vilkårsvurdering.totrinnskontroller.last())
            visitor.postVisitParagraf_11_22(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): Paragraf_11_22ModellApi =
            vilkårsvurdering.toParagraf_11_22ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltAvventerKvalitetssikring :
        Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_22, Paragraf_11_22ModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_22,
            kvalitetssikring: KvalitetssikringParagraf_11_22
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): Paragraf_11_22ModellApi =
            vilkårsvurdering.toParagraf_11_22ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltKvalitetssikret :
        Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_22, Paragraf_11_22ModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_22): Paragraf_11_22ModellApi =
            vilkårsvurdering.toParagraf_11_22ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_11_22ModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_11_22ModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_11_22::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_11_22::toDto,
        ),
    )


    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_22ModellApi, KvalitetssikringParagraf_11_22ModellApi>>
        ) = Paragraf_11_22(
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
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_22")
        }

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_22ModellApi, KvalitetssikringParagraf_11_22ModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_11_22ModellApi::toLøsning,
                KvalitetssikringParagraf_11_22ModellApi::toKvalitetssikring,
            )
    }
}
