package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toFrontendSak
import no.nav.aap.domene.Vilkårsvurdering.Companion.erAlleOppfylt
import no.nav.aap.domene.Vilkårsvurdering.Companion.erNoenIkkeOppfylt
import no.nav.aap.domene.Vilkårsvurdering.Companion.toFrontendVilkårsvurdering
import no.nav.aap.domene.frontendView.FrontendOppgave
import no.nav.aap.domene.frontendView.FrontendSak
import no.nav.aap.domene.frontendView.FrontendVilkår
import no.nav.aap.domene.frontendView.FrontendVilkårsvurdering
import java.time.LocalDate

class Søker(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val lytter: Lytter
) {
    private val saker: MutableList<Sak> = mutableListOf()

    fun håndterSøknad(søknad: Søknad) {
        val sak = Sak(lytter)
        saker.add(sak)
        sak.håndterSøknad(søknad, fødselsdato)
    }

    fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_2) {
        saker.forEach { it.håndterOppgavesvar(oppgavesvar) }
    }

    fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_5) {
        saker.forEach { it.håndterOppgavesvar(oppgavesvar) }
    }

    private fun toFrontendSaker() =
        saker.toFrontendSak(
            personident = personident,
            fødselsdato = fødselsdato
        )

    companion object {
        fun Iterable<Søker>.toFrontendSaker() = flatMap(Søker::toFrontendSaker)
    }
}

interface Oppgave {
    fun tilFrontendOppgave(): FrontendOppgave
}

interface Lytter {
    fun sendOppgave(oppgave: Oppgave) {}
}

class Personident(
    private val ident: String
) {
    internal fun toFrontendPersonident() = ident
}

class Fødselsdato(private val dato: LocalDate) {
    private val `18ÅrsDagen`: LocalDate = this.dato.plusYears(18)
    private val `67ÅrsDagen`: LocalDate = this.dato.plusYears(67)

    internal fun erMellom18Og67År(vurderingsdato: LocalDate) = vurderingsdato in `18ÅrsDagen`..`67ÅrsDagen`

    internal fun toFrontendFødselsdato() = dato
}

internal class Sak(private val lytter: Lytter = object : Lytter {}) {
    private val vilkårsvurderinger: MutableList<Vilkårsvurdering> = mutableListOf()
    private lateinit var vurderingsdato: LocalDate

    internal fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato) {
        this.vurderingsdato = LocalDate.now()
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    internal fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_2) {
        tilstand.håndterOppgavesvar(this, oppgavesvar)
    }

    internal fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_5) {
        tilstand.håndterOppgavesvar(this, oppgavesvar)
    }

    private var tilstand: Tilstand = Start

    private fun tilstand(nyTilstand: Tilstand) {
        nyTilstand.onExit()
        tilstand = nyTilstand
        tilstand.onEntry()
    }

    private sealed interface Tilstand {
        val tilstandsnavn: Tilstandsnavn

        enum class Tilstandsnavn {
            START, SØKNAD_MOTTATT, IKKE_OPPFYLT, BEREGN_INNTEKT
        }

        fun onEntry() {}
        fun onExit() {}
        fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            error("Forventet ikke søknad i tilstand ${tilstandsnavn.name}")
        }

        fun håndterOppgavesvar(sak: Sak, oppgavesvar: OppgavesvarParagraf_11_2) {
            error("Forventet ikke oppgavesvar i tilstand ${tilstandsnavn.name}")
        }

        fun håndterOppgavesvar(sak: Sak, oppgavesvar: OppgavesvarParagraf_11_5) {
            error("Forventet ikke oppgavesvar i tilstand ${tilstandsnavn.name}")
        }

        fun toFrontendTilstand() = tilstandsnavn.name
    }

    private object Start : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.START
        override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            //opprett initielle vilkårsvurderinger
            sak.vilkårsvurderinger.add(Paragraf_11_2(sak.lytter))
            sak.vilkårsvurderinger.add(Paragraf_11_4FørsteLedd(sak.lytter))
            sak.vilkårsvurderinger.add(Paragraf_11_5(sak.lytter))
            sak.vilkårsvurderinger.forEach { it.håndterSøknad(søknad, fødselsdato, vurderingsdato) }

            vurderNestetilstand(sak)
        }

        private fun vurderNestetilstand(sak: Sak) {
            when {
                sak.vilkårsvurderinger.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt)
                else -> sak.tilstand(SøknadMottatt)
            }
        }
    }

    private object SøknadMottatt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.SØKNAD_MOTTATT
        override fun håndterOppgavesvar(sak: Sak, oppgavesvar: OppgavesvarParagraf_11_2) {
            sak.vilkårsvurderinger.forEach { it.håndterOppgavesvar(oppgavesvar) }
            vurderNesteTilstand(sak)
        }

        override fun håndterOppgavesvar(sak: Sak, oppgavesvar: OppgavesvarParagraf_11_5) {
            sak.vilkårsvurderinger.forEach { it.håndterOppgavesvar(oppgavesvar) }
            vurderNesteTilstand(sak)
        }

        private fun vurderNesteTilstand(sak: Sak) {
            when {
                sak.vilkårsvurderinger.erAlleOppfylt() -> sak.tilstand(BeregnInntekt)
                sak.vilkårsvurderinger.erNoenIkkeOppfylt() -> sak.tilstand(IkkeOppfylt)
            }
        }
    }

    private object BeregnInntekt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.BEREGN_INNTEKT
    }

    private object IkkeOppfylt : Tilstand {
        override val tilstandsnavn = Tilstand.Tilstandsnavn.IKKE_OPPFYLT
        override fun håndterSøknad(sak: Sak, søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
            error("Forventet ikke søknad i tilstand IkkeOppfylt")
        }
    }


    private fun toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) =
        FrontendSak(
            personident = personident.toFrontendPersonident(),
            fødselsdato = fødselsdato.toFrontendFødselsdato(),
            tilstand = tilstand.toFrontendTilstand(),
            vilkårsvurderinger = vilkårsvurderinger.toFrontendVilkårsvurdering()
        )

    internal companion object {
        internal fun Iterable<Sak>.toFrontendSak(personident: Personident, fødselsdato: Fødselsdato) = map {
            it.toFrontendSak(personident = personident, fødselsdato = fødselsdato)
        }
    }
}

class Søknad(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato
) {
    fun opprettSøker(lytter: Lytter = object : Lytter {}) = Søker(personident, fødselsdato, lytter)
}

internal abstract class Vilkårsvurdering(
    protected val lytter: Lytter,
    protected val paragraf: Paragraf,
    protected val ledd: List<Ledd>
) {
    internal constructor(
        lytter: Lytter,
        paragraf: Paragraf,
        ledd: Ledd
    ) : this(lytter, paragraf, listOf(ledd))

    internal enum class Paragraf {
        PARAGRAF_11_2, PARAGRAF_11_4, PARAGRAF_11_5
    }

    internal enum class Ledd {
        LEDD_1, LEDD_2;

        operator fun plus(other: Ledd) = listOf(this, other)
    }

    internal abstract fun erOppfylt(): Boolean
    internal abstract fun erIkkeOppfylt(): Boolean

    internal open fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {}
    internal open fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_2) {}
    internal open fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_5) {}

    private fun toFrontendVilkårsvurdering() =
        FrontendVilkårsvurdering(
            vilkår = FrontendVilkår(paragraf.name, ledd.map(Ledd::name)),
            tilstand = toFrontendTilstand()
        )

    protected abstract fun toFrontendTilstand(): String

    internal companion object {
        internal fun Iterable<Vilkårsvurdering>.erAlleOppfylt() = all(Vilkårsvurdering::erOppfylt)
        internal fun Iterable<Vilkårsvurdering>.erNoenIkkeOppfylt() = any(Vilkårsvurdering::erIkkeOppfylt)

        internal fun Iterable<Vilkårsvurdering>.toFrontendVilkårsvurdering() =
            map(Vilkårsvurdering::toFrontendVilkårsvurdering)
    }
}

internal class Paragraf_11_2(lytter: Lytter = object : Lytter {}) :
    Vilkårsvurdering(lytter, Paragraf.PARAGRAF_11_2, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var maskineltOppgavesvar: OppgavesvarParagraf_11_2
    private lateinit var manueltOppgavesvar: OppgavesvarParagraf_11_2

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand.onExit(this)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_2) {
        tilstand.vurderMedlemskap(this, oppgavesvar)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        private val name: String,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        internal open fun onEntry(vilkårsvurdering: Paragraf_11_2) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_2) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_2,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $name")
        }

        internal open fun vurderMedlemskap(
            vilkårsvurdering: Paragraf_11_2,
            oppgavesvar: OppgavesvarParagraf_11_2
        ) {
            error("Oppgave skal ikke håndteres i tilstand $name")
        }

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_2,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt)
            }
        }

        object SøknadMottatt : Tilstand(name = "SØKNAD_MOTTATT", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2) {
                //send ut oppgave for innhenting av maskinell medlemskapsvurdering
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                oppgavesvar: OppgavesvarParagraf_11_2
            ) {
                vilkårsvurdering.maskineltOppgavesvar = oppgavesvar
                when {
                    oppgavesvar.erMedlem() -> vilkårsvurdering.tilstand(Oppfylt)
                    oppgavesvar.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfylt)
                    else -> vilkårsvurdering.tilstand(ManuellVurderingTrengs)
                }
            }
        }

        object ManuellVurderingTrengs :
            Tilstand(name = "MANUELL_VURDERING_TRENGS", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_2) {
                //send ut oppgave for manuell vurdering av medlemskap
            }

            override fun vurderMedlemskap(
                vilkårsvurdering: Paragraf_11_2,
                oppgavesvar: OppgavesvarParagraf_11_2
            ) {
                vilkårsvurdering.manueltOppgavesvar = oppgavesvar
                when {
                    oppgavesvar.erMedlem() -> vilkårsvurdering.tilstand(Oppfylt)
                    oppgavesvar.erIkkeMedlem() -> vilkårsvurdering.tilstand(IkkeOppfylt)
                    else -> error("Veileder/saksbehandler må ta stilling til om bruker er medlem eller ikke")
                }
            }
        }

        object Oppfylt : Tilstand(
            name = "OPPFYLT",
            erOppfylt = true,
            erIkkeOppfylt = false
        )

        object IkkeOppfylt : Tilstand(
            name = "IKKE_OPPFYLT",
            erOppfylt = false,
            erIkkeOppfylt = true
        )

        internal fun toFrontendTilstand(): String = name
    }

    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
}

internal class Paragraf_11_4FørsteLedd(lytter: Lytter = object : Lytter {}) :
    Vilkårsvurdering(lytter, Paragraf.PARAGRAF_11_4, Ledd.LEDD_1) {
    private lateinit var fødselsdato: Fødselsdato
    private lateinit var vurderingsdato: LocalDate

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand = nyTilstand
    }

    private fun vurderAldersvilkår(fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        this.fødselsdato = fødselsdato
        this.vurderingsdato = vurderingsdato
        if (fødselsdato.erMellom18Og67År(vurderingsdato)) tilstand(Tilstand.Oppfylt)
        else tilstand(Tilstand.IkkeOppfylt)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        private val name: String,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal abstract fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_4FørsteLedd,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        )

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4FørsteLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.vurderAldersvilkår(fødselsdato, vurderingsdato)
            }
        }

        object Oppfylt : Tilstand(
            name = "OPPFYLT",
            erOppfylt = true,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4FørsteLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                error("Vilkår allerede vurdert til oppfylt. Forventer ikke ny søknad")
            }
        }

        object IkkeOppfylt : Tilstand(
            name = "IKKE_OPPFYLT",
            erOppfylt = false,
            erIkkeOppfylt = true
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_4FørsteLedd,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                error("Vilkår allerede vurdert til ikke oppfylt. Forventer ikke ny søknad")
            }
        }

        internal fun toFrontendTilstand(): String = name
    }

    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
}

internal class Paragraf_11_5(lytter: Lytter = object : Lytter {}) :
    Vilkårsvurdering(lytter, Paragraf.PARAGRAF_11_5, Ledd.LEDD_1 + Ledd.LEDD_2) {
    private lateinit var oppgavesvar: OppgavesvarParagraf_11_5
    private lateinit var nedsattArbeidsevnegrad: OppgavesvarParagraf_11_5.NedsattArbeidsevnegrad

    private var tilstand: Tilstand = Tilstand.IkkeVurdert

    private fun tilstand(nyTilstand: Tilstand) {
        this.tilstand.onExit(this)
        this.tilstand = nyTilstand
        nyTilstand.onEntry(this)
    }

    override fun håndterSøknad(søknad: Søknad, fødselsdato: Fødselsdato, vurderingsdato: LocalDate) {
        tilstand.håndterSøknad(this, søknad, fødselsdato, vurderingsdato)
    }

    override fun håndterOppgavesvar(oppgavesvar: OppgavesvarParagraf_11_5) {
        oppgavesvar.vurderNedsattArbeidsevne(this)
    }

    internal fun vurderNedsattArbeidsevne(
        oppgavesvar: OppgavesvarParagraf_11_5,
        nedsattArbeidsevnegrad: OppgavesvarParagraf_11_5.NedsattArbeidsevnegrad
    ) {
        tilstand.vurderNedsattArbeidsevne(this, oppgavesvar, nedsattArbeidsevnegrad)
    }

    override fun erOppfylt() = tilstand.erOppfylt()
    override fun erIkkeOppfylt() = tilstand.erIkkeOppfylt()

    internal sealed class Tilstand(
        private val name: String,
        private val erOppfylt: Boolean,
        private val erIkkeOppfylt: Boolean
    ) {
        internal open fun onEntry(vilkårsvurdering: Paragraf_11_5) {}
        internal open fun onExit(vilkårsvurdering: Paragraf_11_5) {}
        internal fun erOppfylt() = erOppfylt
        internal fun erIkkeOppfylt() = erIkkeOppfylt

        internal open fun håndterSøknad(
            vilkårsvurdering: Paragraf_11_5,
            søknad: Søknad,
            fødselsdato: Fødselsdato,
            vurderingsdato: LocalDate
        ) {
            error("Søknad skal ikke håndteres i tilstand $name")
        }

        internal open fun vurderNedsattArbeidsevne(
            vilkårsvurdering: Paragraf_11_5,
            oppgavesvar: OppgavesvarParagraf_11_5,
            nedsattArbeidsevnegrad: OppgavesvarParagraf_11_5.NedsattArbeidsevnegrad
        ) {
            error("Oppgave skal ikke håndteres i tilstand $name")
        }

        object IkkeVurdert : Tilstand(
            name = "IKKE_VURDERT",
            erOppfylt = false,
            erIkkeOppfylt = false
        ) {
            override fun håndterSøknad(
                vilkårsvurdering: Paragraf_11_5,
                søknad: Søknad,
                fødselsdato: Fødselsdato,
                vurderingsdato: LocalDate
            ) {
                vilkårsvurdering.tilstand(SøknadMottatt)
            }
        }

        object SøknadMottatt : Tilstand(name = "SØKNAD_MOTTATT", erOppfylt = false, erIkkeOppfylt = false) {
            override fun onEntry(vilkårsvurdering: Paragraf_11_5) {
                vilkårsvurdering.lytter.sendOppgave(
                    Oppgave_11_5(
                        vilkårsvurdering.paragraf,
                        vilkårsvurdering.ledd,
                        Personident("") //FIXME!!
                    )
                )
            }

            class Oppgave_11_5(
                private val paragraf: Paragraf,
                private val ledd: List<Ledd>,
                private val personident: Personident
            ) : Oppgave {
                override fun tilFrontendOppgave() =
                    FrontendOppgave(
                        paragraf = paragraf.name,
                        ledd = ledd.map(Ledd::name),
                        personident = personident.toFrontendPersonident()
                    )
            }

            override fun vurderNedsattArbeidsevne(
                vilkårsvurdering: Paragraf_11_5,
                oppgavesvar: OppgavesvarParagraf_11_5,
                nedsattArbeidsevnegrad: OppgavesvarParagraf_11_5.NedsattArbeidsevnegrad
            ) {
                vilkårsvurdering.oppgavesvar = oppgavesvar
                vilkårsvurdering.nedsattArbeidsevnegrad = nedsattArbeidsevnegrad
                if (nedsattArbeidsevnegrad.erNedsattMedMinstHalvparten()) {
                    vilkårsvurdering.tilstand(Oppfylt)
                } else {
                    vilkårsvurdering.tilstand(IkkeOppfylt)
                }
            }
        }

        object Oppfylt : Tilstand(
            name = "OPPFYLT",
            erOppfylt = true,
            erIkkeOppfylt = false
        )

        object IkkeOppfylt : Tilstand(
            name = "IKKE_OPPFYLT",
            erOppfylt = false,
            erIkkeOppfylt = true
        )

        internal fun toFrontendTilstand(): String = name
    }

    override fun toFrontendTilstand(): String = tilstand.toFrontendTilstand()
}

class OppgavesvarParagraf_11_2(private val medlemskap: Medlemskap) {
    class Medlemskap(private val svar: Svar) {
        enum class Svar {
            JA, VET_IKKE, NEI
        }

        internal fun erMedlem() = svar == Svar.JA
        internal fun erIkkeMedlem() = svar == Svar.NEI
    }

    internal fun erMedlem() = medlemskap.erMedlem()
    internal fun erIkkeMedlem() = medlemskap.erIkkeMedlem()
}

class OppgavesvarParagraf_11_5(private val nedsattArbeidsevnegrad: NedsattArbeidsevnegrad) {
    class NedsattArbeidsevnegrad(private val grad: Int) {
        internal fun erNedsattMedMinstHalvparten() = grad >= 50
    }

    internal fun vurderNedsattArbeidsevne(vilkår: Paragraf_11_5) {
        vilkår.vurderNedsattArbeidsevne(this, nedsattArbeidsevnegrad)
    }
}
