package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_4AndreOgTredjeLedd.AvventerManuellVurdering
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.gjenopprett
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.leggTilKvalitetssikring
import no.nav.aap.domene.vilkår.Totrinnskontroll.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.hendelse.LøsningParagraf_11_4AndreOgTredjeLedd
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_4AndreOgTredjeLedd
import no.nav.aap.modellapi.*
import java.time.LocalDate
import java.util.*

internal class Paragraf_11_4AndreOgTredjeLedd private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>,
    totrinnskontroller: List<Totrinnskontroll<LøsningParagraf_11_4AndreOgTredjeLedd, KvalitetssikringParagraf_11_4AndreOgTredjeLedd>>,
) :
    Vilkårsvurdering<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_4,
        Ledd.LEDD_2 + Ledd.LEDD_3,
        tilstand
    ) {
    private val totrinnskontroller = totrinnskontroller.toMutableList()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert, emptyList())

    override fun <T> callWithReceiver(block: Paragraf_11_4AndreOgTredjeLedd.() -> T) = this.block()

    override fun lagSnapshot(vedtak: Vedtak) {
        totrinnskontroller.lastOrNull()?.let(vedtak::leggTilTotrinnskontroll)
    }

    private fun vurderAlder(hendelse: Hendelse, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        if (fødselsdato.erUnder62(vurderingsdato)) {
            tilstand(IkkeRelevant, hendelse)
        } else {
            tilstand(AvventerManuellVurdering, hendelse)
        }
    }

    object IkkeVurdert :
        Tilstand.IkkeVurdert<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.vurderAlder(søknad, fødselsdato, vurderingsdato)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object AvventerManuellVurdering :
        Tilstand.AvventerManuellVurdering<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd, hendelse: Hendelse) {
            hendelse.opprettBehov(Behov_11_4AndreOgTredjeLedd())
        }

        override fun håndterLøsning(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            løsning: LøsningParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.totrinnskontroller.add(Totrinnskontroll(løsning))
            if (løsning.erManueltOppfylt()) {
                vilkårsvurdering.tilstand(OppfyltAvventerKvalitetssikring, løsning)
            } else {
                vilkårsvurdering.tilstand(IkkeOppfyltAvventerKvalitetssikring, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            vilkårsvurdering.toParagraf_11_4AndreOgTredjeLeddModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_VURDERT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltAvventerKvalitetssikring :
        Tilstand.OppfyltManueltAvventerKvalitetssikring<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            vilkårsvurdering.toParagraf_11_4AndreOgTredjeLeddModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object OppfyltKvalitetssikret :
        Tilstand.OppfyltManueltKvalitetssikret<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(OppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            vilkårsvurdering.toParagraf_11_4AndreOgTredjeLeddModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltAvventerKvalitetssikring :
        Tilstand.IkkeOppfyltManueltAvventerKvalitetssikring<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            vilkårsvurdering.totrinnskontroller.leggTilKvalitetssikring(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(IkkeOppfyltKvalitetssikret, kvalitetssikring)
                else -> vilkårsvurdering.tilstand(AvventerManuellVurdering, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            vilkårsvurdering.toParagraf_11_4AndreOgTredjeLeddModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeOppfyltKvalitetssikret :
        Tilstand.IkkeOppfyltManueltKvalitetssikret<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            vilkårsvurdering.toParagraf_11_4AndreOgTredjeLeddModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_OPPFYLT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    object IkkeRelevant :
        Tilstand.IkkeRelevant<Paragraf_11_4AndreOgTredjeLedd, Paragraf_11_4AndreOgTredjeLeddModellApi>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_4AndreOgTredjeLedd): Paragraf_11_4AndreOgTredjeLeddModellApi =
            vilkårsvurdering.toParagraf_11_4AndreOgTredjeLeddModellApi(
                tilstandsnavn = tilstandsnavn,
                utfall = Utfall.IKKE_RELEVANT,
                vurdertMaskinelt = vurdertMaskinelt,
            )
    }

    private fun toParagraf_11_4AndreOgTredjeLeddModellApi(
        tilstandsnavn: Tilstand.Tilstandsnavn,
        utfall: Utfall,
        vurdertMaskinelt: Boolean,
    ) = Paragraf_11_4AndreOgTredjeLeddModellApi(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf.name,
        ledd = ledd.map(Ledd::name),
        tilstand = tilstandsnavn.name,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.toDto(
            toLøsningDto = LøsningParagraf_11_4AndreOgTredjeLedd::toDto,
            toKvalitetssikringDto = KvalitetssikringParagraf_11_4AndreOgTredjeLedd::toDto,
        ),
    )

    internal companion object {
        internal fun gjenopprett(
            vilkårsvurderingsid: UUID,
            tilstandsnavn: Tilstand.Tilstandsnavn,
            totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_4AndreOgTredjeLeddModellApi, KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi>>
        ) = Paragraf_11_4AndreOgTredjeLedd(
            vilkårsvurderingsid = vilkårsvurderingsid,
            tilstand = tilknyttetTilstand(tilstandsnavn),
            totrinnskontroller = gjenopprettTotrinnskontroller(totrinnskontroller)
        )

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING -> AvventerManuellVurdering
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> OppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING -> IkkeOppfyltAvventerKvalitetssikring
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_RELEVANT -> IkkeRelevant
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_4AndreOgTredjeLedd")
        }

        private fun gjenopprettTotrinnskontroller(totrinnskontroller: List<TotrinnskontrollModellApi<LøsningParagraf_11_4AndreOgTredjeLeddModellApi, KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi>>) =
            totrinnskontroller.gjenopprett(
                LøsningParagraf_11_4AndreOgTredjeLeddModellApi::toLøsning,
                KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi::toKvalitetssikring,
            )
    }
}
