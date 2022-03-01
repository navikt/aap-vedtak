package no.nav.aap.domene

import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.*
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erAlleOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.erNoenIkkeOppfylt
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toFrontendVilkårsvurdering
import no.nav.aap.dto.DtoSak
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.BehovInntekter
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

private val log = LoggerFactory.getLogger("sak")

internal class Sak private constructor(
    private var tilstand: Tilstand,
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering>,
    private val inntektshistorikk: Inntektshistorikk,
) {
    private lateinit var vurderingAvBeregningsdato: VurderingAvBeregningsdato
    private lateinit var vurderingsdato: LocalDate
    private lateinit var vedtak: Vedtak

    constructor() : this(Tilstand.Start, mutableListOf(), Inntektshistorikk())

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato) {
        this.vurderingsdato = LocalDate.now()
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_2) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_3) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningVurderingAvBeregningsdato) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
        tilstand.håndterLøsning(this, løsning, fødselsdato)
    }

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
        nyTilstand.onExit(this, hendelse)
        tilstand = nyTilstand
        tilstand.onEntry(this, hendelse)
    }

    private sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn
    ) {
        enum class Tilstandsnavn {
            START,
            STANDARD_SØKNAD_MOTTATT,
            STANDARD_BEREGN_INNTEKT,
            STANDARD_VEDTAK_FATTET,
            STANDARD_IKKE_OPPFYLT,
            STUDENT_SØKNAD_MOTTATT,
            STUDENT_BEREGN_INNTEKT,
            STUDENT_VEDTAK_FATTET,
            STUDENT_IKKE_OPPFYLT,
            UFØRETRYGD_SØKNAD_MOTTATT,
            ARBEIDSSØKER_SØKNAD_MOTTATT
        }

        open fun onEntry(sak: Sak, hendelse: Hendelse) {}
        open fun onExit(sak: Sak, hendelse: Hendelse) {}
        open fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            log.info("Forventet ikke søknad i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            log.info("Forventet ikke løsning på inntekter i tilstand ${tilstandsnavn.name}")
        }

        open fun toFrontendSak(sak: Sak, personident: Personident, fødselsdato: Fødselsdato) =
            FrontendSak(
                personident = personident.toFrontendPersonident(),
                fødselsdato = fødselsdato.toFrontendFødselsdato(),
                tilstand = tilstandsnavn.name,
                vilkårsvurderinger = sak.vilkårsvurderinger.toFrontendVilkårsvurdering(),
                vedtak = null
            )

        open fun toDto(sak: Sak) = DtoSak(
            tilstand = tilstandsnavn.name,
            vilkårsvurderinger = sak.vilkårsvurderinger.toDto(),
            vurderingsdato = sak.vurderingsdato, // ALLTID SATT
            vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
            vedtak = null
        )

        open fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
            sak.vurderingsdato = dtoSak.vurderingsdato
            sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
        }

        object Start : Tilstand(Tilstandsnavn.START) {
            override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
                val nestePositiveTilstand = vurderLøype(sak, søknad, fødselsdato, vurderingsdato)

                vurderNestetilstand(sak, søknad, nestePositiveTilstand)
            }

            private fun vurderLøype(
                sak: Sak,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ): Tilstand {
                return if (søknad.erStudent()) {
                    sak.vilkårsvurderinger.add(Paragraf_11_14())
                    sak.vilkårsvurderinger.forEach { it.håndterSøknad(søknad, fødselsdato, vurderingsdato) }

                    sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato()
                    sak.vurderingAvBeregningsdato.håndterSøknad(søknad)
                    StudentSøknadMottatt
                } else if (søknad.harSøktUføretrygd()) {
                    UføretrygdSøknadMottatt
                } else if (søknad.erArbeidssøker()) {
                    ArbeidssøkerSøknadMottatt
                } else {
                    //opprett initielle vilkårsvurderinger
                    sak.vilkårsvurderinger.add(Paragraf_11_2())
                    sak.vilkårsvurderinger.add(Paragraf_11_3())
                    sak.vilkårsvurderinger.add(Paragraf_11_4FørsteLedd())
                    sak.vilkårsvurderinger.add(Paragraf_11_4AndreOgTredjeLedd())
                    sak.vilkårsvurderinger.add(Paragraf_11_5())
                    sak.vilkårsvurderinger.add(Paragraf_11_6())
                    sak.vilkårsvurderinger.add(Paragraf_11_12FørsteLedd())
                    sak.vilkårsvurderinger.add(Paragraf_11_29())
                    sak.vilkårsvurderinger.forEach { it.håndterSøknad(søknad, fødselsdato, vurderingsdato) }

                    sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato()
                    sak.vurderingAvBeregningsdato.håndterSøknad(søknad)
                    StandardSøknadMottatt
                }
            }

            private fun vurderNestetilstand(sak: Sak, søknad: Søknad, nestePositiveTilstand: Tilstand) {
                when {
                    sak.vilkårsvurderinger.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt, søknad)
                    else -> sak.tilstand(nestePositiveTilstand, søknad)
                }
            }
        }

        object StudentSøknadMottatt : Tilstand(Tilstandsnavn.STUDENT_SØKNAD_MOTTATT) {

            override fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
                sak.vurderingAvBeregningsdato.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                when {
                    sak.vilkårsvurderinger.erAlleOppfylt() && sak.vurderingAvBeregningsdato.erFerdig() ->
                        sak.tilstand(StudentBeregnInntekt, hendelse)
                    sak.vilkårsvurderinger.erNoenIkkeOppfylt() ->
                        sak.tilstand(IkkeOppfylt, hendelse)
                }
            }
        }

        object StudentBeregnInntekt : Tilstand(Tilstandsnavn.STUDENT_BEREGN_INNTEKT) {

            override fun onEntry(sak: Sak, hendelse: Hendelse) {
                hendelse.opprettBehov(
                    BehovInntekter(
                        fom = Year.from(sak.vurderingAvBeregningsdato.beregningsdato()).minusYears(3),
                        tom = Year.from(sak.vurderingAvBeregningsdato.beregningsdato()).minusYears(1)
                    )
                )
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
                løsning.lagreInntekter(sak.inntektshistorikk)
                val inntektsgrunnlag =
                    sak.inntektshistorikk.finnInntektsgrunnlag(
                        sak.vurderingAvBeregningsdato.beregningsdato(),
                        fødselsdato
                    )
                sak.vedtak = Vedtak(
                    innvilget = true,
                    inntektsgrunnlag = inntektsgrunnlag,
                    søknadstidspunkt = LocalDateTime.now(),
                    vedtaksdato = LocalDate.now(),
                    virkningsdato = LocalDate.now()
                )

                sak.tilstand(StudentVedtakFattet, løsning)
            }
        }

        object StudentVedtakFattet : Tilstand(Tilstandsnavn.STUDENT_VEDTAK_FATTET) {

            override fun toDto(sak: Sak) = DtoSak(
                tilstand = tilstandsnavn.name,
                vilkårsvurderinger = sak.vilkårsvurderinger.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
                sak.vurderingsdato = dtoSak.vurderingsdato
                sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
                val dtoVedtak = requireNotNull(dtoSak.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }

            override fun toFrontendSak(sak: Sak, personident: Personident, fødselsdato: Fødselsdato) =
                FrontendSak(
                    personident = personident.toFrontendPersonident(),
                    fødselsdato = fødselsdato.toFrontendFødselsdato(),
                    tilstand = tilstandsnavn.name,
                    vilkårsvurderinger = sak.vilkårsvurderinger.toFrontendVilkårsvurdering(),
                    vedtak = sak.vedtak.toFrontendVedtak()
                )
        }

        object StudentIkkeOppfylt : Tilstand(Tilstandsnavn.STUDENT_IKKE_OPPFYLT)

        object UføretrygdSøknadMottatt : Tilstand(Tilstandsnavn.UFØRETRYGD_SØKNAD_MOTTATT)
        object ArbeidssøkerSøknadMottatt : Tilstand(Tilstandsnavn.ARBEIDSSØKER_SØKNAD_MOTTATT)

        object StandardSøknadMottatt : Tilstand(Tilstandsnavn.STANDARD_SØKNAD_MOTTATT) {
            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
                sak.vilkårsvurderinger.forEach { it.håndterLøsning(løsning) }
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
                sak.vurderingAvBeregningsdato.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                when {
                    sak.vilkårsvurderinger.erAlleOppfylt() && sak.vurderingAvBeregningsdato.erFerdig() ->
                        sak.tilstand(BeregnInntekt, hendelse)
                    sak.vilkårsvurderinger.erNoenIkkeOppfylt() ->
                        sak.tilstand(IkkeOppfylt, hendelse)
                }
            }
        }

        object BeregnInntekt : Tilstand(Tilstandsnavn.STANDARD_BEREGN_INNTEKT) {

            override fun onEntry(sak: Sak, hendelse: Hendelse) {
                hendelse.opprettBehov(
                    BehovInntekter(
                        fom = Year.from(sak.vurderingAvBeregningsdato.beregningsdato()).minusYears(3),
                        tom = Year.from(sak.vurderingAvBeregningsdato.beregningsdato()).minusYears(1)
                    )
                )
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
                løsning.lagreInntekter(sak.inntektshistorikk)
                val inntektsgrunnlag =
                    sak.inntektshistorikk.finnInntektsgrunnlag(
                        sak.vurderingAvBeregningsdato.beregningsdato(),
                        fødselsdato
                    )
                sak.vedtak = Vedtak(
                    innvilget = true,
                    inntektsgrunnlag = inntektsgrunnlag,
                    søknadstidspunkt = LocalDateTime.now(),
                    vedtaksdato = LocalDate.now(),
                    virkningsdato = LocalDate.now()
                )

                sak.tilstand(VedtakFattet, løsning)
            }
        }

        object VedtakFattet : Tilstand(Tilstandsnavn.STANDARD_VEDTAK_FATTET) {

            override fun toDto(sak: Sak) = DtoSak(
                tilstand = tilstandsnavn.name,
                vilkårsvurderinger = sak.vilkårsvurderinger.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
                sak.vurderingsdato = dtoSak.vurderingsdato
                sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
                val dtoVedtak = requireNotNull(dtoSak.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }

            override fun toFrontendSak(sak: Sak, personident: Personident, fødselsdato: Fødselsdato) =
                FrontendSak(
                    personident = personident.toFrontendPersonident(),
                    fødselsdato = fødselsdato.toFrontendFødselsdato(),
                    tilstand = tilstandsnavn.name,
                    vilkårsvurderinger = sak.vilkårsvurderinger.toFrontendVilkårsvurdering(),
                    vedtak = sak.vedtak.toFrontendVedtak()
                )
        }

        object IkkeOppfylt : Tilstand(Tilstandsnavn.STANDARD_IKKE_OPPFYLT)
    }

    private fun toDto() = tilstand.toDto(this)

    private fun toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) =
        tilstand.toFrontendSak(this, personident, fødselsdato)

    internal companion object {
        internal fun Iterable<Sak>.toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) = map {
            it.toFrontendSak(personident = personident, fødselsdato = fødselsdato)
        }

        internal fun Iterable<Sak>.toDto() = map { sak -> sak.toDto() }

        internal fun gjenopprett(dtoSak: DtoSak): Sak = Sak(
            vilkårsvurderinger = dtoSak.vilkårsvurderinger.mapNotNull(Vilkårsvurdering::gjenopprett).toMutableList(),
            tilstand = when (Tilstand.Tilstandsnavn.valueOf(dtoSak.tilstand)) {
                Tilstand.Tilstandsnavn.START -> Tilstand.Start
                Tilstand.Tilstandsnavn.STANDARD_SØKNAD_MOTTATT -> Tilstand.StandardSøknadMottatt
                Tilstand.Tilstandsnavn.STANDARD_BEREGN_INNTEKT -> Tilstand.BeregnInntekt
                Tilstand.Tilstandsnavn.STANDARD_VEDTAK_FATTET -> Tilstand.VedtakFattet
                Tilstand.Tilstandsnavn.STANDARD_IKKE_OPPFYLT -> Tilstand.IkkeOppfylt
                Tilstand.Tilstandsnavn.STUDENT_SØKNAD_MOTTATT -> Tilstand.StudentSøknadMottatt
                Tilstand.Tilstandsnavn.STUDENT_BEREGN_INNTEKT -> Tilstand.StudentBeregnInntekt
                Tilstand.Tilstandsnavn.STUDENT_VEDTAK_FATTET -> Tilstand.StudentVedtakFattet
                Tilstand.Tilstandsnavn.STUDENT_IKKE_OPPFYLT -> Tilstand.StudentIkkeOppfylt
                Tilstand.Tilstandsnavn.UFØRETRYGD_SØKNAD_MOTTATT -> Tilstand.UføretrygdSøknadMottatt
                Tilstand.Tilstandsnavn.ARBEIDSSØKER_SØKNAD_MOTTATT -> Tilstand.ArbeidssøkerSøknadMottatt
            },
            inntektshistorikk = Inntektshistorikk()
        ).apply {
            this.tilstand.gjenopprettTilstand(this, dtoSak)
        }
    }
}
