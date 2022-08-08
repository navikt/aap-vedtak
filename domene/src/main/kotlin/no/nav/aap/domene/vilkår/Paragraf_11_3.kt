package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_3.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_3
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_3.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_3
import no.nav.aap.hendelse.LøsningParagraf_11_3.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_3
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_3")

internal class Paragraf_11_3 private constructor(
    vilkårsvurderingsid: UUID, tilstand: Tilstand<Paragraf_11_3>
) : Vilkårsvurdering<Paragraf_11_3>(
    vilkårsvurderingsid, Paragraf.PARAGRAF_11_3, Ledd.LEDD_1 + Ledd.LEDD_2 + Ledd.LEDD_3, tilstand
) {
    private val løsninger = mutableListOf<LøsningParagraf_11_3>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_3>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_3.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_3>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_3, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_3>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_3, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_3())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_3, løsning: LøsningParagraf_11_3
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            løsning_11_3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_3, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_3>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_3,
            kvalitetssikring: KvalitetssikringParagraf_11_3
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_3, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_3>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_3, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_3>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_3,
            kvalitetssikring: KvalitetssikringParagraf_11_3
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_3, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_3>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_3): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_3, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoLøsninger = vilkårsvurdering.løsning_11_3_manuell ?: emptyList()
        løsninger.addAll(dtoLøsninger.map {
            LøsningParagraf_11_3(it.vurdertAv, it.tidspunktForVurdering, it.erOppfylt)
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_11_3 ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringParagraf_11_3(it.kvalitetssikretAv, it.erGodkjent, it.begrunnelse)
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_3(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_3")
        }
    }
}
