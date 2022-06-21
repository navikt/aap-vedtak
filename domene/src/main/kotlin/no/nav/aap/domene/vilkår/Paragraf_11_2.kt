package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_2
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("Paragraf_11_2")

internal class Paragraf_11_2 private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<Paragraf_11_2>
) :
    Vilkårsvurdering<Paragraf_11_2>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_2,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private lateinit var maskineltLøsning: LøsningParagraf_11_2
    private lateinit var manueltLøsning: LøsningParagraf_11_2

    internal constructor() : this(UUID.randomUUID(), P112IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_2.() -> T): T = this.block()

    private object P112IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_2>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(P112SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2): DtoVilkårsvurdering {
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }
    }

    private object P112SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_2>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
            //send ut behov for innhenting av maskinell medlemskapsvurdering
            hendelse.opprettBehov(Behov_11_2())
        }

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_2, løsning: LøsningParagraf_11_2) {
            vilkårsvurdering.maskineltLøsning = løsning
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(P112OppfyltMaskinelt, løsning)
                løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(P112IkkeOppfyltMaskinelt, løsning)
                else -> vilkårsvurdering.tilstand(P112ManuellVurderingTrengs, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT
        )
    }

    private object P112ManuellVurderingTrengs : Tilstand.ManuellVurderingTrengs<Paragraf_11_2>() {
        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_2, løsning: LøsningParagraf_11_2) {
            vilkårsvurdering.manueltLøsning = løsning
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(P112OppfyltManuelt, løsning)
                else -> vilkårsvurdering.tilstand(
                    P112IkkeOppfyltManuelt,
                    løsning
                ) // todo: se på alternativ løsning?
            }
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            løsning_11_2_maskinell = vilkårsvurdering.maskineltLøsning.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
        }
    }

    private object P112OppfyltMaskinelt : Tilstand.OppfyltMaskinelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskineltLøsning.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
        }
    }

    private object P112IkkeOppfyltMaskinelt : Tilstand.IkkeOppfyltMaskinelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskineltLøsning.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
        }
    }

    private object P112OppfyltManuelt : Tilstand.OppfyltManuelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manueltLøsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskineltLøsning.toDto(),
            løsning_11_2_manuell = vilkårsvurdering.manueltLøsning.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
        }
    }

    private object P112IkkeOppfyltManuelt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manueltLøsning.vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskineltLøsning.toDto(),
            løsning_11_2_manuell = vilkårsvurdering.manueltLøsning.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
        }
    }

    private fun settMaskinellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoVurdertAv = requireNotNull(vilkårsvurdering.vurdertAv)
        val dtoMaskinell = requireNotNull(vilkårsvurdering.løsning_11_2_maskinell)
        maskineltLøsning = LøsningParagraf_11_2(dtoVurdertAv, enumValueOf(dtoMaskinell.erMedlem))
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoVurdertAv = requireNotNull(vilkårsvurdering.vurdertAv)
        val dtoManuell = requireNotNull(vilkårsvurdering.løsning_11_2_manuell)
        manueltLøsning = LøsningParagraf_11_2(dtoVurdertAv, enumValueOf(dtoManuell.erMedlem))
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_2 =
            Paragraf_11_2(
                vilkårsvurdering.vilkårsvurderingsid,
                tilknyttetTilstand(enumValueOf(vilkårsvurdering.tilstand))
            )
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> P112IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> P112SøknadMottatt
            Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS -> P112ManuellVurderingTrengs
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT -> P112OppfyltMaskinelt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT -> P112IkkeOppfyltMaskinelt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> P112OppfyltManuelt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> P112IkkeOppfyltManuelt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_2")
        }
    }
}
