package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.*

internal class `§11-4 første ledd Test` {
    @Test
    fun `Hvis søkers alder er 67 år, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis søkers alder er 68 år, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(68))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis søkers alder er 18 år, er vilkår oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MASKINELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis søkers alder er 17 år, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))

        val vilkår = Paragraf_11_4FørsteLedd()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis søknad ikke er håndtert, er vilkåret hverken oppfylt eller ikke-oppfylt`() {
        val vilkår = Paragraf_11_4FørsteLedd()

        assertThrows<UlovligTilstandException> { listOf(vilkår).toDto() }
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_4FørsteLedd) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(
        tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn,
        vilkårsvurdering: Paragraf_11_4FørsteLedd
    ) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }
}
