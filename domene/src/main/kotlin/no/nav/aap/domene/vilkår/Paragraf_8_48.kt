package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_8_48.*
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_22_13.Companion.toDto
import no.nav.aap.hendelse.LøsningSykepengedager.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import no.nav.aap.modellapi.Paragraf_8_48ModellApi
import no.nav.aap.modellapi.SykepengedagerModellApi
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
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
    private val løsninger22_13 = mutableListOf<LøsningParagraf_22_13>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_22_13>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_8_48.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_8_48>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_8_48,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_8_48>() {
        override fun onEntry(vilkårsvurdering: Paragraf_8_48, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_8_48AndreLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_8_48,
            løsning: LøsningSykepengedager
        ) {
            vilkårsvurdering.løsningSykepengedager.add(løsning)

            if (løsning.erRelevantFor8_48()) {
                vilkårsvurdering.tilstand(ManuellVurderingTrengs, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
            kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurderingModellApi as Paragraf_8_48ModellApi
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object ManuellVurderingTrengs : Tilstand.ManuellVurderingTrengs<Paragraf_8_48>() {

        override fun håndterLøsning(vilkårsvurdering: Paragraf_8_48, løsning: LøsningParagraf_22_13) {
            vilkårsvurdering.løsninger22_13.add(løsning)
            if (løsning.bestemmesAv8_48())
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            else
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_8_48): VilkårsvurderingModellApi = Paragraf_8_48ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
            kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurderingModellApi as Paragraf_8_48ModellApi
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_8_48>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_8_48,
            kvalitetssikring: KvalitetssikringParagraf_22_13
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
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
            løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
            kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurderingModellApi as Paragraf_8_48ModellApi
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
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
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_8_48_maskinell = vilkårsvurdering.løsningSykepengedager.toDto(),
            løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
            kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurderingModellApi as Paragraf_8_48ModellApi
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
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
            løsning_22_13_manuell = vilkårsvurdering.løsninger22_13.toDto(),
            kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_8_48,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurderingModellApi as Paragraf_8_48ModellApi
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: Paragraf_8_48ModellApi) {
        løsningSykepengedager.addAll(vilkårsvurdering.løsning_8_48_maskinell.map(SykepengedagerModellApi::toLøsning))
    }

    private fun settKvalitetssikring(vilkårsvurdering: Paragraf_8_48ModellApi) {
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
            Paragraf_8_48(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS -> ManuellVurderingTrengs
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_8_48")
        }
    }
}
