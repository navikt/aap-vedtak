package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException.Companion.ulovligTilstand
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
    tilstand: Tilstand
) :
    Vilkårsvurdering<Paragraf_11_2, Paragraf_11_2.Tilstand>(
        vilkårsvurderingsid,
        Paragraf.PARAGRAF_11_2,
        Ledd.LEDD_1 + Ledd.LEDD_2,
        tilstand
    ) {
    private lateinit var maskineltLøsning: LøsningParagraf_11_2
    private lateinit var manueltLøsning: LøsningParagraf_11_2

    internal constructor() : this(UUID.randomUUID(), Tilstand.IkkeVurdert)

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_2) {
        tilstand.vurderMedlemskap(this, løsning)
    }

    override fun onEntry(hendelse: Hendelse) {
        tilstand.onEntry(this, hendelse)
    }

    override fun onExit(hendelse: Hendelse) {
        tilstand.onExit(this, hendelse)
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

    internal sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) : Vilkårsvurderingstilstand<Paragraf_11_2> {
        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            IKKE_VURDERT({ IkkeVurdert }),
            SØKNAD_MOTTATT({ SøknadMottatt }),
            MANUELL_VURDERING_TRENGS({ ManuellVurderingTrengs }),
            OPPFYLT_MASKINELT({ OppfyltMaskinelt }),
            IKKE_OPPFYLT_MASKINELT({ IkkeOppfyltMaskinelt }),
            OPPFYLT_MANUELT({ OppfyltManuelt }),
            IKKE_OPPFYLT_MANUELT({ IkkeOppfyltManuelt })
        }

        override fun erOppfylt() = erOppfylt
        override fun erIkkeOppfylt() = erIkkeOppfylt
        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun vurderMedlemskap(
            vilkårsvurdering: Paragraf_11_2,
            løsning: LøsningParagraf_11_2
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        object IkkeVurdert : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_2,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt, søknad)
            }

            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering =
                ulovligTilstand("IkkeVurdert skal håndtere søknad før serialisering")
        }

        object SøknadMottatt :
            Tilstand(tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT, erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
                //send ut behov for innhenting av maskinell medlemskapsvurdering
                hendelse.opprettBehov(Behov_11_2())
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.maskineltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltMaskinelt, løsning)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltMaskinelt, løsning)
                    else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs, løsning)
                }
            }

            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = null,
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT
            )
        }

        object ManuellVurderingTrengs :
            Tilstand(tilstandsnavn = Tilstandsnavn.MANUELL_VURDERING_TRENGS, erOppfylt = false, erIkkeOppfylt = false) {

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.manueltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManuelt, løsning)
                    else -> vilkårsvurdering.tilstand(IkkeOppfyltManuelt, løsning) // todo: se på alternativ løsning?
                }
            }

            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = null,
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_VURDERT,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object OppfyltMaskinelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = "maskinell saksbehandling",
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object IkkeOppfyltMaskinelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = "maskinell saksbehandling",
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object OppfyltManuelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = paragraf.manueltLøsning.vurdertAv(),
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.OPPFYLT,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
                løsning_11_2_manuell = paragraf.manueltLøsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
                paragraf.settManuellLøsning(vilkårsvurdering)
            }
        }

        object IkkeOppfyltManuelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                vilkårsvurderingsid = paragraf.vilkårsvurderingsid,
                vurdertAv = paragraf.manueltLøsning.vurdertAv(),
                godkjentAv = null,
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                utfall = Utfall.IKKE_OPPFYLT,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
                løsning_11_2_manuell = paragraf.manueltLøsning.toDto()
            )

            override fun gjenopprettTilstand(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
                paragraf.settManuellLøsning(vilkårsvurdering)
            }
        }
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)

    internal companion object {
        internal fun gjenopprett(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_2 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let { tilstand -> Paragraf_11_2(vilkårsvurdering.vilkårsvurderingsid, tilstand) }
                .apply { this.tilstand.gjenopprettTilstand(this, vilkårsvurdering) }
    }
}
