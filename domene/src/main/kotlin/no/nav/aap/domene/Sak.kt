package no.nav.aap.domene

import no.nav.aap.domene.Sakstype.Companion.toDto
import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoSak
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.BehovInntekter
import no.nav.aap.visitor.SøkerVisitor
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
    private lateinit var vurderingAvBeregningsdato: VurderingAvBeregningsdato
    private lateinit var vurderingsdato: LocalDate
    private lateinit var søknadstidspunkt: LocalDateTime
    private lateinit var vedtak: Vedtak

    constructor() : this(UUID.randomUUID(), Tilstand.Start, mutableListOf(), Inntektshistorikk())

    fun accept(visitor: SøkerVisitor) {
        visitor.preVisitSak(saksid, vurderingsdato, søknadstidspunkt)
        tilstand.accept(visitor)
        sakstyper.forEach { it.accept(visitor) }
        inntektshistorikk.accept(visitor)
        vurderingAvBeregningsdato.accept(visitor)
        vedtak.accept(visitor)
        visitor.postVisitSak(saksid, vurderingsdato, søknadstidspunkt)
    }

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato) = tilstand.håndterSøknad(this, søknad, fødselsdato)
    internal fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_2) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_3) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_5) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_5_yrkesskade) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_22) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningVurderingAvBeregningsdato) = tilstand.håndterLøsning(this, løsning)
    internal fun håndterLøsning(løsning: LøsningInntekter, fødselsdato: Fødselsdato) = tilstand.håndterLøsning(this, løsning, fødselsdato)

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

        abstract fun accept(visitor: SøkerVisitor)

        open fun onEntry(sak: Sak, hendelse: Hendelse) {}
        open fun onExit(sak: Sak, hendelse: Hendelse) {}

        private fun logUventetLøsning() = log.info("Forventet ikke søknad i tilstand ${tilstandsnavn.name}")

        open fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningMaskinellMedlemskapYrkesskade) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningManuellMedlemskapYrkesskade) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_2) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_3) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_4AndreOgTredjeLedd) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_5_yrkesskade) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_6) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_12FørsteLedd) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_22) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningParagraf_11_29) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) = logUventetLøsning()
        open fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) = logUventetLøsning()

        open fun toDto(sak: Sak) = DtoSak(
            saksid = sak.saksid,
            tilstand = tilstandsnavn.name,
            sakstyper = sak.sakstyper.toDto(),
            vurderingsdato = sak.vurderingsdato, // ALLTID SATT
            vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
            søknadstidspunkt = sak.søknadstidspunkt,
            vedtak = null
        )

        open fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
            sak.søknadstidspunkt = dtoSak.søknadstidspunkt
            sak.vurderingsdato = dtoSak.vurderingsdato
            sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
        }

        object Start : Tilstand(Tilstandsnavn.START) {
            override fun accept(visitor: SøkerVisitor) = visitor.visitSakTilstandStart()

            override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato) {
                sak.søknadstidspunkt = LocalDateTime.now()
                sak.vurderingsdato = LocalDate.now()

                opprettLøype(sak, søknad)
                sak.sakstype.håndterSøknad(søknad, fødselsdato, sak.vurderingsdato)

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
            override fun accept(visitor: SøkerVisitor) = visitor.visitSakTilstandSøknadMottatt()

            override fun håndterLøsning(sak: Sak, løsning: LøsningMaskinellMedlemskapYrkesskade) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningManuellMedlemskapYrkesskade) {
                sak.sakstype.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

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

            override fun håndterLøsning(sak: Sak, løsning: LøsningVurderingAvBeregningsdato) {
                sak.vurderingAvBeregningsdato.håndterLøsning(løsning)
                vurderNesteTilstand(sak, løsning)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                when {
                    sak.sakstype.erAlleOppfylt() && sak.vurderingAvBeregningsdato.erFerdig() -> sak.tilstand(BeregnInntekt, hendelse)
                    sak.sakstype.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt, hendelse)
                }
            }
        }

        object BeregnInntekt : Tilstand(Tilstandsnavn.BEREGN_INNTEKT) {
            override fun accept(visitor: SøkerVisitor) = visitor.visitSakTilstandBeregnInntekt()

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

                sak.vedtak = sak.sakstype.opprettVedtak(
                    sak.inntektshistorikk,
                    sak.vurderingAvBeregningsdato.beregningsdato(),
                    fødselsdato
                )

                sak.tilstand(VedtakFattet, løsning)
            }
        }

        object VedtakFattet : Tilstand(Tilstandsnavn.VEDTAK_FATTET) {
            override fun accept(visitor: SøkerVisitor) = visitor.visitSakTilstandVedtakFattet()

            override fun toDto(sak: Sak) = DtoSak(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                vurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.toDto(),
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, dtoSak: DtoSak) {
                sak.søknadstidspunkt = dtoSak.søknadstidspunkt
                sak.vurderingsdato = dtoSak.vurderingsdato
                sak.vurderingAvBeregningsdato = VurderingAvBeregningsdato.gjenopprett(dtoSak.vurderingAvBeregningsdato)
                val dtoVedtak = requireNotNull(dtoSak.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }
        }

        object IkkeOppfylt : Tilstand(Tilstandsnavn.IKKE_OPPFYLT) {
            override fun accept(visitor: SøkerVisitor) = visitor.visitSakTilstandIkkeOppfylt()
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
