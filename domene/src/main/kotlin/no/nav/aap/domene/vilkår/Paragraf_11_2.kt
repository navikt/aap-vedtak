package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_11_2
import java.time.LocalDate

internal class Paragraf_11_2 private constructor(private var tilstand: Tilstand) :
    Vilkårsvurdering(Paragraf.PARAGRAF_11_2, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var maskineltLøsning: LøsningParagraf_11_2
    private lateinit var manueltLøsning: LøsningParagraf_11_2

    internal constructor() : this(Tilstand.IkkeVurdert)

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        this.tilstand.onExit(this, hendelse)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this, hendelse)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterLøsning(løsning: LøsningParagraf_11_2) {
        tilstand.vurderMedlemskap(this, løsning)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    private fun settMaskinellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoMaskinell = requireNotNull(vilkårsvurdering.løsning_11_2_maskinell)
        maskineltLøsning = LøsningParagraf_11_2(enumValueOf(dtoMaskinell.erMedlem))
    }

    private fun settManuellLøsning(vilkårsvurdering: DtoVilkårsvurdering) {
        val dtoManuell = requireNotNull(vilkårsvurdering.løsning_11_2_manuell)
        manueltLøsning = LøsningParagraf_11_2(enumValueOf(dtoManuell.erMedlem))
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

        internal open fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt
        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun vurderMedlemskap(
            vilkårsvurdering: Paragraf_11_2,
            løsning: LøsningParagraf_11_2
        ) {
            error("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
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
        }

        object ManuellVurderingTrengs :
            Tilstand(tilstandsnavn = Tilstandsnavn.MANUELL_VURDERING_TRENGS, erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2, hendelse: Hendelse) {
                //send ut behov for manuell vurdering av medlemskap
                hendelse.opprettBehov(Behov_11_2()) //FIXME Eget behov for manuell????
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                løsning: LøsningParagraf_11_2
            ) {
                vilkårsvurdering.manueltLøsning = løsning
                when {
                    løsning.erMedlem() -> vilkårsvurdering.tilstand(OppfyltManuelt, løsning)
                    løsning.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfyltManuelt, løsning)
                    else -> error("Veileder/saksbehandler må ta stilling til om bruker er medlem eller ikke")
                }
            }

            override fun toFrontendHarÅpenOppgave() = true

            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
            )

            override fun restoreData(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object OppfyltMaskinelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
            )

            override fun restoreData(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object IkkeOppfyltMaskinelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
            )

            override fun restoreData(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
            }
        }

        object OppfyltManuelt : Tilstand(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
                løsning_11_2_manuell = paragraf.manueltLøsning.toDto()
            )

            override fun restoreData(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
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
                paragraf = paragraf.paragraf.name,
                ledd = paragraf.ledd.map(Ledd::name),
                tilstand = tilstandsnavn.name,
                løsning_11_2_maskinell = paragraf.maskineltLøsning.toDto(),
                løsning_11_2_manuell = paragraf.manueltLøsning.toDto()
            )

            override fun restoreData(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {
                paragraf.settMaskinellLøsning(vilkårsvurdering)
                paragraf.settManuellLøsning(vilkårsvurdering)
            }
        }

        internal open fun toDto(paragraf: Paragraf_11_2): DtoVilkårsvurdering = DtoVilkårsvurdering(
            paragraf = paragraf.paragraf.name,
            ledd = paragraf.ledd.map(Ledd::name),
            tilstand = tilstandsnavn.name,
        )

        internal open fun restoreData(paragraf: Paragraf_11_2, vilkårsvurdering: DtoVilkårsvurdering) {}
        internal fun toFrontendTilstand(): String = tilstandsnavn.name
        internal open fun toFrontendHarÅpenOppgave() = false
    }

    override fun toDto(): DtoVilkårsvurdering = tilstand.toDto(this)
    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
    override fun toFrontendHarÅpenOppgave() = tilstand.toFrontendHarÅpenOppgave()

    internal companion object {
        internal fun create(vilkårsvurdering: DtoVilkårsvurdering): Paragraf_11_2 =
            enumValueOf<Tilstand.Tilstandsnavn>(vilkårsvurdering.tilstand)
                .tilknyttetTilstand()
                .let(::Paragraf_11_2)
                .apply { this.tilstand.restoreData(this, vilkårsvurdering) }
    }
}
