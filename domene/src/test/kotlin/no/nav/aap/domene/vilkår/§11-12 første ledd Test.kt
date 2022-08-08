package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.LøsningParagraf_11_12FørsteLedd
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal class `§11-12 første ledd Test` {

    @Test
    fun `Hvis saksbehandler manuelt har oppfylt 11-12, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_12FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_12FørsteLedd(
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_12FørsteLedd.BestemmesAv.soknadstidspunkt,
            "INGEN",
            "",
            LocalDate.now()
        )
        vilkår.håndterLøsning(løsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_12FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_12FørsteLedd(
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_12FørsteLedd.BestemmesAv.soknadstidspunkt,
            "INGEN",
            "",
            LocalDate.now()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_12FørsteLedd("X", true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_12FørsteLedd()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_12FørsteLedd(
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_12FørsteLedd.BestemmesAv.soknadstidspunkt,
            "INGEN",
            "",
            LocalDate.now()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_12FørsteLedd("X", false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_12FørsteLedd) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_12FørsteLedd) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_12FørsteLedd) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_12FørsteLedd) {
        Assertions.assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
