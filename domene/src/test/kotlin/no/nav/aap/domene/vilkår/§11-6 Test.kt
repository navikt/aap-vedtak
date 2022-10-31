package no.nav.aap.domene.vilkår

import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_6
import no.nav.aap.hendelse.LøsningParagraf_11_6
import no.nav.aap.hendelse.Søknad
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class `§11-6 Test` {

    @Test
    fun `Hvis veileder manuelt har innstilt 11-6, settes vilkår til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis saksbehandler manuelt har oppfylt 11-6, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis bruker ikke oppfyller noen av vilkårene, settes vilkår til ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = false,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis bruker har behov for behandling, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = false,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis bruker har behov for tiltak, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = false,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis bruker har mulighet til å komme i arbeid, settes vilkår til oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "saksbehandler",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterLøsning(løsning)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring =
            KvalitetssikringParagraf_11_6(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt blir godkjent av kvalitetssiker blir tilstand satt til ikke oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = false,
            individuellBegrunnelse = "Begrunnelse",
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring =
            KvalitetssikringParagraf_11_6(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), true, "JA")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertKvalitetssikretAv("X", vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring =
            KvalitetssikringParagraf_11_6(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_6()

        vilkår.håndterSøknad(Søknad(personident, fødselsdato), fødselsdato, LocalDate.now())

        val innstilling = InnstillingParagraf_11_6(
            innstillingId = UUID.randomUUID(),
            vurdertAv = "veileder",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = true,
            harBehovForTiltak = true,
            harMulighetForÅKommeIArbeid = true,
            individuellBegrunnelse = "Begrunnelse",
        )
        vilkår.håndterInnstilling(innstilling)

        val løsning = LøsningParagraf_11_6(
            løsningId = UUID.randomUUID(),
            vurdertAv = "X",
            tidspunktForVurdering = LocalDateTime.now(),
            harBehovForBehandling = false,
            harBehovForTiltak = false,
            harMulighetForÅKommeIArbeid = false,
            individuellBegrunnelse = "Begrunnelse",
        )

        vilkår.håndterLøsning(løsning)

        val kvalitetssikring =
            KvalitetssikringParagraf_11_6(UUID.randomUUID(), UUID.randomUUID(), "X", LocalDateTime.now(), false, "NEI")
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertIkkeKvalitetssikret(vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_6) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_6) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }

    private fun assertKvalitetssikretAv(kvalitetssikretAv: String, vilkårsvurdering: Paragraf_11_6) {
        assertEquals(kvalitetssikretAv, listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv)
    }

    private fun assertIkkeKvalitetssikret(vilkårsvurdering: Paragraf_11_6) {
        assertNull(listOf(vilkårsvurdering).toDto().first().kvalitetssikretAv?.takeIf { it.isNotEmpty() })
    }
}
