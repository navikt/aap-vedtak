package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_12FørsteLedd.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_12FørsteLedd.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_12FørsteLedd
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_12FørsteLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_12FørsteLedd>
) :
    Vilkårsvurdering<Paragraf_11_12FørsteLedd>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_12,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_12FørsteLedd>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_12FørsteLedd>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_12FørsteLedd.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_12FørsteLedd>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_12FørsteLedd>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_12FørsteLedd, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_12FørsteLedd())
            hendelse.opprettBehov(Behov_8_48AndreLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            løsning: LøsningParagraf_11_12FørsteLedd
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            vilkårsvurdering.tilstand(Oppfylt, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_12_ledd1_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_12_ledd1 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_12FørsteLedd>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_12FørsteLedd
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_11_12FørsteLedd, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_12FørsteLedd(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_12FørsteLedd(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_12_ledd1_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_12_ledd1 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_12FørsteLedd>() {
        override fun accept(vilkårsvurdering: Paragraf_11_12FørsteLedd, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_11_12FørsteLedd(vilkårsvurdering)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_11_12FørsteLedd(vilkårsvurdering)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_12FørsteLedd): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_12_ledd1_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_12_ledd1 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_12FørsteLedd,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoLøsninger = vilkårsvurdering.løsning_11_12_ledd1_manuell ?: emptyList()
        løsninger.addAll(dtoLøsninger.map {
            LøsningParagraf_11_12FørsteLedd(
                løsningId = it.løsningId,
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                bestemmesAv = enumValueOf(it.bestemmesAv),
                unntak = it.unntak,
                unntaksbegrunnelse = it.unntaksbegrunnelse,
                manueltSattVirkningsdato = it.manueltSattVirkningsdato
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_11_12_ledd1 ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringParagraf_11_12FørsteLedd(
                kvalitetssikringId = it.kvalitetssikringId,
                kvalitetssikretAv = it.kvalitetssikretAv,
                tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                erGodkjent = it.erGodkjent,
                begrunnelse = it.begrunnelse,
                løsningId = it.løsningId
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_12FørsteLedd(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_12FørsteLedd")
        }
    }
}
