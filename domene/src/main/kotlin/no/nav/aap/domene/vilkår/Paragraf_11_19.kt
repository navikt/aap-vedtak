package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_19.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_19
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_19.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_19
import no.nav.aap.hendelse.LøsningParagraf_11_19.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_19
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_19 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_19>
) :
    Vilkårsvurdering<Paragraf_11_19>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_19,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_19>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_19>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_19.() -> T) = this.block()

    private object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_19>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_19,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate,
        ) {
            søknad.opprettBehov(Behov_11_19())
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_19): DtoVilkårsvurdering {
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }
    }

    internal object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_19>() {
        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_19,
            løsning: LøsningParagraf_11_19
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            vilkårsvurdering.tilstand(Oppfylt, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_19) =
            DtoVilkårsvurdering(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                løsning_11_19_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_19 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )
    }

    internal object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_19>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_19,
            kvalitetssikring: KvalitetssikringParagraf_11_19
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun beregningsdato(vilkårsvurdering: Paragraf_11_19): LocalDate =
            vilkårsvurdering.løsninger.last().beregningsdato

        override fun toDto(vilkårsvurdering: Paragraf_11_19) =
            DtoVilkårsvurdering(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                løsning_11_19_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_19 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_19,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    internal object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_19>() {
        override fun beregningsdato(vilkårsvurdering: Paragraf_11_19): LocalDate =
            vilkårsvurdering.løsninger.last().beregningsdato

        override fun toDto(vilkårsvurdering: Paragraf_11_19) =
            DtoVilkårsvurdering(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                løsning_11_19_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_11_19 = vilkårsvurdering.kvalitetssikringer.toDto(),
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_19,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoLøsninger = vilkårsvurdering.løsning_11_19_manuell ?: emptyList()
        løsninger.addAll(dtoLøsninger.map {
            LøsningParagraf_11_19(
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                beregningsdato = it.beregningsdato,
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_11_19 ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringParagraf_11_19(
                it.kvalitetssikretAv,
                it.tidspunktForKvalitetssikring,
                it.erGodkjent,
                it.begrunnelse
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn): Paragraf_11_19 =
            Paragraf_11_19(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_19")
        }
    }
}
