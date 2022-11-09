package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_22_13.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Paragraf_22_13.IkkeRelevant
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13.Companion.toDto
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.LøsningParagraf_22_13.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_22_13
import no.nav.aap.modellapi.Paragraf_22_13ModellApi
import no.nav.aap.modellapi.Utfall
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class Paragraf_22_13 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_22_13>
) :
    Vilkårsvurdering<Paragraf_22_13>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_22_13,
        Ledd.LEDD_1,
        tilstand
    ) {
    private val løsninger = mutableListOf<LøsningParagraf_22_13>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringParagraf_22_13>()
    private val søknadstidspunkter = mutableListOf<Søknadsdata>()

    private class Søknadsdata(val søknadId: UUID, val søknadstidspunkt: LocalDateTime)

    private val søknadstidspunkt: LocalDateTime
        get() = søknadstidspunkter.last().søknadstidspunkt

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_22_13.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_22_13>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_22_13,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.søknadstidspunkter.add(Søknadsdata(søknad.søknadId(), søknad.søknadstidspunkt()))
            vilkårsvurdering.tilstand(AvventerManuellVurdering, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_22_13): VilkårsvurderingModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerManuellVurdering : Tilstand.AvventerManuellVurdering<Paragraf_22_13>() {
        override fun onEntry(vilkårsvurdering: Paragraf_22_13, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_22_13())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_22_13,
            løsning: LøsningParagraf_22_13
        ) {
            vilkårsvurdering.løsninger.add(løsning)
            if (løsning.bestemmesAv22_13())
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            else
                vilkårsvurdering.tilstand(IkkeRelevant, løsning)
        }

        override fun toDto(vilkårsvurdering: Paragraf_22_13): VilkårsvurderingModellApi =
            Paragraf_22_13ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_22_13_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
                søknadsdata = vilkårsvurdering.søknadstidspunkter.map {
                    Paragraf_22_13ModellApi.SøknadsdataModellApi(
                        søknadId = it.søknadId,
                        søknadstidspunkt = it.søknadstidspunkt,
                    )
                }
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_22_13,
            modellApi: Paragraf_22_13ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
            vilkårsvurdering.settSøknadsdata(modellApi)
        }
    }

    object OppfyltAvventerKvalitetssikring : Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_22_13>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_22_13,
            kvalitetssikring: KvalitetssikringParagraf_22_13
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun accept(vilkårsvurdering: Paragraf_22_13, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_22_13(vilkårsvurdering, vilkårsvurdering.søknadstidspunkt)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_22_13(vilkårsvurdering, vilkårsvurdering.søknadstidspunkt)
        }

        override fun toDto(vilkårsvurdering: Paragraf_22_13): VilkårsvurderingModellApi =
            Paragraf_22_13ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_22_13_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
                søknadsdata = vilkårsvurdering.søknadstidspunkter.map {
                    Paragraf_22_13ModellApi.SøknadsdataModellApi(
                        søknadId = it.søknadId,
                        søknadstidspunkt = it.søknadstidspunkt,
                    )
                }
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_22_13,
            modellApi: Paragraf_22_13ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
            vilkårsvurdering.settSøknadsdata(modellApi)
        }
    }

    object OppfyltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<Paragraf_22_13>() {
        override fun accept(vilkårsvurdering: Paragraf_22_13, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_22_13(vilkårsvurdering, vilkårsvurdering.søknadstidspunkt)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_22_13(vilkårsvurdering, vilkårsvurdering.søknadstidspunkt)
        }

        override fun toDto(vilkårsvurdering: Paragraf_22_13): VilkårsvurderingModellApi =
            Paragraf_22_13ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = vilkårsvurdering.løsninger.last().vurdertAv(),
                kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_22_13_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
                søknadsdata = vilkårsvurdering.søknadstidspunkter.map {
                    Paragraf_22_13ModellApi.SøknadsdataModellApi(
                        søknadId = it.søknadId,
                        søknadstidspunkt = it.søknadstidspunkt,
                    )
                }
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_22_13,
            modellApi: Paragraf_22_13ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
            vilkårsvurdering.settSøknadsdata(modellApi)
        }
    }

    object IkkeRelevant : Tilstand.IkkeRelevant<Paragraf_22_13>() {
        override fun accept(vilkårsvurdering: Paragraf_22_13, visitor: VilkårsvurderingVisitor) {
            visitor.preVisitParagraf_22_13(vilkårsvurdering, vilkårsvurdering.søknadstidspunkt)
            visitor.preVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            vilkårsvurdering.løsninger.last().accept(visitor)
            visitor.postVisitGjeldendeLøsning(vilkårsvurdering.løsninger.last())
            visitor.postVisitParagraf_22_13(vilkårsvurdering, vilkårsvurdering.søknadstidspunkt)
        }

        override fun toDto(vilkårsvurdering: Paragraf_22_13): VilkårsvurderingModellApi =
            Paragraf_22_13ModellApi(
                vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
                vurdertAv = null,
                kvalitetssikretAv = null,
                paragraf = vilkårsvurdering.paragraf.name,
                ledd = vilkårsvurdering.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_RELEVANT,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_22_13_manuell = vilkårsvurdering.løsninger.toDto(),
                kvalitetssikringer_22_13 = vilkårsvurdering.kvalitetssikringer.toDto(),
                søknadsdata = vilkårsvurdering.søknadstidspunkter.map {
                    Paragraf_22_13ModellApi.SøknadsdataModellApi(
                        søknadId = it.søknadId,
                        søknadstidspunkt = it.søknadstidspunkt,
                    )
                }
            )

        override fun gjenopprettTilstand(
            vilkårsvurdering: Paragraf_22_13,
            modellApi: Paragraf_22_13ModellApi
        ) {
            vilkårsvurdering.settManuellLøsning(modellApi)
            vilkårsvurdering.settKvalitetssikring(modellApi)
            vilkårsvurdering.settSøknadsdata(modellApi)
        }
    }

    private fun settManuellLøsning(vilkårsvurdering: Paragraf_22_13ModellApi) {
        løsninger.addAll(vilkårsvurdering.løsning_22_13_manuell.map {
            LøsningParagraf_22_13(
                løsningId = it.løsningId,
                vurdertAv = it.vurdertAv,
                tidspunktForVurdering = it.tidspunktForVurdering,
                bestemmesAv = enumValueOf(it.bestemmesAv),
                unntak = it.unntak,
                unntaksbegrunnelse = it.unntaksbegrunnelse,
                manueltSattVirkningsdato = it.manueltSattVirkningsdato,
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: Paragraf_22_13ModellApi) {
        kvalitetssikringer.addAll(vilkårsvurdering.kvalitetssikringer_22_13.map {
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = it.kvalitetssikringId,
                kvalitetssikretAv = it.kvalitetssikretAv,
                tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                erGodkjent = it.erGodkjent,
                begrunnelse = it.begrunnelse,
                løsningId = it.løsningId,
            )
        })
    }

    private fun settSøknadsdata(vilkårsvurdering: Paragraf_22_13ModellApi) {
        søknadstidspunkter.addAll(vilkårsvurdering.søknadsdata.map {
            Søknadsdata(
                søknadId = it.søknadId,
                søknadstidspunkt = it.søknadstidspunkt,
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_22_13(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_22_13")
        }
    }
}
