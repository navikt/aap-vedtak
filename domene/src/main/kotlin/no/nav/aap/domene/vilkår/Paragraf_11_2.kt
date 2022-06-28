package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Paragraf_11_2.*
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningManuellParagraf_11_2
import no.nav.aap.hendelse.LøsningManuellParagraf_11_2.Companion.toDto
import no.nav.aap.hendelse.LøsningMaskinellParagraf_11_2
import no.nav.aap.hendelse.LøsningMaskinellParagraf_11_2.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_2
import java.time.LocalDate
import java.util.*

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
    private val maskinelleLøsninger = mutableListOf<LøsningMaskinellParagraf_11_2>()
    private val manuelleLøsninger = mutableListOf<LøsningManuellParagraf_11_2>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: Paragraf_11_2.() -> T): T = this.block()

    private object IkkeVurdert : Tilstand.IkkeVurdert<Paragraf_11_2>() {
        override fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: Paragraf_11_2): DtoVilkårsvurdering {
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }
    }

    private object SøknadMottatt : Tilstand.SøknadMottatt<Paragraf_11_2>() {
        override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
            //send ut behov for innhenting av maskinell medlemskapsvurdering
            hendelse.opprettBehov(Behov_11_2())
        }

        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_2, løsning: LøsningMaskinellParagraf_11_2) {
            vilkårsvurdering.maskinelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltMaskinelt, løsning)
                løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltMaskinelt, løsning)
                else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, løsning)
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

    private object ManuellVurderingTrengs : Tilstand.ManuellVurderingTrengs<Paragraf_11_2>() {
        override fun håndterLøsning(vilkårsvurdering: Paragraf_11_2, løsning: LøsningManuellParagraf_11_2) {
            vilkårsvurdering.manuelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManuelt, løsning)
                else -> vilkårsvurdering.tilstand(
                    IkkeOppfyltManuelt,
                    løsning
                )
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
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
        }
    }

    private object OppfyltMaskinelt : Tilstand.OppfyltMaskinelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
        }
    }

    private object IkkeOppfyltMaskinelt : Tilstand.IkkeOppfyltMaskinelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
        }
    }

    private object OppfyltManuelt : Tilstand.OppfyltManuelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(), // TODO
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_11_2_manuell = vilkårsvurdering.manuelleLøsninger.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
        }
    }

    private object IkkeOppfyltManuelt : Tilstand.IkkeOppfyltManuelt<Paragraf_11_2>() {
        override fun toDto(vilkårsvurdering: Paragraf_11_2) = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
            godkjentAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_11_2_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_11_2_manuell = vilkårsvurdering.manuelleLøsninger.toDto()
        )

        override fun gjenopprettTilstand(vilkårsvurdering: Paragraf_11_2, dtoVilkårsvurdering: DtoVilkårsvurdering) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
        }
    }

    private fun settMaskinellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoMaskinell = requireNotNull(vilkårsvurdering.løsning_11_2_maskinell)
        maskinelleLøsninger.addAll(dtoMaskinell.map {
            LøsningMaskinellParagraf_11_2(
                enumValueOf(it.erMedlem)
            )
        })
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoManuell = requireNotNull(vilkårsvurdering.løsning_11_2_manuell)
        manuelleLøsninger.addAll(dtoManuell.map {
            LøsningManuellParagraf_11_2(
                it.vurdertAv,
                it.tidspunktForVurdering,
                enumValueOf(it.erMedlem)
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            Paragraf_11_2(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS -> ManuellVurderingTrengs
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT -> OppfyltMaskinelt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT -> IkkeOppfyltMaskinelt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> OppfyltManuelt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfyltManuelt
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_2")
        }
    }
}
