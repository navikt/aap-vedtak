package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

internal abstract class Vilkårsvurdering<PARAGRAF : Vilkårsvurdering<PARAGRAF>>(
    protected val vilkårsvurderingsid: UUID,
    protected val paragraf: Paragraf,
    protected val ledd: List<Ledd>,
    private var tilstand: Tilstand<PARAGRAF>,
) {
    internal constructor(
        vilkårsvurderingsid: UUID,
        paragraf: Paragraf,
        ledd: Ledd,
        tilstand: Tilstand<PARAGRAF>,
    ) : this(vilkårsvurderingsid, paragraf, listOf(ledd), tilstand)

    internal enum class Paragraf {
        MEDLEMSKAP_YRKESSKADE,
        PARAGRAF_11_2,
        PARAGRAF_11_3,
        PARAGRAF_11_4,
        PARAGRAF_11_5,
        PARAGRAF_11_5_YRKESSKADE,
        PARAGRAF_11_6,
        PARAGRAF_11_12,
        PARAGRAF_11_14,
        PARAGRAF_11_19,
        PARAGRAF_11_22,
        PARAGRAF_11_29
    }

    internal enum class Ledd {
        LEDD_1,
        LEDD_2,
        LEDD_3;

        operator fun plus(other: Ledd) = listOf(this, other)
    }

    internal fun accept(visitor: VilkårsvurderingVisitor) = callWithReceiver {
        tilstand.accept(this, visitor)
    }

    protected open fun onEntry(hendelse: Hendelse) = callWithReceiver {
        tilstand.onEntry(this, hendelse)
    }

    protected open fun onExit(hendelse: Hendelse) = callWithReceiver {
        tilstand.onExit(this, hendelse)
    }

    protected fun tilstand(nyTilstand: Tilstand<PARAGRAF>, hendelse: Hendelse) {
        onExit(hendelse)
        this.tilstand = nyTilstand
        onEntry(hendelse)
    }

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) = callWithReceiver {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    protected abstract fun <T> callWithReceiver(block: PARAGRAF.() -> T): T

    internal fun håndterLøsning(løsning: LøsningMaskinellParagraf_11_2) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningManuellParagraf_11_2) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningManuellMedlemskapYrkesskade) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_3) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_4AndreOgTredjeLedd) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5Yrkesskade) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_12FørsteLedd) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_19) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_22) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_29) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringMedlemskapYrkesskade) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_2) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_3) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd) =
        callWithReceiver {
            tilstand.håndterKvalitetssikring(this, kvalitetssikring)
        }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_6) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_12FørsteLedd) =
        callWithReceiver {
            tilstand.håndterKvalitetssikring(this, kvalitetssikring)
        }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_19) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_22) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_29) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }


    protected fun toDto(): VilkårsvurderingModellApi = callWithReceiver {
        tilstand.toDto(this)
    }

    internal sealed class Tilstand<PARAGRAF : Vilkårsvurdering<PARAGRAF>>(
        protected val tilstandsnavn: Tilstandsnavn,
        protected val vurdertMaskinelt: Boolean
    ) {
        enum class Tilstandsnavn {
            IKKE_VURDERT,
            SØKNAD_MOTTATT,
            MANUELL_VURDERING_TRENGS,
            OPPFYLT_MASKINELT,
            OPPFYLT_MASKINELT_KVALITETSSIKRET,
            IKKE_OPPFYLT_MASKINELT,
            IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET,
            OPPFYLT_MANUELT,
            OPPFYLT_MANUELT_KVALITETSSIKRET,
            IKKE_OPPFYLT_MANUELT,
            IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET,
            IKKE_RELEVANT,
        }

        internal abstract fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor)
        protected open fun subAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {}

        internal open fun onEntry(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}

        internal open fun gjenopprettTilstand(
            vilkårsvurdering: PARAGRAF,
            vilkårsvurderingModellApi: VilkårsvurderingModellApi
        ) {
        }

        internal abstract fun toDto(vilkårsvurdering: PARAGRAF): VilkårsvurderingModellApi

        internal open fun håndterSøknad(
            vilkårsvurdering: PARAGRAF,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningMaskinellParagraf_11_2
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningManuellParagraf_11_2
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningMaskinellMedlemskapYrkesskade
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningManuellMedlemskapYrkesskade
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_3
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_4AndreOgTredjeLedd
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_5
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_5Yrkesskade
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_6
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_12FørsteLedd
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_19
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_22
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterLøsning(
            vilkårsvurdering: PARAGRAF,
            løsning: LøsningParagraf_11_29
        ) {
            log.info("Oppgave skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringMedlemskapYrkesskade
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_2
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_3
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLedd
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_5
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_6
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_12FørsteLedd
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_19
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_22
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_29
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal abstract class IkkeVurdert<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            vurdertMaskinelt = false
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeVurdert()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class SøknadMottatt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            vurdertMaskinelt = false
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitSøknadMottatt()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class ManuellVurderingTrengs<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.MANUELL_VURDERING_TRENGS,
            vurdertMaskinelt = false
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitManuellVurderingTrengs()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltMaskinelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT,
            vurdertMaskinelt = true
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltMaskinelt()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltMaskineltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF>> :
            Tilstand<PARAGRAF>(
                tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET,
                vurdertMaskinelt = true
            ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltMaskineltKvalitetssikret()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltMaskinelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MASKINELT,
            vurdertMaskinelt = true
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltMaskinelt()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltMaskineltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF>> :
            Tilstand<PARAGRAF>(
                tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET,
                vurdertMaskinelt = true
            ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltMaskineltKvalitetssikret()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltManuelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT,
            vurdertMaskinelt = false
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltManuelt()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltManueltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF>> :
            Tilstand<PARAGRAF>(
                tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET,
                vurdertMaskinelt = false
            ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltManueltKvalitetssikret()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltManuelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            vurdertMaskinelt = false
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltManuelt()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltManueltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF>> :
            Tilstand<PARAGRAF>(
                tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET,
                vurdertMaskinelt = false
            ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltManueltKvalitetssikret()
                subAccept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeRelevant<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_RELEVANT,
            vurdertMaskinelt = false
        ) {
            final override fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeRelevant()
                subAccept(vilkårsvurdering, visitor)
            }
        }
    }


    internal companion object {
        private val log = LoggerFactory.getLogger("Vilkårsvurdering")

        internal fun Iterable<Vilkårsvurdering<*>>.toDto() = map { it.toDto() }

        internal fun gjenopprett(vilkårsvurderingModellApi: VilkårsvurderingModellApi) =
            when (enumValueOf<Paragraf>(vilkårsvurderingModellApi.paragraf)) {
                Paragraf.MEDLEMSKAP_YRKESSKADE ->
                    gjenopprett(vilkårsvurderingModellApi, MedlemskapYrkesskade.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_2 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_2.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_3 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_3.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_4 -> {
                    vilkårsvurderingModellApi.ledd.map<String, Ledd> { enumValueOf(it) }.let { ledd ->
                        when (ledd) {
                            listOf(Ledd.LEDD_1) ->
                                gjenopprett(vilkårsvurderingModellApi, Paragraf_11_4FørsteLedd.Companion::gjenopprett)

                            listOf(Ledd.LEDD_2, Ledd.LEDD_3) ->
                                gjenopprett(
                                    vilkårsvurderingModellApi,
                                    Paragraf_11_4AndreOgTredjeLedd.Companion::gjenopprett
                                )

                            else -> null.also { log.warn("Paragraf ${vilkårsvurderingModellApi.paragraf} Ledd $ledd not implemented") }
                        }
                    }
                }

                Paragraf.PARAGRAF_11_5 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_5.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_5_YRKESSKADE ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_5_yrkesskade.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_6 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_6.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_12 -> {
                    vilkårsvurderingModellApi.ledd.map<String, Ledd> { enumValueOf(it) }.let { ledd ->
                        when (ledd) {
                            listOf(Ledd.LEDD_1) ->
                                gjenopprett(vilkårsvurderingModellApi, Paragraf_11_12FørsteLedd.Companion::gjenopprett)

                            else -> null.also { log.warn("Paragraf ${vilkårsvurderingModellApi.paragraf} Ledd $ledd not implemented") }
                        }
                    }
                }

                Paragraf.PARAGRAF_11_14 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_14.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_19 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_19.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_22 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_22.Companion::gjenopprett)

                Paragraf.PARAGRAF_11_29 ->
                    gjenopprett(vilkårsvurderingModellApi, Paragraf_11_29.Companion::gjenopprett)
            }

        private inline fun <PARAGRAF : Vilkårsvurdering<PARAGRAF>> gjenopprett(
            vilkårsvurderingModellApi: VilkårsvurderingModellApi,
            gjenopprettParagraf: (UUID, Tilstand.Tilstandsnavn) -> PARAGRAF
        ): PARAGRAF {
            val paragraf = gjenopprettParagraf(
                vilkårsvurderingModellApi.vilkårsvurderingsid,
                enumValueOf(vilkårsvurderingModellApi.tilstand)
            )
            paragraf.tilstand.gjenopprettTilstand(paragraf, vilkårsvurderingModellApi)
            return paragraf
        }
    }
}
