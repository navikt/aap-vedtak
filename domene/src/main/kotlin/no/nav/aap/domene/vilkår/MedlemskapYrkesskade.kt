package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.MedlemskapYrkesskade.*
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.KvalitetssikringMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.LøsningManuellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.LøsningMaskinellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.hendelse.behov.Behov_11_2
import java.time.LocalDate
import java.util.*

internal class MedlemskapYrkesskade private constructor(
    vilkårsvurderingsid: UUID,
    tilstand: Tilstand<MedlemskapYrkesskade>
) :
    Vilkårsvurdering<MedlemskapYrkesskade>(
        vilkårsvurderingsid,
        Paragraf.MEDLEMSKAP_YRKESSKADE,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private val maskinelleLøsninger = mutableListOf<LøsningMaskinellMedlemskapYrkesskade>()
    private val manuelleLøsninger = mutableListOf<LøsningManuellMedlemskapYrkesskade>()
    private val kvalitetssikringer = mutableListOf<KvalitetssikringMedlemskapYrkesskade>()

    internal constructor() : this(UUID.randomUUID(), IkkeVurdert)

    override fun <T> callWithReceiver(block: MedlemskapYrkesskade.() -> T) = this.block()

    object IkkeVurdert : Tilstand.IkkeVurdert<MedlemskapYrkesskade>() {
        override fun håndterSøknad(
            vilkårsvurdering: MedlemskapYrkesskade,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            vilkårsvurdering.tilstand(SøknadMottatt, søknad)
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering =
            UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
    }

    object SøknadMottatt : Tilstand.SøknadMottatt<MedlemskapYrkesskade>() {
        override fun onEntry(vilkårsvurdering: MedlemskapYrkesskade, hendelse: Hendelse) {
            //send ut behov for innhenting av maskinell medlemskapsvurdering
            hendelse.opprettBehov(Behov_11_2())
        }

        override fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningMaskinellMedlemskapYrkesskade
        ) {
            vilkårsvurdering.maskinelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltMaskinelt, løsning)
                løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltMaskinelt, løsning)
                else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
        )
    }

    object ManuellVurderingTrengs : Tilstand.ManuellVurderingTrengs<MedlemskapYrkesskade>() {
        override fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningManuellMedlemskapYrkesskade
        ) {
            vilkårsvurdering.manuelleLøsninger.add(løsning)
            when {
                løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManuelt, løsning)
                else -> vilkårsvurdering.tilstand(IkkeOppfyltManuelt, løsning)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = null,
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_VURDERT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltMaskinelt : Tilstand.OppfyltMaskinelt<MedlemskapYrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    OppfyltMaskineltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltMaskineltKvalitetssikret : Tilstand.OppfyltMaskineltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfyltMaskinelt : Tilstand.IkkeOppfyltMaskinelt<MedlemskapYrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    IkkeOppfyltMaskineltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfyltMaskineltKvalitetssikret : Tilstand.IkkeOppfyltMaskineltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = "maskinell saksbehandling",
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltManuelt : Tilstand.OppfyltManuelt<MedlemskapYrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    OppfyltManueltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object OppfyltManueltKvalitetssikret : Tilstand.OppfyltManueltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfyltManuelt : Tilstand.IkkeOppfyltManuelt<MedlemskapYrkesskade>() {
        override fun håndterKvalitetssikring(
            vilkårsvurdering: MedlemskapYrkesskade,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            vilkårsvurdering.kvalitetssikringer.add(kvalitetssikring)
            when {
                kvalitetssikring.erGodkjent() -> vilkårsvurdering.tilstand(
                    IkkeOppfyltManueltKvalitetssikret,
                    kvalitetssikring
                )

                else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, kvalitetssikring)
            }
        }

        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
            kvalitetssikretAv = null,
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    object IkkeOppfyltManueltKvalitetssikret : Tilstand.IkkeOppfyltManueltKvalitetssikret<MedlemskapYrkesskade>() {
        override fun toDto(vilkårsvurdering: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurdering.vilkårsvurderingsid,
            vurdertAv = vilkårsvurdering.manuelleLøsninger.last().vurdertAv(),
            kvalitetssikretAv = vilkårsvurdering.kvalitetssikringer.last().kvalitetssikretAv(),
            paragraf = vilkårsvurdering.paragraf.name,
            ledd = vilkårsvurdering.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
            utfall = Utfall.IKKE_OPPFYLT,
            løsning_medlemskap_yrkesskade_maskinell = vilkårsvurdering.maskinelleLøsninger.toDto(),
            løsning_medlemskap_yrkesskade_manuell = vilkårsvurdering.manuelleLøsninger.toDto(),
            kvalitetssikringer_medlemskap_yrkesskade = vilkårsvurdering.kvalitetssikringer.toDto(),
        )

        override fun gjenopprettTilstand(
            vilkårsvurdering: MedlemskapYrkesskade,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
            vilkårsvurdering.settMaskinellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settManuellLøsning(dtoVilkårsvurdering)
            vilkårsvurdering.settKvalitetssikring(dtoVilkårsvurdering)
        }
    }

    private fun settMaskinellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoMaskinell = requireNotNull(vilkårsvurdering.løsning_medlemskap_yrkesskade_maskinell)
        maskinelleLøsninger.addAll(dtoMaskinell.map { LøsningMaskinellMedlemskapYrkesskade(it.løsningId, enumValueOf(it.erMedlem)) })
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoManuell = requireNotNull(vilkårsvurdering.løsning_medlemskap_yrkesskade_manuell)
        manuelleLøsninger.addAll(dtoManuell.map {
            LøsningManuellMedlemskapYrkesskade(
                it.løsningId,
                it.vurdertAv,
                it.tidspunktForVurdering,
                enumValueOf(it.erMedlem)
            )
        })
    }

    private fun settKvalitetssikring(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoKvalitetssikringer = vilkårsvurdering.kvalitetssikringer_medlemskap_yrkesskade ?: emptyList()
        kvalitetssikringer.addAll(dtoKvalitetssikringer.map {
            KvalitetssikringMedlemskapYrkesskade(
                it.kvalitetssikringId,
                it.løsningId,
                it.kvalitetssikretAv,
                it.tidspunktForKvalitetssikring,
                it.erGodkjent,
                it.begrunnelse
            )
        })
    }

    internal companion object {
        internal fun gjenopprett(vilkårsvurderingsid: UUID, tilstandsnavn: Tilstand.Tilstandsnavn) =
            MedlemskapYrkesskade(vilkårsvurderingsid, tilknyttetTilstand(tilstandsnavn))

        private fun tilknyttetTilstand(tilstandsnavn: Tilstand.Tilstandsnavn) = when (tilstandsnavn) {
            Tilstand.Tilstandsnavn.IKKE_VURDERT -> IkkeVurdert
            Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> SøknadMottatt
            Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS -> ManuellVurderingTrengs
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT -> OppfyltMaskinelt
            Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET -> OppfyltMaskineltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT -> IkkeOppfyltMaskinelt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET -> IkkeOppfyltMaskineltKvalitetssikret
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT -> OppfyltManuelt
            Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET -> OppfyltManueltKvalitetssikret
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT -> IkkeOppfyltManuelt
            Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET -> IkkeOppfyltManueltKvalitetssikret
            else -> error("Tilstand ${tilstandsnavn.name} ikke i bruk i Paragraf_11_2")
        }
    }
}
