package no.nav.aap.domene

import no.nav.aap.domene.Sakstype.Companion.toDto
import no.nav.aap.domene.beregning.Inntektshistorikk
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.domene.visitor.BeregningsdatoVisitor
import no.nav.aap.domene.visitor.KvalitetssikretVisitor
import no.nav.aap.domene.visitor.OppfyltVisitor
import no.nav.aap.domene.visitor.VirkningsdatoVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.BehovInntekter
import no.nav.aap.hendelse.behov.BehovIverksettVedtak
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import no.nav.aap.modellapi.SakModellApi
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

    internal fun <T : Hendelse> håndterLøsning(løsning: T, håndter: Vilkårsvurdering<*, *>.(T) -> Unit) {
        tilstand.håndterLøsning(this, løsning, håndter)
    }

    internal fun håndterLøsning(løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
        tilstand.håndterLøsning(this, løsning, fødselsdato)
    }

    internal fun <T : Hendelse> håndterKvalitetssikring(kvalitetssikring: T, håndter: Vilkårsvurdering<*, *>.(T) -> Unit) {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring, håndter)
    }

    internal fun håndterLøsning(løsning: LøsningSykepengedager) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterIverksettelse(iverksettelseAvVedtak: IverksettelseAvVedtak) {
        tilstand.håndterIverksettelse(this, iverksettelseAvVedtak)
    }

    private fun tilstand(nyTilstand: Tilstand, hendelse: Hendelse) {
//        println("Går fra $tilstand til $nyTilstand")
        nyTilstand.onExit(this, hendelse)
        tilstand = nyTilstand
        tilstand.onEntry(this, hendelse)
    }

    private sealed class Tilstand(
        protected val tilstandsnavn: Tilstandsnavn
    ) {
        enum class Tilstandsnavn {
            START,
            AVVENTER_VURDERING,
            BEREGN_INNTEKT,
            AVVENTER_KVALITETSSIKRING,
            VEDTAK_FATTET,
            VENTER_SYKEPENGER,
            VEDTAK_IVERKSATT,
            IKKE_OPPFYLT,
        }

        open fun onEntry(sak: Sak, hendelse: Hendelse) {}
        open fun onExit(sak: Sak, hendelse: Hendelse) {}
        open fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato) {
            log.info("Forventet ikke søknad i tilstand ${tilstandsnavn.name}")
        }

        open fun <T : Hendelse> håndterLøsning(sak: Sak, løsning: T, håndterLøsning: Vilkårsvurdering<*, *>.(T) -> Unit) {
            log.debug("Forventet ikke løsning i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
            log.debug("Forventet ikke løsning på inntekter i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterLøsning(sak: Sak, løsning: LøsningSykepengedager) {
            log.debug("Forventet ikke løsning på sykepengedager i tilstand ${tilstandsnavn.name}")
        }

        open fun <T : Hendelse> håndterKvalitetssikring(
            sak: Sak,
            kvalitetssikring: T,
            håndterKvalitetssikring: Vilkårsvurdering<*, *>.(T) -> Unit
        ) {
            log.debug("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        open fun håndterIverksettelse(sak: Sak, iverksettelseAvVedtak: IverksettelseAvVedtak) {
            log.debug("Forventet ikke iverksettelse i tilstand ${tilstandsnavn.name}")
        }

        open fun toDto(sak: Sak) = SakModellApi(
            saksid = sak.saksid,
            tilstand = tilstandsnavn.name,
            sakstyper = sak.sakstyper.toDto(),
            vurderingsdato = sak.vurderingsdato, // ALLTID SATT
            søknadstidspunkt = sak.søknadstidspunkt,
            vedtak = null
        )

        open fun gjenopprettTilstand(sak: Sak, sakModellApi: SakModellApi) {
            sak.søknadstidspunkt = sakModellApi.søknadstidspunkt
            sak.vurderingsdato = sakModellApi.vurderingsdato
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
                    OppfyltVisitor().apply(sak.sakstype::accept).erIkkeOppfylt -> sak.tilstand(IkkeOppfylt, søknad)
                    else -> sak.tilstand(AvventerVurdering, søknad)
                }
            }
        }

        object AvventerVurdering : Tilstand(Tilstandsnavn.AVVENTER_VURDERING) {
            override fun <T : Hendelse> håndterLøsning(
                sak: Sak,
                løsning: T,
                håndterLøsning: Vilkårsvurdering<*, *>.(T) -> Unit
            ) {
                sak.sakstype.håndter(løsning, håndterLøsning)
                vurderNesteTilstand(sak, løsning)
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningSykepengedager) {
                håndterLøsning(sak, løsning, Vilkårsvurdering<*, *>::håndterLøsning)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                val visitor = OppfyltVisitor().apply(sak.sakstype::accept)
                when {
                    visitor.erOppfylt -> sak.tilstand(BeregnInntekt, hendelse)
                    visitor.erIkkeOppfylt -> sak.tilstand(IkkeOppfylt, hendelse)
                }
            }

            override fun <T : Hendelse> håndterKvalitetssikring(
                sak: Sak,
                kvalitetssikring: T,
                håndterKvalitetssikring: Vilkårsvurdering<*, *>.(T) -> Unit
            ) {
                sak.sakstype.håndter(kvalitetssikring, håndterKvalitetssikring)
            }
        }

        object BeregnInntekt : Tilstand(Tilstandsnavn.BEREGN_INNTEKT) {

            override fun onEntry(sak: Sak, hendelse: Hendelse) {
                val beregningsdato = BeregningsdatoVisitor().apply(sak.sakstype::accept).beregningsdato
                hendelse.opprettBehov(
                    BehovInntekter(
                        fom = Year.from(beregningsdato).minusYears(3),
                        tom = Year.from(beregningsdato).minusYears(1)
                    )
                )
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningInntekter, fødselsdato: Fødselsdato) {
                løsning.lagreInntekter(sak.inntektshistorikk)

                val virkningsdato = VirkningsdatoVisitor().apply(sak.sakstype::accept).virkningsdato

                sak.vedtak = sak.sakstype.opprettVedtak(
                    inntektshistorikk = sak.inntektshistorikk,
                    fødselsdato = fødselsdato,
                    virkningsdato = virkningsdato
                )

                sak.tilstand(AvventerKvalitetssikring, løsning)
            }

            override fun <T : Hendelse> håndterLøsning(
                sak: Sak,
                løsning: T,
                håndterLøsning: Vilkårsvurdering<*, *>.(T) -> Unit
            ) {
                sak.sakstype.håndter(løsning, håndterLøsning)
            }
        }

        object AvventerKvalitetssikring : Tilstand(Tilstandsnavn.AVVENTER_KVALITETSSIKRING) {

//            override fun <T : Hendelse> håndterLøsning(
//                sak: Sak,
//                løsning: T,
//                håndterLøsning: Vilkårsvurdering<*, *>.(T) -> Unit
//            ) {
//                sak.sakstype.håndter(løsning, håndterLøsning)
//                sak.tilstand(BeregnInntekt, løsning)
//            }

            override fun <T : Hendelse> håndterKvalitetssikring(
                sak: Sak,
                kvalitetssikring: T,
                håndterKvalitetssikring: Vilkårsvurdering<*, *>.(T) -> Unit
            ) {
                sak.sakstype.håndter(kvalitetssikring, håndterKvalitetssikring)
                vurderNesteTilstand(sak, kvalitetssikring)
            }

            override fun toDto(sak: Sak) = SakModellApi(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, sakModellApi: SakModellApi) {
                sak.søknadstidspunkt = sakModellApi.søknadstidspunkt
                sak.vurderingsdato = sakModellApi.vurderingsdato
                val dtoVedtak = requireNotNull(sakModellApi.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                val visitor = KvalitetssikretVisitor().apply(sak.sakstype::accept)
                when {
                    visitor.erKvalitetssikret ->
                        sak.tilstand(VedtakFattet, hendelse)

                    visitor.erIkkeIKvalitetssikring ->
                        sak.tilstand(AvventerVurdering, hendelse)
                }
            }
        }

        object VedtakFattet : Tilstand(Tilstandsnavn.VEDTAK_FATTET) {

            override fun onEntry(sak: Sak, hendelse: Hendelse) {
                sak.sakstype.lagSnapshot(sak.vedtak)
            }

            override fun håndterIverksettelse(sak: Sak, iverksettelseAvVedtak: IverksettelseAvVedtak) {
                vurderNesteTilstand(sak, iverksettelseAvVedtak)
            }

            override fun toDto(sak: Sak) = SakModellApi(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, sakModellApi: SakModellApi) {
                sak.søknadstidspunkt = sakModellApi.søknadstidspunkt
                sak.vurderingsdato = sakModellApi.vurderingsdato
                val dtoVedtak = requireNotNull(sakModellApi.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }

            private fun vurderNesteTilstand(sak: Sak, hendelse: Hendelse) {
                val visitor = VirkningsdatoVisitor().apply(sak.sakstype::accept)
                when (visitor.bestemmesAv) {
                    LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger -> sak.tilstand(VenterSykepenger, hendelse)
                    else -> sak.tilstand(VedtakIverksatt, hendelse)
                }
            }
        }

        object VenterSykepenger : Tilstand(Tilstandsnavn.VENTER_SYKEPENGER) {
            override fun onEntry(sak: Sak, hendelse: Hendelse) {
                hendelse.opprettBehov(Behov_8_48AndreLedd())
            }

            override fun håndterLøsning(sak: Sak, løsning: LøsningSykepengedager) {
                if (løsning.gjenståendeSykedager() == 0) {
                    sak.tilstand(VedtakIverksatt, løsning)
                }
            }

            override fun toDto(sak: Sak) = SakModellApi(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, sakModellApi: SakModellApi) {
                sak.søknadstidspunkt = sakModellApi.søknadstidspunkt
                sak.vurderingsdato = sakModellApi.vurderingsdato
                val dtoVedtak = requireNotNull(sakModellApi.vedtak)
                sak.vedtak = Vedtak.gjenopprett(dtoVedtak)
            }
        }

        object VedtakIverksatt : Tilstand(Tilstandsnavn.VEDTAK_IVERKSATT) {
            override fun onEntry(sak: Sak, hendelse: Hendelse) {
                hendelse.opprettBehov(BehovIverksettVedtak(sak.vedtak))
            }

            override fun <T : Hendelse> håndterLøsning(
                sak: Sak,
                løsning: T,
                håndterLøsning: Vilkårsvurdering<*, *>.(T) -> Unit
            ) {
                sak.sakstype.håndter(løsning, håndterLøsning)
                sak.tilstand(BeregnInntekt, løsning)
            }

            override fun toDto(sak: Sak) = SakModellApi(
                saksid = sak.saksid,
                tilstand = tilstandsnavn.name,
                sakstyper = sak.sakstyper.toDto(),
                vurderingsdato = sak.vurderingsdato, // ALLTID SATT
                søknadstidspunkt = sak.søknadstidspunkt,
                vedtak = sak.vedtak.toDto()
            )

            override fun gjenopprettTilstand(sak: Sak, sakModellApi: SakModellApi) {
                sak.søknadstidspunkt = sakModellApi.søknadstidspunkt
                sak.vurderingsdato = sakModellApi.vurderingsdato
                val dtoVedtak = requireNotNull(sakModellApi.vedtak)
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

        internal fun gjenopprett(sakModellApi: SakModellApi): Sak = Sak(
            saksid = sakModellApi.saksid,
            tilstand = when (Tilstand.Tilstandsnavn.valueOf(sakModellApi.tilstand)) {
                Tilstand.Tilstandsnavn.START -> Tilstand.Start
                Tilstand.Tilstandsnavn.AVVENTER_VURDERING -> Tilstand.AvventerVurdering
                Tilstand.Tilstandsnavn.BEREGN_INNTEKT -> Tilstand.BeregnInntekt
                Tilstand.Tilstandsnavn.AVVENTER_KVALITETSSIKRING -> Tilstand.AvventerKvalitetssikring
                Tilstand.Tilstandsnavn.VEDTAK_FATTET -> Tilstand.VedtakFattet
                Tilstand.Tilstandsnavn.VENTER_SYKEPENGER -> Tilstand.VenterSykepenger
                Tilstand.Tilstandsnavn.VEDTAK_IVERKSATT -> Tilstand.VedtakIverksatt
                Tilstand.Tilstandsnavn.IKKE_OPPFYLT -> Tilstand.IkkeOppfylt
            },
            sakstyper = sakModellApi.sakstyper.map(Sakstype::gjenopprett).toMutableList(),
            inntektshistorikk = Inntektshistorikk()
        ).apply {
            this.tilstand.gjenopprettTilstand(this, sakModellApi)
        }
    }
}
