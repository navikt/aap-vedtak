package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_22_13
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class `§22-13 Test` {

    @Test
    fun `Hvis saksbehandler manuelt har oppfylt 22-13, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            "INGEN",
            "",
            LocalDate.now()
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            "INGEN",
            "",
            LocalDate.now()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_22_13(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            "INGEN",
            "",
            LocalDate.now()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_22_13(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    @Test
    fun `Hvis vilkår opprettes vil behov om sykependedager og varighet opprettes`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val behov = søknad.behov()
        assertEquals(2, behov.size)
        assertEquals(1, behov.filterIsInstance<Behov_8_48AndreLedd>().size)
        assertEquals(1, behov.filterIsInstance<Behov_22_13>().size)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_22_13) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(
        tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn,
        vilkårsvurdering: Paragraf_22_13
    ) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_22_13) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_22_13) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
