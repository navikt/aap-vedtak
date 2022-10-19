package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class `§8-48 Test` {

    @Test
    fun `Hvis vilkår opprettes vil behov om sykepengedager opprettes`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_8_48()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val behov = søknad.behov()
        assertEquals(1, behov.size)
        assertEquals(1, behov.filterIsInstance<Behov_8_48AndreLedd>().size)
    }

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
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
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
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
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
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Dersom paragrafen er i avventer manuell vurdering og virkningsdato skal settes etter maksdato på sykepenger, settes paragraf oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_8_48()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningSykepengedager(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
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
        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Dersom paragrafen er oppfylt og blir kvalitetssikret, settes paragraf oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_8_48()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningSykepengedager(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
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
    fun `Dersom paragrafen er i avventer manuell vurdering og virkningsdato skal settes etter svangerskapspenger, settes paragraf til ikke relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_8_48()

        val søknad = Søknad(personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())
        assertHarBehov(søknad)
        assertUtfall(Utfall.IKKE_VURDERT, vilkår)

        val løsning = LøsningSykepengedager(
            løsningId = UUID.randomUUID(),
            tidspunktForVurdering = LocalDateTime.now(),
            sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                gjenståendeSykedager = 10,
                foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                kilde = LøsningSykepengedager.Kilde.SPLEIS
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

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_8_48) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_8_48) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_8_48) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_8_48) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
