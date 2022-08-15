package no.nav.aap.domene.vilkår

import no.nav.aap.august
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_19
import no.nav.aap.hendelse.LøsningParagraf_11_19
import no.nav.aap.hendelse.Søknad
import no.nav.aap.januar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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

        vilkår.håndterLøsning(LøsningParagraf_11_19(UUID.randomUUID(), "saksbehandler", LocalDateTime.now(), 15 august 2018))

        assertEquals(15 august 2018, vilkår.beregningsdato())
    }

    @Test
    fun `Kan ikke hente beregningsdato før løsningen er behandlet`() {
        val vilkår = Paragraf_11_19()
        val søknad = Søknad(Personident("12345678910"), Fødselsdato(1 januar 1980))
        vilkår.håndterSøknad(søknad, Fødselsdato(1 januar 1980), 1 januar 2022)

        assertNull(vilkår.beregningsdato())
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(1 januar 1980)

        val vilkår = Paragraf_11_19()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        vilkår.håndterLøsning(LøsningParagraf_11_19(UUID.randomUUID(), "saksbehandler", LocalDateTime.now(), 15 august 2018))

        val kvalitetssikring = KvalitetssikringParagraf_11_19(UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(1 januar 1980)

        val vilkår = Paragraf_11_19()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        vilkår.håndterLøsning(LøsningParagraf_11_19(UUID.randomUUID(), "saksbehandler", LocalDateTime.now(), 15 august 2018))

        val kvalitetssikring = KvalitetssikringParagraf_11_19(UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_19) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_19) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_19) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_19) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
