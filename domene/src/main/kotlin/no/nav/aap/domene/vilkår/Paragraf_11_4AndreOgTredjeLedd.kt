package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_4AndreOgTredjeLedd.SøknadMottatt
import no.nav.aap.dto.VilkårsvurderingModellApi
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_4AndreOgTredjeLedd.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.hendelse.LøsningParagraf_11_4AndreOgTredjeLedd.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_4AndreOgTredjeLedd
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_4AndreOgTredjeLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_4AndreOgTredjeLedd>
) :
    Vilkårsvurdering<Paragraf_11_4AndreOgTredjeLedd>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_4,
        Ledd.LEDD_2 + Ledd.LEDD_3,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_11_4AndreOgTredjeLedd>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_4AndreOgTredjeLedd>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_4AndreOgTredjeLedd.() -> T) = this.block()

    private fun vurderAlder(hendelse: Hendelse, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        if (fødselsdato.erUnder62(vurderingsdato)) {
            tilstand(IkkeRelevant, hendelse)
        } else {
            tilstand(SøknadMottatt, hendelse)
        }
    }

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.vurderAlder(søknad, fødselsdato, vurderingsdato)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_4AndreOgTredjeLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            løsning: LøsningParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(Oppfylt, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfylt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_4_ledd2_ledd3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object Oppfylt : Tilstand.OppfyltManuelt<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_4_ledd2_ledd3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_4_ledd2_ledd3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object IkkeOppfylt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(SøknadMottatt, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_4_ledd2_ledd3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            løsning_11_4_ledd2_ledd3_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_4_ledd2_ledd3 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(vilkårsvurderingModellApi)
            vilkårsvurdering.settKvalitetssikring(vilkårsvurderingModellApi)
        }
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_11_4AndreOgTredjeLedd>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): VilkårsvurderingModellApi = VilkårsvurderingModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_RELEVANT,
            vurdertMaskinelt = vurdertMaskinelt,
        )
    }

    private fun settManuellLøsning(vilkårsvurdering: VilkårsvurderingModellApi) {
        val dtoLøsninger = vilkårsvurdering.løsning_11_4_ledd2_ledd3_manuell ?: emptyList()
        løsninger.addAll(dtoLøsninger.map {
            LøsningParagraf_11_4AndreOgTredjeLedd(it.løsningId, it.vurdertAv, it.tidspunktForVurdering, it.erOppfylt)
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: VilkårsvurderingModellApi) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_11_4_ledd2_ledd3 ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
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
            Paragraf_11_4AndreOgTredjeLedd(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> Oppfylt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfylt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_4AndreOgTredjeLedd")
        }
    }
}
