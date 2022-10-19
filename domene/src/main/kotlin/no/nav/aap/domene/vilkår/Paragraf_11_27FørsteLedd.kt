package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_27FørsteLedd.*
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_22_13.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_11_27
import no.nav.aap.modellapi.Paragraf_11_27FørsteLeddModellApi
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_27FørsteLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_27FørsteLedd>
) :
    Vilkårsvurdering<Paragraf_11_27FørsteLedd>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_27,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_27_FørsteLedd>()
    private val løsninger22_13 = mutableListOf<LøsningParagraf_22_13>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_22_13>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_27FørsteLedd.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_27FørsteLedd>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerMaskinellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerMaskinellVurdering : Tilstand.AvventerMaskinellVurdering<Paragraf_11_27FørsteLedd>() {
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

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): VilkårsvurderingModellApi =
            Paragraf_11_27FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_27_maskinell = vilkårsvurdering.løsninger.toDto(),
                løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            modellApi: Paragraf_11_27FørsteLeddModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_27FørsteLedd>() {

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_27FørsteLedd, løsning: LøsningParagraf_22_13) {
            vilkårsvurdering.løsninger22_13.add(løsning)
            if (løsning.bestemmesAv11_27())
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            else
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): VilkårsvurderingModellApi =
            Paragraf_11_27FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_27_maskinell = vilkårsvurdering.løsninger.toDto(),
                løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            modellApi: Paragraf_11_27FørsteLeddModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_27FørsteLedd>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            kvalitetssikring: KvalitetssikringParagraf_22_13
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
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

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): VilkårsvurderingModellApi =
            Paragraf_11_27FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = "maskinell saksbehandling",
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_27_maskinell = vilkårsvurdering.løsninger.toDto(),
                løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            modellApi: Paragraf_11_27FørsteLeddModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_27FørsteLedd>() {
        override fun accept(vilkårsvurdering: Paragraf_11_27FørsteLedd, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_27(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_27(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): VilkårsvurderingModellApi =
            Paragraf_11_27FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = "maskinell saksbehandling",
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_27_maskinell = vilkårsvurdering.løsninger.toDto(),
                løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            modellApi: Paragraf_11_27FørsteLeddModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_11_27FørsteLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_27FørsteLedd): VilkårsvurderingModellApi =
            Paragraf_11_27FørsteLeddModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_RELEVANT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_27_maskinell = vilkårsvurdering.løsninger.toDto(),
                løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_27FørsteLedd,
            modellApi: Paragraf_11_27FørsteLeddModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: Paragraf_11_27FørsteLeddModellApi) {
        løsninger.addAll(vilkårsvurdering.løsning_11_27_maskinell.map {
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = it.løsningId,
                tidspunktForVurdering = it.tidspunktForVurdering,
                svangerskapspenger = it.svangerskapspenger.gjenopprett()
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: Paragraf_11_27FørsteLeddModellApi) {
        kvalitetssikringer.addAll(vilkårsvurdering.kvalitetssikringer_22_13.map {
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = it.kvalitetssikringId,
                løsningId = it.løsningId,
                kvalitetssikretAv = it.kvalitetssikretAv,
                tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                erGodkjent = it.erGodkjent,
                begrunnelse = it.begrunnelse
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_27FørsteLedd(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MASKINELL_VURDERING -> AvventerMaskinellVurdering
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_27")
        }
    }
}
