package no.nav.aap.domene

import no.nav.aap.domene.Sakstype.Companion.toDto
import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.dto.DtoSak
import no.nav.aap.frontendView.FrontendSak
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.BehovInntekter
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year

internal class Sak private constructor(
    private var tilstand: Tilstand,
    private val sakstyper: MutableList<Sakstype>,
    private val inntektshistorikk: Inntektshistorikk
) {
    private val sakstype: Sakstype get() = sakstyper.last()
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
            SØKNAD_MOTTATT,
            BEREGN_INNTEKT,
            VEDTAK_FATTET,
            IKKE_OPPFYLT
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
                sakstype = sak.sakstype.toFrontendSakstype(),
                vedtak = null
            )

        open fun toDto(sak: Sak) = DtoSak(
            tilstand = tilstandsnavn.name,
            sakstyper = sak.sakstyper.toDto(),
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
                opprettLøype(sak, søknad)
                sak.sakstype.håndterSøknad(søknad, fødselsdato, vurderingsdato)

                sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato()
                sak.vurderingAvBeregningsdato.håndterSøknad(søknad)

                vurderNestetilstand(sak, søknad)
            }

            private fun opprettLøype(
                sak: Sak,
                søknad: Søknad
            ) {
                val sakstype: Sakstype = when {
                    søknad.erStudent() -> Sakstype.Student.opprettStudent()
                    søknad.harSøktUføretrygd() -> TODO("Opprett uføretrygd")
                    søknad.erArbeidssøker() -> TODO("Opprett arbeidssøker")
                    else -> Sakstype.Standard.opprettStandard()
                }
                sak.sakstyper.add(sakstype)
            }

            private fun vurderNestetilstand(sak: Sak, søknad: Søknad) {
                when {
                    sak.sakstype.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt, søknad)
                    else -> sak.tilstand(SøknadMottatt, søknad)
                }
            }

            override fun toFrontendSak(sak: Sak, personident: Personident, fødselsdato: Fødselsdato) =
                FrontendSak(
                    personident = personident.toFrontendPersonident(),
                    fødselsdato = fødselsdato.toFrontendFødselsdato(),
                    tilstand = tilstandsnavn.name,
                    sakstype = null,
                    vedtak = null
                )
        }

        object SøknadMottatt : Tilstand(Tilstandsnavn.SØKNAD_MOTTATT) {
            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
                sak.vurderingAvBeregningsdato.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                when {
                    sak.sakstype.erAlleOppfylt() && sak.vurderingAvBeregningsdato.erFerdig() ->
                        sak.tilstand(BeregnInntekt, hendelse)
                    sak.sakstype.erNoenIkkeOppfylt() ->
                        sak.tilstand(IkkeOppfylt, hendelse)
                }
            }
        }

        object BeregnInntekt : Tilstand(Tilstandsnavn.BEREGN_INNTEKT) {

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

        object VedtakFattet : Tilstand(Tilstandsnavn.VEDTAK_FATTET) {

            override fun toDto(sak: Sak) = DtoSak(
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
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
                    sakstype = sak.sakstype.toFrontendSakstype(),
                    vedtak = sak.vedtak.toFrontendVedtak()
                )
        }

        object IkkeOppfylt : Tilstand(Tilstandsnavn.IKKE_OPPFYLT)
    }

    private fun toDto() = tilstand.toDto(this)

    private fun toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) =
        tilstand.toFrontendSak(this, personident, fødselsdato)

    internal companion object {
        private val log = LoggerFactory.getLogger("sak")

        internal fun Iterable<Sak>.toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) = map {
            it.toFrontendSak(personident = personident, fødselsdato = fødselsdato)
        }

        internal fun Iterable<Sak>.toDto() = map { sak -> sak.toDto() }

        internal fun gjenopprett(dtoSak: DtoSak): Sak = Sak(
            tilstand = when (Tilstand.Tilstandsnavn.valueOf(dtoSak.tilstand)) {
                Tilstand.Tilstandsnavn.START -> Tilstand.Start
                Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> Tilstand.SøknadMottatt
                Tilstand.Tilstandsnavn.BEREGN_INNTEKT -> Tilstand.BeregnInntekt
                Tilstand.Tilstandsnavn.VEDTAK_FATTET -> Tilstand.VedtakFattet
                Tilstand.Tilstandsnavn.IKKE_OPPFYLT -> Tilstand.IkkeOppfylt
            },
            sakstyper = dtoSak.sakstyper.map(Sakstype::gjenopprett).toMutableList(),
            inntektshistorikk = Inntektshistorikk()
        ).apply {
            this.tilstand.gjenopprettTilstand(this, dtoSak)
        }
    }
}
