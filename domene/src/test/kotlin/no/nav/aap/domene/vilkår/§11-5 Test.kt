package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class `§11-5 Test` {
    @Test
    fun `Hvis søkers arbeidsevne er nedsatt med 50 prosent, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "veileder",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = true,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        )
        løsning.vurderNedsattArbeidsevne(Paragraf_11_5.AvventerManuellVurdering, vilkår)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis søkers arbeidsevne ikke er nedsatt med 50 prosent, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "veileder",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = false,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        )
        løsning.vurderNedsattArbeidsevne(Paragraf_11_5.AvventerManuellVurdering, vilkår)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_5()

        assertThrows<UlovligTilstandException> { listOf(vilkår).toDto() }
    }

    @Test
    fun `Hvis søknad er håndtert, men ikke løsning, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = true,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt blir godkjent av kvalitetssiker blir tilstand satt til ikke oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = false,
                nedsettelseSkyldesSykdomEllerSkade = false
            )
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = true,
                nedsettelseSkyldesSykdomEllerSkade = true
            )
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "saksbehandler",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = false,
                nedsettelseSkyldesSykdomEllerSkade = false
            )
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_11_5(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_5) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_5) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_5) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_5) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
