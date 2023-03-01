package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.Hendelse
import no.nav.aap.hendelse.KvalitetssikringParagraf_22_13
import no.nav.aap.hendelse.LøsningParagraf_22_13
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.behov.Behov_22_13
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            unntak = "INGEN",
            unntaksbegrunnelse = "",
            manueltSattVirkningsdato = LocalDate.now(),
            begrunnelseForAnnet = null,
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            unntak = "INGEN",
            unntaksbegrunnelse = "",
            manueltSattVirkningsdato = LocalDate.now(),
            begrunnelseForAnnet = null,
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_22_13(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
            unntak = "INGEN",
            unntaksbegrunnelse = "",
            manueltSattVirkningsdato = LocalDate.now(),
            begrunnelseForAnnet = null,
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = KvalitetssikringParagraf_22_13(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Dersom paragrafen er i søknad mottatt og virkningsdato skal settes etter maksdato på sykepenger, settes paragraf til ikke relevant`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_22_13(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            bestemmesAv = LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger,
            unntak = "INGEN",
            unntaksbegrunnelse = "",
            manueltSattVirkningsdato = LocalDate.now(),
            begrunnelseForAnnet = null,
        )
        vilkår.håndterLøsning(løsning)

        assertHarIkkeBehov(løsning)
        assertUtfall(Utfall.IKKE_RELEVANT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_RELEVANT, vilkår)
    }

    @Test
    fun `Hvis vilkår opprettes vil behov om varighet opprettes`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_22_13()

        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        vilkår.håndterSøknad(søknad, fødselsdato, LocalDate.now())

        val behov = søknad.behov()
        assertEquals(1, behov.size)
        assertEquals(1, behov.filterIsInstance<Behov_22_13>().size)
    }

    private fun assertHarIkkeBehov(hendelse: Hendelse) {
        assertTrue(hendelse.behov().isEmpty())
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
}
