package no.nav.aap.domene

import no.nav.aap.august
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.LøsningVurderingAvBeregningsdato
import no.nav.aap.hendelse.Søknad
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class VurderingAvBeregningsdatoTest {

    @Test
    fun `Ved håndtering av søknad opprettes behov for vurdering av beregningsdato`() {
        val vurderingAvBeregningsdato = VurderingAvBeregningsdato()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vurderingAvBeregningsdato.håndterSøknad(søknad)

        assertTrue(søknad.behov().isNotEmpty())
    }

    @Test
    fun `Ved håndtering av løsning lagres beregningsdatoen`() {
        val vurderingAvBeregningsdato = VurderingAvBeregningsdato()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vurderingAvBeregningsdato.håndterSøknad(søknad)

        vurderingAvBeregningsdato.håndterLøsning(LøsningVurderingAvBeregningsdato(15 august 2018))

        assertEquals(15 august 2018, vurderingAvBeregningsdato.beregningsdato())
    }

    @Test
    fun `Kan ikke hente beregningsdato før løsningen er behandlet`() {
        val vurderingAvBeregningsdato = VurderingAvBeregningsdato()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vurderingAvBeregningsdato.håndterSøknad(søknad)

        assertThrows<IllegalStateException> { vurderingAvBeregningsdato.beregningsdato() }
    }
}