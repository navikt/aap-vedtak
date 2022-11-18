package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_27FørsteLedd.*
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_11_27
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_27FørsteLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>,
    løsninger: List<LøsningParagraf_11_27_FørsteLedd>,
    totrinnskontroll: List<Totrinnskontroll<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13>>,
) :
    Vilkårsvurdering<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_27,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsninger = løsninger.toMutableList()
    private val totrinnskontroller = totrinnskontroll.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList(), emptyList())

    override fun <T> callWithReceiver(block: Paragraf_11_27FørsteLedd.() -> T) = this.block()

    override fun lagSnapshot(vedtak: Vedtak) {
        totrinnskontroller.lastOrNull()?.let(vedtak::leggTilTotrinnskontroll)
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): Paragraf_11_27FørsteLeddModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerMaskinellVurdering :
        Tilstand.AvventerMaskinellVurdering<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_27FørsteLedd, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_27())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            løsning: LøsningParagraf_11_27_FørsteLedd
        ) {
            vilkårsvurdering.løsninger.add(løsning)

            if (løsning.harEnFullYtelse()) {
                vilkårsvurdering.tilstand(AvventerManuellVurdering, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): Paragraf_11_27FørsteLeddModellApi =
            vilkårsvurdering.toParagraf_11_27ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object AvventerManuellVurdering :
        Tilstand.AvventerManuellVurdering<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>() {

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_27FørsteLedd, løsning: LøsningParagraf_22_13) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.bestemmesAv11_27())
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            else
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): Paragraf_11_27FørsteLeddModellApi =
            vilkårsvurdering.toParagraf_11_27ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            kvalitetssikring: KvalitetssikringParagraf_22_13
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerMaskinellVurdering, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_11_27FørsteLedd, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_27(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_27(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): Paragraf_11_27FørsteLeddModellApi =
            vilkårsvurdering.toParagraf_11_27ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltKvalitetssikret :
        Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>() {
        override fun accept(vilkårsvurdering: Paragraf_11_27FørsteLedd, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_27(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_27(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): Paragraf_11_27FørsteLeddModellApi =
            vilkårsvurdering.toParagraf_11_27ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_11_27FørsteLedd, Paragraf_11_27FørsteLeddModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): Paragraf_11_27FørsteLeddModellApi =
            vilkårsvurdering.toParagraf_11_27ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_RELEVANT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_11_27ModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_11_27FørsteLeddModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_27_maskinell = løsninger.toDto(),
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_22_13::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
        ),
    )

    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            løsninger: List<LøsningParagraf_11_27_FørsteLedd_ModellApi>,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>>,
        ) = Paragraf_11_27FørsteLedd(
            vilkårsvurderingsid = vilkårsvurderingsid,
            tilstand = tilknyttetTilstand(tilstandsnavn),
            løsninger = gjenopprettLøsninger(løsninger),
            totrinnskontroll = gjenopprettTotrinnskontroller(totrinnskontroller)
        )

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MASKINELL_VURDERING -> AvventerMaskinellVurdering
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_27")
        }

        private fun gjenopprettLøsninger(løsninger: List<LøsningParagraf_11_27_FørsteLedd_ModellApi>) =
            løsninger.map(LøsningParagraf_11_27_FørsteLedd_ModellApi::toLøsning)

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_22_13ModellApi::toLøsning,
                KvalitetssikringParagraf_22_13ModellApi::toKvalitetssikring
            )
    }
}
