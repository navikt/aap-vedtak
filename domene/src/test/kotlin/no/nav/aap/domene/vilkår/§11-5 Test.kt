package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class `§11-5 Test` {
    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 50 prosent, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            "veileder",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = true,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        )
        løsning.vurderNedsattArbeidsevne(Paragraf_11_5.SøknadMottatt, vilkår)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 49 prosent, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            "veileder",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = false,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        )
        løsning.vurderNedsattArbeidsevne(Paragraf_11_5.SøknadMottatt, vilkår)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_5()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke løsning, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }
}
