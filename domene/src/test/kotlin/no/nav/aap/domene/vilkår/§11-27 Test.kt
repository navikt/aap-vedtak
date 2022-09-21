package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.LøsningParagraf_11_27_FørsteLedd
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.entitet.Periode
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

internal class `§11-27 Test` {

    @Test
    fun `Dersom svangerskapspenger ikke er i løsning er vilkåret ikke relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_27_FørsteLedd()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                null, null, null
            )
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår)
    }

    @Test
    fun `Dersom svangerskapspenger har grad lavere enn 100 i løsning er vilkåret ikke relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_27_FørsteLedd()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                Periode(LocalDate.now(), LocalDate.now()), 66.0, LocalDate.now()
            )
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår)
    }

    @Test
    fun `Dersom svagerskapspenger har grad 100 i løsning går vilkåret til manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_27_FørsteLedd()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                Periode(LocalDate.now(), LocalDate.now()), 100.0, LocalDate.now()
            )
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    private fun assertHarBehov(hendelse: Hendelse) {
        Assertions.assertTrue(hendelse.behov().isNotEmpty())
    }

    private fun assertHarIkkeBehov(hendelse: Hendelse) {
        Assertions.assertTrue(hendelse.behov().isEmpty())
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_27_FørsteLedd) {
        Assertions.assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_27_FørsteLedd) {
        Assertions.assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_27_FørsteLedd) {
        Assertions.assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }

}