package no.nav.aap.domene

import no.nav.aap.domene.Sakstype.Companion.toDto
import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoSak
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.BehovInntekter
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.util.*

internal class Sak private constructor(
    private val saksid: UUID,
    private var tilstand: Tilstand,
    private val sakstyper: MutableList<Sakstype>,
    private val inntektshistorikk: Inntektshistorikk
) {
    private val sakstype: Sakstype get() = sakstyper.last()
    private lateinit var vurderingsdato: LocalDate
    private lateinit var søknadstidspunkt: LocalDateTime
    private lateinit var vedtak: Vedtak

    constructor() : this(UUID.randomUUID(), Tilstand.Start, mutableListOf(), Inntektshistorikk())

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato) {
        tilstand.håndterSøknad(this, søknad, fødselsdato)
    }

    internal fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningMaskinellParagraf_11_2) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningManuellParagraf_11_2) {
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

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5_yrkesskade) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_22) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_19) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
        tilstand.håndterLøsning(this, løsning, fødselsdato)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_2) {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
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
            AVVENTER_KVALITETSSIKRING,
            VENTER_SYKEPENGER,
            VEDTAK_FATTET,
            IKKE_OPPFYLT
        }

        open fun onEntry(sak: Sak, hendelse: Hendelse) {}
        open fun onExit(sak: Sak, hendelse: Hendelse) {}
        open fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato) {
            log.info("Forventet ikke søknad i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningMaskinellMedlemskapYrkesskade) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningManuellMedlemskapYrkesskade) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningMaskinellParagraf_11_2) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningManuellParagraf_11_2) {
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

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5_yrkesskade) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_22) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_19) {
            log.info("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            log.info("Forventet ikke løsning på inntekter i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterKvalitetssikring(sak: Sak, kvalitetssikring: KvalitetssikringParagraf_11_2) {
            log.info("Forventet ikke kvalitetssikring på paragraf 11-2 i tilstand ${tilstandsnavn.name}")
        }

        open fun toDto(sak: Sak) = DtoSak(
            saksid = sak.saksid,
            tilstand = tilstandsnavn.name,
            sakstyper = sak.sakstyper.toDto(),
            vurderingsdato = sak.vurderingsdato, // ALLTID SATT
            søknadstidspunkt = sak.søknadstidspunkt,
            vedtak = null
        )

        open fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
            sak.søknadstidspunkt = dtoSak.søknadstidspunkt
            sak.vurderingsdato = dtoSak.vurderingsdato
        }

        object Start : Tilstand(Tilstandsnavn.START) {
            override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato) {
                sak.søknadstidspunkt = LocalDateTime.now()
                sak.vurderingsdato = LocalDate.now()

                opprettLøype(sak, søknad)
                sak.sakstype.håndterSøknad(søknad, fødselsdato, sak.vurderingsdato)

                vurderNestetilstand(sak, søknad)
            }

            private fun opprettLøype(
                sak: Sak,
                søknad: Søknad
            ) {
                val sakstype: Sakstype = when {
                    søknad.erStudent() -> Sakstype.Student.opprettStudent()
                    søknad.harTidligereYrkesskade() -> Sakstype.Yrkesskade.opprettYrkesskade()
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
        }

        object SøknadMottatt : Tilstand(Tilstandsnavn.SØKNAD_MOTTATT) {
            override fun håndterLøsning(sak: Sak, løsning: LøsningMaskinellMedlemskapYrkesskade) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningManuellMedlemskapYrkesskade) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningMaskinellParagraf_11_2) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningManuellParagraf_11_2) {
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

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5_yrkesskade) {
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

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_22) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_19) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                when {
                    sak.sakstype.erAlleOppfylt() ->
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
                        fom = Year.from(sak.sakstype.beregningsdato()).minusYears(3),
                        tom = Year.from(sak.sakstype.beregningsdato()).minusYears(1)
                    )
                )
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
                løsning.lagreInntekter(sak.inntektshistorikk)

                sak.vedtak = sak.sakstype.opprettVedtak(
                    sak.inntektshistorikk,
                    fødselsdato
                )

                //Vente på sykepenger etter totrinnskontroll, og ikke her?
                sak.tilstand(VedtakFattet, løsning)
            }
        }

        object AvventerKvalitetssikring : Tilstand(Tilstandsnavn.AVVENTER_KVALITETSSIKRING) {

            override fun håndterKvalitetssikring(sak: Sak, kvalitetssikring: KvalitetssikringParagraf_11_2) {
                sak.sakstype.håndterKvalitetssikring(kvalitetssikring)
                vurderNesteTilstand(sak, kvalitetssikring)
            }

            override fun toDto(sak: Sak) = DtoSak(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
                sak.søknadstidspunkt = dtoSak.søknadstidspunkt
                sak.vurderingsdato = dtoSak.vurderingsdato
                val dtoVedtak = requireNotNull(dtoSak.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }

            // TODO: Her må vi sjekke tilstander på en annen måte
            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                when {
                    sak.sakstype.erAlleOppfylt() ->
                        sak.tilstand(VedtakFattet, hendelse)
                    sak.sakstype.erNoenIkkeOppfylt() ->
                        sak.tilstand(VedtakFattet, hendelse)
                }
            }
        }

        object VenterSykepenger : Tilstand(Tilstandsnavn.VENTER_SYKEPENGER) {

            override fun toDto(sak: Sak) = DtoSak(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
                sak.søknadstidspunkt = dtoSak.søknadstidspunkt
                sak.vurderingsdato = dtoSak.vurderingsdato
                val dtoVedtak = requireNotNull(dtoSak.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }
        }

        object VedtakFattet : Tilstand(Tilstandsnavn.VEDTAK_FATTET) {

            override fun toDto(sak: Sak) = DtoSak(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
                sak.søknadstidspunkt = dtoSak.søknadstidspunkt
                sak.vurderingsdato = dtoSak.vurderingsdato
                val dtoVedtak = requireNotNull(dtoSak.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }
        }

        object IkkeOppfylt : Tilstand(Tilstandsnavn.IKKE_OPPFYLT) {
            override fun onEntry(sak: Sak, hendelse: Hendelse) = hendelse.kansellerAlleBehov()
        }
    }

    private fun toDto() = tilstand.toDto(this)

    internal companion object {
        private val log = LoggerFactory.getLogger("sak")

        internal fun Iterable<Sak>.toDto() = map { sak -> sak.toDto() }

        internal fun gjenopprett(dtoSak: DtoSak): Sak = Sak(
            saksid = dtoSak.saksid,
            tilstand = when (Tilstand.Tilstandsnavn.valueOf(dtoSak.tilstand)) {
                Tilstand.Tilstandsnavn.START -> Tilstand.Start
                Tilstand.Tilstandsnavn.SØKNAD_MOTTATT -> Tilstand.SøknadMottatt
                Tilstand.Tilstandsnavn.BEREGN_INNTEKT -> Tilstand.BeregnInntekt
                Tilstand.Tilstandsnavn.AVVENTER_KVALITETSSIKRING -> Tilstand.AvventerKvalitetssikring
                Tilstand.Tilstandsnavn.VENTER_SYKEPENGER -> Tilstand.VenterSykepenger
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
