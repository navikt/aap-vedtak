package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningParagraf_11_2
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

        val løsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.JA))

        vilkår.håndterLøsning(løsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.NEI))

        vilkår.håndterLøsning(løsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis vi ikke vet om søker er medlem, er vilkår for medlemskap ikke vurdert ferdig`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterLøsning(maskineltLøsning)

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker er medlem etter manuell vurdering, er vilkår for medlemskap oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterLøsning(maskineltLøsning)

        val manueltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.JA))
        vilkår.håndterLøsning(manueltLøsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søker ikke er medlem etter manuell vurdering, er vilkår for medlemskap ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterLøsning(maskineltLøsning)

        val manueltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.NEI))
        vilkår.håndterLøsning(manueltLøsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis vi ikke vet om søker er medlem etter manuell vurdering, kastes en feil`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val maskineltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.VET_IKKE))
        vilkår.håndterLøsning(maskineltLøsning)

        val manueltLøsning =
            LøsningParagraf_11_2(LøsningParagraf_11_2.Medlemskap(LøsningParagraf_11_2.Medlemskap.Svar.VET_IKKE))

        assertThrows<IllegalStateException> { vilkår.håndterLøsning(manueltLøsning) }
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_2()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke løsning, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_2()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }
}
