package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.OppgavesvarParagraf_11_2
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class `§11-2 Test` {
    @Test
    fun `Hvis søker er medlem, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val oppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.JA))

        vilkår.håndterOppgavesvar(oppgavesvar)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val oppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.NEI))

        vilkår.håndterOppgavesvar(oppgavesvar)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis vi ikke vet om søker er medlem, er vilkår for medlemskap ikke vurdert ferdig`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterOppgavesvar(maskineltOppgavesvar)

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker er medlem etter manuell vurdering, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterOppgavesvar(maskineltOppgavesvar)

        val manueltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.JA))
        vilkår.håndterOppgavesvar(manueltOppgavesvar)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem etter manuell vurdering, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterOppgavesvar(maskineltOppgavesvar)

        val manueltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.NEI))
        vilkår.håndterOppgavesvar(manueltOppgavesvar)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis vi ikke vet om søker er medlem etter manuell vurdering, kastes en feil`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterOppgavesvar(maskineltOppgavesvar)

        val manueltOppgavesvar =
            OppgavesvarParagraf_11_2(OppgavesvarParagraf_11_2.Medlemskap(OppgavesvarParagraf_11_2.Medlemskap.Svar.VET_IKKE))

        assertThrows<IllegalStateException> { vilkår.håndterOppgavesvar(manueltOppgavesvar) }
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_2()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke oppgavesvar, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }
}
