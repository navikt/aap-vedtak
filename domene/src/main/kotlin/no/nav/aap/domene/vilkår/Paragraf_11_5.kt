package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_5.SøknadMottatt
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_5
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_5 private constructor(
    vilkårsvurderingsid: UUID, tilstand: Tilstand<Paragraf_11_5>
) : Vilkårsvurdering<Paragraf_11_5>(
    vilkårsvurderingsid, Paragraf.PARAGRAF_11_5, Ledd.LEDD_1 + Ledd.LEDD_2, tilstand
) {
    private val løsninger = mutableListOf<LøsningParagraf_11_5>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_5>()
    private lateinit var nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_5.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_5>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_5>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_5, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_5())
        }

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_5, løsning: LøsningParagraf_11_5) {
            løsning.vurderNedsattArbeidsevne(this, vilkårsvurdering)
        }

        internal fun vurderNedsattArbeidsevne(
            vilkårsvurdering: Paragraf_11_5,
            løsning: LøsningParagraf_11_5,
            nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            vilkårsvurdering.nedsattArbeidsevnegrad = nedsattArbeidsevnegrad
            if (nedsattArbeidsevnegrad.erOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_5 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_5>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_5,
            kvalitetssikring: KvalitetssikringParagraf_11_5
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_5 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_5>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_5 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_5>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_5,
            kvalitetssikring: KvalitetssikringParagraf_11_5
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_5 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_5>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_5): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_5_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_5 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_5, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoLøsninger = vilkårsvurdering.løsning_11_5_manuell ?: emptyList()
        løsninger.addAll(dtoLøsninger.map {
            LøsningParagraf_11_5(
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                    nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                )
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_11_5 ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringParagraf_11_5(
                it.kvalitetssikretAv,
                it.tidspunktForKvalitetssikring,
                it.erGodkjent,
                it.begrunnelse
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_5(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_5")
        }
    }
}
