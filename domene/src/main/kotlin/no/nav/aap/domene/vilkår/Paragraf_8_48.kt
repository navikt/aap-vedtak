package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
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
    tilstand: Tilstand<Paragraf_8_48>
) :
    Vilkårsvurdering<Paragraf_8_48>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_8_48,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsningSykepengedager = mutableListOf<LøsningSykepengedager>()
    private val totrinnskontroller =
        mutableListOf<Totrinnskontroll<LøsningParagraf_22_13, KvalitetssikringParagraf_22_13>>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_8_48.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_8_48>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_8_48,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerMaskinellVurdering : Tilstand.AvventerMaskinellVurdering<Paragraf_8_48>() {
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

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_22_13::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            modellApi: Paragraf_8_48ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_8_48>() {

        override fun håndterLøsning(vilkårsvurdering: Paragraf_8_48, løsning: LøsningParagraf_22_13) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.bestemmesAv8_48())
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            else
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_22_13::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            modellApi: Paragraf_8_48ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_8_48>() {
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

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_22_13::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            modellApi: Paragraf_8_48ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_8_48>() {
        override fun accept(vilkårsvurdering: Paragraf_8_48, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_8_48(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsningSykepengedager.last())
            vilkårsvurdering.løsningSykepengedager.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsningSykepengedager.last())
            visitor.postVisitParagraf_8_48(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_22_13::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            modellApi: Paragraf_8_48ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_8_48>() {
        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_RELEVANT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                toLøsningDto = LøsningParagraf_22_13::toDto,
                toKvalitetssikringDto = KvalitetssikringParagraf_22_13::toDto,
            ),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            modellApi: Paragraf_8_48ModellApi
        ) {
            vilkårsvurdering.settMaskinellLøsning(modellApi)
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private fun settMaskinellLøsning(vilkårsvurdering: Paragraf_8_48ModellApi) {
        løsningSykepengedager.addAll(vilkårsvurdering.løsning_8_48_maskinell.map(SykepengedagerModellApi::toLøsning))
    }

    private fun gjenopprettTotrinnskontroller(modellApi: Paragraf_8_48ModellApi) {
        totrinnskontroller.addAll(
            modellApi.totrinnskontroller.gjenopprett(
                LøsningParagraf_22_13ModellApi::toLøsning,
                KvalitetssikringParagraf_22_13ModellApi::toKvalitetssikring,
            )
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_8_48(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MASKINELL_VURDERING -> AvventerMaskinellVurdering
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_8_48")
        }
    }
}
