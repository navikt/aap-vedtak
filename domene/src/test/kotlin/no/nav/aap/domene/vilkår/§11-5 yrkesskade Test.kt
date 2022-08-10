package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.dto.Utfall
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5Yrkesskade
import no.nav.aap.hendelse.LøsningParagraf_11_5Yrkesskade
import no.nav.aap.hendelse.Søknad
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class `§11-5 yrkesskade Test` {

    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 30 prosent, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = true,
            arbeidsevneErNedsattMedMinst30Prosent = true
        )
        vilkår.håndterLøsning(løsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers arbeidsevne ikke er nedsatt med 30 prosent, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = false,
            arbeidsevneErNedsattMedMinst30Prosent = false
        )
        vilkår.håndterLøsning(løsning)

        assertFalse(vilkår.erOppfylt())
        assertTrue(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 30 prosent, men ikke 50 prosent, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = false,
            arbeidsevneErNedsattMedMinst30Prosent = true
        )
        vilkår.håndterLøsning(løsning)

        assertTrue(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_5_yrkesskade()

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke løsning, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertFalse(vilkår.erOppfylt())
        assertFalse(vilkår.erIkkeOppfylt())
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = true,
            arbeidsevneErNedsattMedMinst30Prosent = true
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5Yrkesskade("X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt blir godkjent av kvalitetssiker blir tilstand satt til ikke oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = false,
            arbeidsevneErNedsattMedMinst30Prosent = false
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5Yrkesskade("X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = true,
            arbeidsevneErNedsattMedMinst30Prosent = true
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5Yrkesskade("X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til søknad mottatt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5_yrkesskade()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5Yrkesskade(
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            arbeidsevneErNedsattMedMinst50Prosent = false,
            arbeidsevneErNedsattMedMinst30Prosent = false
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5Yrkesskade("X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.SØKNAD_MOTTATT, vilkår)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_5_yrkesskade) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(
        tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn,
        vilkårsvurdering: Paragraf_11_5_yrkesskade
    ) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_5_yrkesskade) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_5_yrkesskade) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
