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
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_19ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_19ModellApi
import no.nav.aap.modellapi.Paragraf_11_19ModellApi
import no.nav.aap.modellapi.Utfall
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_19 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_19, Paragraf_11_19ModellApi>
) :
    Vilkårsvurdering<Paragraf_11_19, Paragraf_11_19ModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_19,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val totrinnskontroller =
        mutableListOf<Totrinnskontroll<LøsningParagraf_11_19, KvalitetssikringParagraf_11_19>>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

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
            Paragraf_11_19ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                    toLøsningDto = LøsningParagraf_11_19::toDto,
                    toKvalitetssikringDto = KvalitetssikringParagraf_11_19::toDto,
                ),
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
            Paragraf_11_19ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                    toLøsningDto = LøsningParagraf_11_19::toDto,
                    toKvalitetssikringDto = KvalitetssikringParagraf_11_19::toDto,
                ),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_19,
            modellApi: Paragraf_11_19ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
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
            Paragraf_11_19ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                totrinnskontroller = vilkårsvurdering.totrinnskontroller.toDto(
                    toLøsningDto = LøsningParagraf_11_19::toDto,
                    toKvalitetssikringDto = KvalitetssikringParagraf_11_19::toDto,
                ),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_19,
            modellApi: Paragraf_11_19ModellApi
        ) {
            vilkårsvurdering.gjenopprettTotrinnskontroller(modellApi)
        }
    }

    private fun gjenopprettTotrinnskontroller(modellApi: Paragraf_11_19ModellApi) {
        totrinnskontroller.addAll(
            modellApi.totrinnskontroller.gjenopprett(
                LøsningParagraf_11_19ModellApi::toLøsning,
                KvalitetssikringParagraf_11_19ModellApi::toKvalitetssikring,
            )
        )
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn): Paragraf_11_19 =
            Paragraf_11_19(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_19")
        }
    }
}
