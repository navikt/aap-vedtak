package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_6
import no.nav.aap.hendelse.LøsningParagraf_11_6
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class `§11-6 Test` {

    @Test
    fun `Hvis saksbehandler manuelt har oppfylt 11-6, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true
        )
        vilkår.håndterLøsning(løsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis bruker ikke har behov for behandling, settes vilkår til ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true
        )
        vilkår.håndterLøsning(løsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis bruker ikke har behov for tiltak, settes vilkår til ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = true
        )
        vilkår.håndterLøsning(løsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis bruker ikke har mulighet til å komme i arbeid, settes vilkår til ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = false
        )
        vilkår.håndterLøsning(løsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_6("X", true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt blir godkjent av kvalitetssiker blir tilstand satt til ikke oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = false
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_6("X", true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_6("X", false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_6(
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = false
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_6("X", false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_6) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_6) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_6) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_6) {
        Assertions.assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
