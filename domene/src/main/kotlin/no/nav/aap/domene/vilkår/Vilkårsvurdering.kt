package no.nav.aap.domene.vilkår

import no.nav.aap.domene.beregning.Yrkesskade
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.*
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

    internal fun erOppfylt(): Boolean = tilstand.erOppfylt()
    internal fun erIkkeOppfylt(): Boolean = tilstand.erIkkeOppfylt()

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) =
        callWithReceiver {
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

    internal fun håndterLøsning(løsning: LøsningParagraf_11_5_yrkesskade) = callWithReceiver {
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

    protected fun toDto(): DtoVilkårsvurdering = callWithReceiver {
        tilstand.toDto(this)
    }

    //FIXME: Noe skurr med denne her
    internal fun yrkesskade() = callWithReceiver {
        tilstand.yrkesskade(this)
    }

    internal fun beregningsdato() = callWithReceiver { tilstand.beregningsdato(this) }

    internal sealed class Tilstand<PARAGRAF : Vilkårsvurdering<PARAGRAF>>(
        protected val tilstandsnavn: Tilstandsnavn,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        enum class Tilstandsnavn {
            IKKE_VURDERT,
            SØKNAD_MOTTATT,
            MANUELL_VURDERING_TRENGS,
            OPPFYLT_MASKINELT,
            IKKE_OPPFYLT_MASKINELT,
            OPPFYLT_MANUELT,
            IKKE_OPPFYLT_MANUELT,
            IKKE_RELEVANT,
        }

        internal open fun onEntry(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}

        internal open fun gjenopprettTilstand(
            vilkårsvurdering: PARAGRAF,
            dtoVilkårsvurdering: DtoVilkårsvurdering
        ) {
        }

        internal abstract fun toDto(vilkårsvurdering: PARAGRAF): DtoVilkårsvurdering

        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

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
            løsning: LøsningParagraf_11_5_yrkesskade
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

        //FIXME: Noe skurr med denne også
        internal open fun yrkesskade(paragraf1122: PARAGRAF): Yrkesskade {
            error("Kun for 11-22") //FIXME
        }

        internal open fun beregningsdato(vilkårsvurdering: PARAGRAF): LocalDate? = null //TODO("Kun for 11-19")

        internal abstract class IkkeVurdert<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
            erOppfylt = false,
            erIkkeOppfylt = false
        )

        internal abstract class SøknadMottatt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.SØKNAD_MOTTATT,
            erOppfylt = false,
            erIkkeOppfylt = false
        )

        internal abstract class ManuellVurderingTrengs<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.MANUELL_VURDERING_TRENGS,
            erOppfylt = false,
            erIkkeOppfylt = false
        )

        internal abstract class OppfyltMaskinelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        )

        internal abstract class IkkeOppfyltMaskinelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        )

        internal abstract class OppfyltManuelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT,
            erOppfylt = true,
            erIkkeOppfylt = false
        )

        internal abstract class IkkeOppfyltManuelt<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT,
            erOppfylt = false,
            erIkkeOppfylt = true
        )

        internal abstract class IkkeRelevant<PARAGRAF : Vilkårsvurdering<PARAGRAF>> : Tilstand<PARAGRAF>(
            tilstandsnavn = Tilstandsnavn.IKKE_RELEVANT,
            erOppfylt = true,
            erIkkeOppfylt = false
        )
    }


    internal companion object {
        private val log = LoggerFactory.getLogger("Vilkårsvurdering")

        internal fun Iterable<Vilkårsvurdering<*>>.erAlleOppfylt() = all { it.erOppfylt() }
        internal fun Iterable<Vilkårsvurdering<*>>.erNoenIkkeOppfylt() = any { it.erIkkeOppfylt() }
        internal fun Iterable<Vilkårsvurdering<*>>.toDto() = map { it.toDto() }

        internal fun gjenopprett(dtoVilkårsvurdering: DtoVilkårsvurdering) =
            when (enumValueOf<Paragraf>(dtoVilkårsvurdering.paragraf)) {
                Paragraf.MEDLEMSKAP_YRKESSKADE ->
                    gjenopprett(dtoVilkårsvurdering, MedlemskapYrkesskade.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_2 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_2.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_3 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_3.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_4 -> {
                    dtoVilkårsvurdering.ledd.map<String, Ledd> { enumValueOf(it) }.let { ledd ->
                        when (ledd) {
                            listOf(Ledd.LEDD_1) ->
                                gjenopprett(dtoVilkårsvurdering, Paragraf_11_4FørsteLedd.Companion::gjenopprett)
                            listOf(Ledd.LEDD_2, Ledd.LEDD_3) ->
                                gjenopprett(dtoVilkårsvurdering, Paragraf_11_4AndreOgTredjeLedd.Companion::gjenopprett)
                            else -> null.also { log.warn("Paragraf ${dtoVilkårsvurdering.paragraf} Ledd $ledd not implemented") }
                        }
                    }
                }
                Paragraf.PARAGRAF_11_5 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_5.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_5_YRKESSKADE ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_5_yrkesskade.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_6 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_6.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_12 -> {
                    dtoVilkårsvurdering.ledd.map<String, Ledd> { enumValueOf(it) }.let { ledd ->
                        when (ledd) {
                            listOf(Ledd.LEDD_1) ->
                                gjenopprett(dtoVilkårsvurdering, Paragraf_11_12FørsteLedd.Companion::gjenopprett)
                            else -> null.also { log.warn("Paragraf ${dtoVilkårsvurdering.paragraf} Ledd $ledd not implemented") }
                        }
                    }
                }
                Paragraf.PARAGRAF_11_14 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_14.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_19 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_19.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_22 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_22.Companion::gjenopprett)
                Paragraf.PARAGRAF_11_29 ->
                    gjenopprett(dtoVilkårsvurdering, Paragraf_11_29.Companion::gjenopprett)
            }

        private inline fun <PARAGRAF : Vilkårsvurdering<PARAGRAF>> gjenopprett(
            dtoVilkårsvurdering: DtoVilkårsvurdering,
            gjenopprettParagraf: (UUID, Tilstand.Tilstandsnavn) -> PARAGRAF
        ): PARAGRAF {
            val paragraf = gjenopprettParagraf(
                dtoVilkårsvurdering.vilkårsvurderingsid,
                enumValueOf(dtoVilkårsvurdering.tilstand)
            )
            paragraf.tilstand.gjenopprettTilstand(paragraf, dtoVilkårsvurdering)
            return paragraf
        }
    }
}
