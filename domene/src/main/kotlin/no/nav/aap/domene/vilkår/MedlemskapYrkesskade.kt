package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningMaskinellMedlemskapYrkesskade
import no.nav.aap.hendelse.LøsningManuellMedlemskapYrkesskade
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_2
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

private val log = LoggerFactory.getLogger("MedlemskapYrkesskade")

internal class MedlemskapYrkesskade private constructor(
    vilkårsvurderingsid: UUID,
    private var tilstand: Tilstand
) :
    Vilkårsvurdering(vilkårsvurderingsid, Paragraf.MEDLEMSKAP_YRKESSKADE, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var maskinellLøsning: LøsningMaskinellMedlemskapYrkesskade
    private lateinit var manuellLøsning: LøsningManuellMedlemskapYrkesskade

    internal constructor() : this(UUID.randomUUID(), Tilstand.IkkeVurdert)

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) {
        tilstand.håndterLøsning(this, løsning)
    }

    override fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) {
        tilstand.håndterLøsning(this, løsning)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    private fun settMaskinellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoMaskinell = requireNotNull(vilkårsvurdering. løsning_medlemskap_yrkesskade_maskinell)
        maskinellLøsning = LøsningMaskinellMedlemskapYrkesskade(enumValueOf(dtoMaskinell.erMedlem))
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoManuell = requireNotNull(vilkårsvurdering.løsning_medlemskap_yrkesskade_manuell)
        manuellLøsning = LøsningManuellMedlemskapYrkesskade(enumValueOf(dtoManuell.erMedlem))
    }

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            MANUELL_VURDERING_TRENGS({ ManuellVurderingTrengs }),
            OPPFYLT_MASKINELT({ OppfyltMaskinelt }),
            IKKE_OPPFYLT_MASKINELT({ IkkeOppfyltMaskinelt }),
            OPPFYLT_MANUELT({ OppfyltManuelt }),
            IKKE_OPPFYLT_MANUELT({ IkkeOppfyltManuelt })
        }

        internal open fun onEntry(vilkårsvurdering: MedlemskapYrkesskade, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: MedlemskapYrkesskade, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt
        internal open fun håndterSøknad(
            vilkårsvurdering: MedlemskapYrkesskade,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningMaskinellMedlemskapYrkesskade
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: MedlemskapYrkesskade,
            løsning: LøsningManuellMedlemskapYrkesskade
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: MedlemskapYrkesskade,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }

            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering =
                UlovligTilstandException.ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }

        object SøknadMottatt :
            Tilstand(tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT, erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: MedlemskapYrkesskade, hendelse: Hendelse) {
                //send ut behov for innhenting av maskinell medlemskapsvurdering
                hendelse.opprettBehov(Behov_11_2())
            }

            override fun håndterLøsning(
                vilkårsvurdering: MedlemskapYrkesskade,
                løsning: LøsningMaskinellMedlemskapYrkesskade
            ) {
                vilkårsvurdering.maskinellLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltMaskinelt, løsning)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltMaskinelt, løsning)
                    else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, løsning)
                }
            }

            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false
            )
        }

        object ManuellVurderingTrengs :
            Tilstand(tilstandsnavn = Tilstandsnavn.MANUELL_VURDERING_TRENGS, erOppfylt = false, erIkkeOppfylt = false) {

            override fun håndterLøsning(
                vilkårsvurdering: MedlemskapYrkesskade,
                løsning: LøsningManuellMedlemskapYrkesskade
            ) {
                vilkårsvurdering.manuellLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManuelt, løsning)
                    else -> vilkårsvurdering.tilstand(IkkeOppfyltManuelt, løsning)
                }
            }

            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = true,
                løsning_medlemskap_yrkesskade_maskinell = paragraf.maskinellLøsning.toDto(),
            )

            override fun gjenopprettTilstand(paragraf: MedlemskapYrkesskade, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object OppfyltMaskinelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false,
                løsning_medlemskap_yrkesskade_maskinell = paragraf.maskinellLøsning.toDto(),
            )

            override fun gjenopprettTilstand(paragraf: MedlemskapYrkesskade, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object IkkeOppfyltMaskinelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false,
                løsning_medlemskap_yrkesskade_maskinell = paragraf.maskinellLøsning.toDto(),
            )

            override fun gjenopprettTilstand(paragraf: MedlemskapYrkesskade, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object OppfyltManuelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false,
                løsning_medlemskap_yrkesskade_maskinell = paragraf.maskinellLøsning.toDto(),
                løsning_medlemskap_yrkesskade_manuell = paragraf.manuellLøsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: MedlemskapYrkesskade, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
                paragraf.settManuellLøsning(vilkårsvurdering)
            }
        }

        object IkkeOppfyltManuelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                måVurderesManuelt = false,
                løsning_medlemskap_yrkesskade_maskinell = paragraf.maskinellLøsning.toDto(),
                løsning_medlemskap_yrkesskade_manuell = paragraf.manuellLøsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: MedlemskapYrkesskade, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
                paragraf.settManuellLøsning(vilkårsvurdering)
            }
        }

        internal open fun gjenopprettTilstand(paragraf: MedlemskapYrkesskade, vilkårsvurdering: DtoVilkårsvurdering) {}
        internal abstract fun toDto(paragraf: MedlemskapYrkesskade): DtoVilkårsvurdering
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): MedlemskapYrkesskade =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let { tilstand -> MedlemskapYrkesskade(vilkårsvurdering.vilkårsvurderingsid, tilstand) }
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}