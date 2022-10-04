package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Periode.Companion.til
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.*
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
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
            tidspunktForVurdering = LocalDateTime.now(),
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
            tidspunktForVurdering = LocalDateTime.now(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                LocalDate.now() til LocalDate.now(), 66.0, LocalDate.now()
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
            tidspunktForVurdering = LocalDateTime.now(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                LocalDate.now() til LocalDate.now(), 100.0, LocalDate.now()
            )
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.MANUELL_VURDERING_TRENGS, vilkår)
    }

    @Test
    fun `Dersom paragrafen er i manuell vurdering trengs og virkningsdato skal settes etter svangerskapspenger, settes paragraf oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_27_FørsteLedd()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                LocalDate.now() til LocalDate.now(), 100.0, LocalDate.now()
            )
        )
        vilkår.håndterLøsning(løsning)

        val løsning22_13 = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.svangerskapspenger,
            unntaksbegrunnelse = "",
            unntak = "",
            manueltSattVirkningsdato = null,
        )
        vilkår.håndterLøsning(løsning22_13)

        assertHarIkkeBehov(løsning22_13)
        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT, vilkår)
    }

    @Test
    fun `Dersom paragrafen er oppfylt og blir kvalitetssikret, settes paragraf oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_27_FørsteLedd()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                LocalDate.now() til LocalDate.now(), 100.0, LocalDate.now()
            )
        )
        vilkår.håndterLøsning(løsning)

        val løsning22_13 = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.svangerskapspenger,
            unntaksbegrunnelse = "",
            unntak = "",
            manueltSattVirkningsdato = null,
        )
        vilkår.håndterLøsning(løsning22_13)

        val kvalitetssikringparagraf22_13 = KvalitetssikringParagraf_22_13(
            kvalitetssikringId = UUID.randomUUID(),
            løsningId = UUID.randomUUID(),
            kvalitetssikretAv = "saksbehandler",
            tidspunktForKvalitetssikring = LocalDateTime.now(),
            erGodkjent = true,
            begrunnelse = "Begrunnelse",
        )
        vilkår.håndterKvalitetssikring(kvalitetssikringparagraf22_13)

        assertHarIkkeBehov(løsning22_13)
        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("saksbehandler", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Dersom paragrafen er i manuell vurdering trengs og virkningsdato skal settes etter maksdato på sykepenger, settes paragraf til ikke relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_27_FørsteLedd()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningParagraf_11_27_FørsteLedd(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                LocalDate.now() til LocalDate.now(), 100.0, LocalDate.now()
            )
        )
        vilkår.håndterLøsning(løsning)

        val løsning22_13 = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger,
            unntaksbegrunnelse = "",
            unntak = "",
            manueltSattVirkningsdato = null,
        )
        vilkår.håndterLøsning(løsning22_13)

        assertHarIkkeBehov(løsning22_13)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår)
    }

    private fun assertHarBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isNotEmpty())
    }

    private fun assertHarIkkeBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isEmpty())
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_27_FørsteLedd) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(
        tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn,
        vilkårsvurdering: Paragraf_11_27_FørsteLedd
    ) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_27_FørsteLedd) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_27_FørsteLedd) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }

}