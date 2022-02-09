package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class `§11-12 første ledd Test` {

    @Test
    fun `Hvis saksbehandler manuelt har oppfylt 11-12, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_12FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_12FørsteLedd(true)
        vilkår.håndterLøsning(løsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis saksbehandler manuelt IKKE har oppfylt 11-12, settes vilkår til ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_12FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_12FørsteLedd(false)
        vilkår.håndterLøsning(løsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }
}
