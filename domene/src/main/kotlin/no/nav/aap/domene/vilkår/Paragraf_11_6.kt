package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_6.AvventerInnstilling
import no.nav.aap.domene.vilkår.Paragraf_11_6.AvventerManuellVurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_6
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_6.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_11_6
import no.nav.aap.hendelse.LøsningParagraf_11_6.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_6
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6.Companion.toDto
import no.nav.aap.modellapi.Paragraf_11_6ModellApi
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_6 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_6>
) :
    Vilkårsvurdering<Paragraf_11_6>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_6,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val innstillinger = mutableListOf<InnstillingParagraf_11_6>()
    private val løsninger = mutableListOf<LøsningParagraf_11_6>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_11_6>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_6.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_6>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_6,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(AvventerInnstilling, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerInnstilling : Tilstand.AvventerInnstilling<Paragraf_11_6>() {

        override fun håndterInnstilling(
            vilkårsvurdering: Paragraf_11_6,
            løsning: InnstillingParagraf_11_6
        ) {
            vilkårsvurdering.innstillinger.add(løsning)
            vilkårsvurdering.tilstand(AvventerManuellVurdering, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi = Paragraf_11_6ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            innstillinger_11_6 = vilkårsvurdering.innstillinger.toDto(),
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_6 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_6,
            modellApi: Paragraf_11_6ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_11_6>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_6, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_6())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_6,
            løsning: LøsningParagraf_11_6
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi = Paragraf_11_6ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            vurdertMaskinelt = vurdertMaskinelt,
            innstillinger_11_6 = vilkårsvurdering.innstillinger.toDto(),
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_6 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_6,
            modellApi: Paragraf_11_6ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_6>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_6,
            kvalitetssikring: KvalitetssikringParagraf_11_6
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi = Paragraf_11_6ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            innstillinger_11_6 = vilkårsvurdering.innstillinger.toDto(),
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_6 = vilkårsvurdering.kvalitetssikringer.toDto()
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_6,
            modellApi: Paragraf_11_6ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_6>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi = Paragraf_11_6ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            innstillinger_11_6 = vilkårsvurdering.innstillinger.toDto(),
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_6 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_6,
            modellApi: Paragraf_11_6ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltAvventerKvalitetssikring : Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_6>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_6,
            kvalitetssikring: KvalitetssikringParagraf_11_6
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi = Paragraf_11_6ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            innstillinger_11_6 = vilkårsvurdering.innstillinger.toDto(),
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_6 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_6,
            modellApi: Paragraf_11_6ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    object IkkeOppfyltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_6>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_6): VilkårsvurderingModellApi = Paragraf_11_6ModellApi(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            vurdertMaskinelt = vurdertMaskinelt,
            innstillinger_11_6 = vilkårsvurdering.innstillinger.toDto(),
            løsning_11_6_manuell = vilkårsvurdering.løsninger.toDto(),
            kvalitetssikringer_11_6 = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_11_6,
            modellApi: Paragraf_11_6ModellApi
        ) {
            vilkårsvurdering.settInnstillinger(modellApi)
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
        }
    }

    private fun settInnstillinger(vilkårsvurdering: Paragraf_11_6ModellApi) {
        innstillinger.addAll(vilkårsvurdering.innstillinger_11_6.map {
            InnstillingParagraf_11_6(
                innstillingId = it.innstillingId,
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                harBehovForBehandling = it.harBehovForBehandling,
                harBehovForTiltak = it.harBehovForTiltak,
                harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid,
                individuellBegrunnelse = it.individuellBegrunnelse,
            )
        })
    }

    private fun settManuellLøsning(vilkårsvurdering: Paragraf_11_6ModellApi) {
        løsninger.addAll(vilkårsvurdering.løsning_11_6_manuell.map {
            LøsningParagraf_11_6(
                løsningId = it.løsningId,
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                harBehovForBehandling = it.harBehovForBehandling,
                harBehovForTiltak = it.harBehovForTiltak,
                harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid,
                individuellBegrunnelse = it.individuellBegrunnelse,
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: Paragraf_11_6ModellApi) {
        kvalitetssikringer.addAll(vilkårsvurdering.kvalitetssikringer_11_6.map {
            KvalitetssikringParagraf_11_6(
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
            Paragraf_11_6(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_INNSTILLING -> AvventerInnstilling
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_6")
        }

    }
}
