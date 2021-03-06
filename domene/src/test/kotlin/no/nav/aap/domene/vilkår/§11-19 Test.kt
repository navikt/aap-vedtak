package no.nav.aap.domene.vilkår

import no.nav.aap.august
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningParagraf_11_19
import no.nav.aap.hendelse.Søknad
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class `§11-19 Test` {

    @Test
    fun `Ved håndtering av søknad opprettes behov for vurdering av beregningsdato`() {
        val vilkår = Paragraf_11_19()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vilkår.håndterSøknad(søknad, Fødselsdato(1 januar 1980), 1 januar 2022)

        assertTrue(søknad.behov().isNotEmpty())
    }

    @Test
    fun `Ved håndtering av løsning lagres beregningsdatoen`() {
        val vilkår = Paragraf_11_19()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vilkår.håndterSøknad(søknad, Fødselsdato(1 januar 1980), 1 januar 2022)

        vilkår.håndterLøsning(LøsningParagraf_11_19("saksbehandler", LocalDateTime.now(), 15 august 2018))

        assertEquals(15 august 2018, vilkår.beregningsdato())
    }

    @Test
    fun `Kan ikke hente beregningsdato før løsningen er behandlet`() {
        val vilkår = Paragraf_11_19()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vilkår.håndterSøknad(søknad, Fødselsdato(1 januar 1980), 1 januar 2022)

        assertNull(vilkår.beregningsdato())
    }
}
