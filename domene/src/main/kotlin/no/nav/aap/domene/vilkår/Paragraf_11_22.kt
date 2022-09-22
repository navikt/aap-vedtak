package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_22.SøknadMottatt
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_22
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_22.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_22
import no.nav.aap.hendelse.LøsningParagraf_11_22.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_22
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_22 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_22>
) :
    Vilkårsvurdering<Paragraf_11_22>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_22,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_22>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_22>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_22.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_22>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_22,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_22>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_22, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_22())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_22,
            løsning: LøsningParagraf_11_22
        ) {
            vilkårsvurdering.løsninger.add(løsning)

            if (løsning.erOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_22_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_22 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_22,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_22>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_22,
            kvalitetssikring: KvalitetssikringParagraf_11_22
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_11_22, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_22(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_22(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_22_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_22 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_22,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_22>() {
        override fun accept(vilkårsvurdering: Paragraf_11_22, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_22(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_22(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_22_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_22 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_22,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_22>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_22,
            kvalitetssikring: KvalitetssikringParagraf_11_22
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_22): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_22_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_22 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_22,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_22>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_22): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_22_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_22 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_22,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: VilkårsvurderingModellApi) {
        val dtoLøsninger = vilkårsvurdering.løsning_11_22_manuell ?: emptyList()
        løsninger.addAll(dtoLøsninger.map {
            LøsningParagraf_11_22(
                løsningId = it.løsningId,
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                erOppfylt = it.erOppfylt,
                andelNedsattArbeidsevne = it.andelNedsattArbeidsevne,
                år = it.år,
                antattÅrligArbeidsinntekt = it.antattÅrligArbeidsinntekt.beløp
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: VilkårsvurderingModellApi) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_11_22 ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringParagraf_11_22(
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
            Paragraf_11_22(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_22")
        }
    }
}
