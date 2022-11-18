package no.nav.aap.domene.vilkår

import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.visitor.VilkårsvurderingVisitor
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6
import no.nav.aap.modellapi.*
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

internal abstract class Vilkårsvurdering<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi>(
    protected val vilkårsvurderingsid: UUID,
    protected val paragraf: Paragraf,
    protected val ledd: List<Ledd>,
    private var tilstand: Tilstand<PARAGRAF, PARAGRAF_MODELL_API>,
) {
    internal constructor(
        vilkårsvurderingsid: UUID,
        paragraf: Paragraf,
        ledd: Ledd,
        tilstand: Tilstand<PARAGRAF, PARAGRAF_MODELL_API>,
    ) : this(vilkårsvurderingsid, paragraf, listOf(ledd), tilstand)

    internal enum class Paragraf {
        MEDLEMSKAP_YRKESSKADE,
        PARAGRAF_8_48,
        PARAGRAF_11_2,
        PARAGRAF_11_3,
        PARAGRAF_11_4,
        PARAGRAF_11_5,
        PARAGRAF_11_5_YRKESSKADE,
        PARAGRAF_11_6,
        PARAGRAF_11_14,
        PARAGRAF_11_19,
        PARAGRAF_11_22,
        PARAGRAF_11_27,
        PARAGRAF_11_29,
        PARAGRAF_22_13,
    }

    internal enum class Ledd {
        LEDD_1,
        LEDD_2,
        LEDD_3;

        internal operator fun plus(other: Ledd) = listOf(this, other)
    }

    internal companion object {
        private val log = LoggerFactory.getLogger("Vilkårsvurdering")
        internal fun Iterable<Vilkårsvurdering<*, *>>.toDto() = map(Vilkårsvurdering<*, *>::toDto)
        internal fun Iterable<Vilkårsvurdering<*, *>>.lagSnapshot(vedtak: Vedtak) = forEach { it.lagSnapshot(vedtak) }
    }

    internal open fun lagSnapshot(vedtak: Vedtak) {}

    internal fun accept(visitor: VilkårsvurderingVisitor) = callWithReceiver {
        tilstand.preAccept(this, visitor)
    }

    protected open fun onEntry(hendelse: Hendelse) = callWithReceiver {
        tilstand.onEntry(this, hendelse)
    }

    protected open fun onExit(hendelse: Hendelse) = callWithReceiver {
        tilstand.onExit(this, hendelse)
    }

    protected fun tilstand(nyTilstand: Tilstand<PARAGRAF, PARAGRAF_MODELL_API>, hendelse: Hendelse) {
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

    internal fun håndterLøsning(løsning: LøsningSykepengedager) = callWithReceiver {
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

    internal fun håndterInnstilling(innstilling: InnstillingParagraf_11_6) = callWithReceiver {
        tilstand.håndterInnstilling(this, innstilling)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_6) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_22_13) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_19) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_22) = callWithReceiver {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsning: LøsningParagraf_11_27_FørsteLedd) = callWithReceiver {
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
        callWithReceiver { tilstand.håndterKvalitetssikring(this, kvalitetssikring) }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5Yrkesskade) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_6) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_22_13) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_19) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_22) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_27_FørsteLedd) =
        callWithReceiver { tilstand.håndterKvalitetssikring(this, kvalitetssikring) }

    internal fun håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_29) = callWithReceiver {
        tilstand.håndterKvalitetssikring(this, kvalitetssikring)
    }

    protected fun toDto(): VilkårsvurderingModellApi = callWithReceiver {
        tilstand.toDto(this)
    }

    internal fun gjenopprettTilstand(modellApi: PARAGRAF_MODELL_API) = callWithReceiver {
        tilstand.gjenopprettTilstand(this, modellApi)
    }

    internal sealed class Tilstand<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> private constructor(
        protected val tilstandsnavn: Tilstandsnavn,
        protected val vurdertMaskinelt: Boolean
    ) {
        enum class Tilstandsnavn {
            IKKE_VURDERT,
            AVVENTER_MASKINELL_VURDERING,
            AVVENTER_INNSTILLING,
            AVVENTER_MANUELL_VURDERING,
            OPPFYLT_MASKINELT_KVALITETSSIKRET,
            IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET,
            OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING,
            OPPFYLT_MANUELT_KVALITETSSIKRET,
            IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING,
            IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET,
            IKKE_RELEVANT,
        }

        internal abstract fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor)
        protected open fun accept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {}

        internal open fun onEntry(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}
        internal open fun onExit(vilkårsvurdering: PARAGRAF, hendelse: Hendelse) {}

        internal open fun gjenopprettTilstand(
            vilkårsvurdering: PARAGRAF,
            modellApi: PARAGRAF_MODELL_API
        ) {
        }

        internal abstract fun toDto(vilkårsvurdering: PARAGRAF): PARAGRAF_MODELL_API

        internal open fun håndterSøknad(
            vilkårsvurdering: PARAGRAF,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            log.info("Søknad skal ikke håndteres i tilstand $tilstandsnavn")
        }

        internal open fun håndterInnstilling(
            vilkårsvurdering: PARAGRAF,
            løsning: InnstillingParagraf_11_6
        ) {
            log.info("Innstilling skal ikke håndteres i tilstand $tilstandsnavn")
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
            løsning: LøsningSykepengedager
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
            løsning: LøsningParagraf_22_13
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
            løsning: LøsningParagraf_11_27_FørsteLedd
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
            kvalitetssikring: KvalitetssikringParagraf_22_13
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
            kvalitetssikring: KvalitetssikringParagraf_11_27_FørsteLedd
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal open fun håndterKvalitetssikring(
            vilkårsvurdering: PARAGRAF,
            kvalitetssikring: KvalitetssikringParagraf_11_29
        ) {
            log.info("Forventet ikke kvalitetssikring i tilstand ${tilstandsnavn.name}")
        }

        internal abstract class IkkeVurdert<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.IKKE_VURDERT,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeVurdert()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class AvventerMaskinellVurdering<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.AVVENTER_MASKINELL_VURDERING,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitAvventerMaskinellVurdering()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class AvventerInnstilling<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.AVVENTER_INNSTILLING,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitAvventerInnstilling()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class AvventerManuellVurdering<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.AVVENTER_MANUELL_VURDERING,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitAvventerManuellVurdering()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltMaskineltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET,
                vurdertMaskinelt = true
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltMaskineltKvalitetssikret()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltMaskineltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET,
                vurdertMaskinelt = true
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltMaskineltKvalitetssikret()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltManueltAvventerKvalitetssikring<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltManuelt()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class OppfyltManueltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitOppfyltManueltKvalitetssikret()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltManueltAvventerKvalitetssikring<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltManuelt()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeOppfyltManueltKvalitetssikret<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeOppfyltManueltKvalitetssikret()
                accept(vilkårsvurdering, visitor)
            }
        }

        internal abstract class IkkeRelevant<PARAGRAF : Vilkårsvurdering<PARAGRAF, PARAGRAF_MODELL_API>, PARAGRAF_MODELL_API : VilkårsvurderingModellApi> :
            Tilstand<PARAGRAF, PARAGRAF_MODELL_API>(
                tilstandsnavn = Tilstandsnavn.IKKE_RELEVANT,
                vurdertMaskinelt = false
            ) {
            final override fun preAccept(vilkårsvurdering: PARAGRAF, visitor: VilkårsvurderingVisitor) {
                visitor.visitIkkeRelevant()
                accept(vilkårsvurdering, visitor)
            }
        }
    }
}
