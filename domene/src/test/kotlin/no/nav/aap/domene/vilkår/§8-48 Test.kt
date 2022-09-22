package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningSykepengedager
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class `§8-48 Test` {

    @Test
    fun `Dersom løsning ikke inneholder sykepengedager, settes paragraf til ikke relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_8_48()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningSykepengedager(
            sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke()
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår)
    }

    @Test
    fun `Dersom løsning inneholder sykepengedager, settes paragraf til manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_8_48()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningSykepengedager(
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
            )
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    private fun assertHarBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isNotEmpty())
    }

    private fun assertHarIkkeBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isEmpty())
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_8_48) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_8_48) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_8_48) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })

    }
}