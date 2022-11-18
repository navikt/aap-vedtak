package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_8_48.*
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.LøsningSykepengedager.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_8_48 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_8_48, Paragraf_8_48ModellApi>,
    løsningSykepengedager: List<LøsningSykepengedager>,
    totrinnskontroller: List<Totrinnskontroll<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13>>,
) :
    Vilkårsvurdering<Paragraf_8_48, Paragraf_8_48ModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_8_48,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsningSykepengedager = løsningSykepengedager.toMutableList()
    private val totrinnskontroller = totrinnskontroller.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList(), emptyList())

    override fun <T> callWithReceiver(block: Paragraf_8_48.() -> T) = this.block()

    override fun lagSnapshot(vedtak: Vedtak) {
        totrinnskontroller.lastOrNull()?.let(vedtak::leggTilTotrinnskontroll)
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_8_48, Paragraf_8_48ModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_8_48,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): Paragraf_8_48ModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerMaskinellVurdering : Tilstand.AvventerMaskinellVurdering<Paragraf_8_48, Paragraf_8_48ModellApi>() {
        override fun onEntry(vilkårsvurdering: Paragraf_8_48, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_8_48AndreLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_8_48,
            løsning: LøsningSykepengedager
        ) {
            vilkårsvurdering.løsningSykepengedager.add(løsning)

            if (løsning.erRelevantFor8_48()) {
                vilkårsvurdering.tilstand(AvventerManuellVurdering, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): Paragraf_8_48ModellApi =
            vilkårsvurdering.toParagraf_8_48ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_8_48, Paragraf_8_48ModellApi>() {

        override fun håndterLøsning(vilkårsvurdering: Paragraf_8_48, løsning: LøsningParagraf_22_13) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.bestemmesAv8_48())
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            else
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): Paragraf_8_48ModellApi =
            vilkårsvurdering.toParagraf_8_48ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_8_48, Paragraf_8_48ModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_8_48,
            kvalitetssikring: KvalitetssikringParagraf_22_13
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerMaskinellVurdering, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_8_48, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_8_48(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsningSykepengedager.last())
            vilkårsvurdering.løsningSykepengedager.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsningSykepengedager.last())
            visitor.postVisitParagraf_8_48(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): Paragraf_8_48ModellApi =
            vilkårsvurdering.toParagraf_8_48ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_8_48, Paragraf_8_48ModellApi>() {
        override fun accept(vilkårsvurdering: Paragraf_8_48, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_8_48(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsningSykepengedager.last())
            vilkårsvurdering.løsningSykepengedager.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsningSykepengedager.last())
            visitor.postVisitParagraf_8_48(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): Paragraf_8_48ModellApi =
            vilkårsvurdering.toParagraf_8_48ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_8_48, Paragraf_8_48ModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_8_48): Paragraf_8_48ModellApi =
            vilkårsvurdering.toParagraf_8_48ModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_RELEVANT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_8_48ModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_8_48ModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_8_48_maskinell = løsningSykepengedager.toDto(),
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_22_13::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
        ),
    )

    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            maskinelleLøsninger: List<SykepengedagerModellApi>,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>>
        ) = Paragraf_8_48(
            vilkårsvurderingsid = vilkårsvurderingsid,
            tilstand = tilknyttetTilstand(tilstandsnavn),
            løsningSykepengedager = gjenopprettMaskinelleLøsninger(maskinelleLøsninger),
            totrinnskontroller = gjenopprettTotrinnskontroller(totrinnskontroller)
        )

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MASKINELL_VURDERING -> AvventerMaskinellVurdering
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_8_48")
        }

        private fun gjenopprettMaskinelleLøsninger(maskinelleLøsninger: List<SykepengedagerModellApi>) =
            maskinelleLøsninger.map(SykepengedagerModellApi::toLøsning)

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_22_13ModellApi::toLøsning,
                KvalitetssikringParagraf_22_13ModellApi::toKvalitetssikring,
            )
    }
}
