package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_19.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_19
import no.nav.aap.hendelse.LøsningParagraf_11_19
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_19
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_19 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_19, Paragraf_11_19ModellApi>,
    totrinnskontroll: List<Totrinnskontroll<LøsningParagraf_11_19, KvalitetssikringParagraf_11_19>>,
) :
    Vilkårsvurdering<Paragraf_11_19, Paragraf_11_19ModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_19,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val totrinnskontroller = totrinnskontroll.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList())

    override fun <T> callWithReceiver(block: Paragraf_11_19.() -> T) = this.block()

    private object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_19, Paragraf_11_19ModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_19,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate,
        ) {
            søknad.opprettBehov(Behov_11_19())
            vilkårsvurdering.tilstand(AvventerManuellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_19): Paragraf_11_19ModellApi {
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }
    }

    internal object AvventerManuellVurdering :
        Tilstand.AvventerManuellVurdering<Paragraf_11_19, Paragraf_11_19ModellApi>() {
        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_19,
            løsning: LøsningParagraf_11_19
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_19): Paragraf_11_19ModellApi =
            vilkårsvurdering.toParagraf_11_19ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    internal object OppfyltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_19, Paragraf_11_19ModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_19,
            kvalitetssikring: KvalitetssikringParagraf_11_19
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_11_19, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_19(vilkårsvurdering)
            visitor.preVisitGjeldendeTotrinnskontroll_11_19(vilkårsvurdering.totrinnskontroller.last())
            vilkårsvurdering.totrinnskontroller.last().accept(visitor)
            visitor.postVisitGjeldendeTotrinnskontroll_11_19(vilkårsvurdering.totrinnskontroller.last())
            visitor.postVisitParagraf_11_19(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_19): Paragraf_11_19ModellApi =
            vilkårsvurdering.toParagraf_11_19ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    internal object OppfyltKvalitetssikret :
        Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_19, Paragraf_11_19ModellApi>() {
        override fun accept(vilkårsvurdering: Paragraf_11_19, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_19(vilkårsvurdering)
            visitor.preVisitGjeldendeTotrinnskontroll_11_19(vilkårsvurdering.totrinnskontroller.last())
            vilkårsvurdering.totrinnskontroller.last().accept(visitor)
            visitor.postVisitGjeldendeTotrinnskontroll_11_19(vilkårsvurdering.totrinnskontroller.last())
            visitor.postVisitParagraf_11_19(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_19): Paragraf_11_19ModellApi =
            vilkårsvurdering.toParagraf_11_19ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_11_19ModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_11_19ModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_11_19::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_11_19::toDto,
        ),
    )

    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_19ModellApi, KvalitetssikringParagraf_11_19ModellApi>>
        ) = Paragraf_11_19(
            vilkårsvurderingsid,
            tilknyttetTilstand(tilstandsnavn),
            gjenopprettTotrinnskontroller(totrinnskontroller)
        )

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_19")
        }

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_19ModellApi, KvalitetssikringParagraf_11_19ModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_11_19ModellApi::toLøsning,
                KvalitetssikringParagraf_11_19ModellApi::toKvalitetssikring,
            )
    }
}
