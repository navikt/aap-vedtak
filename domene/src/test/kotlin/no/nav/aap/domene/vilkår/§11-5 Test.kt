package no.nav.aap.domene.vilkår

import no.nav.aap.domene.UlovligTilstandException
import no.nav.aap.domene.Vedtak
import no.nav.aap.domene.beregning.Inntektsgrunnlag
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering.Companion.toDto
import no.nav.aap.hendelse.KvalitetssikringParagraf_11_5
import no.nav.aap.hendelse.LøsningParagraf_11_5
import no.nav.aap.hendelse.Søknad
import no.nav.aap.modellapi.KvalitetssikringParagraf_11_5ModellApi
import no.nav.aap.modellapi.LøsningParagraf_11_5ModellApi
import no.nav.aap.modellapi.Utfall
import org.junit.jupiter.api.Assertions.assertEquals
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

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "veileder",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradOppfylt()
        )
        løsning.vurderNedsattArbeidsevne(Paragraf_11_5.AvventerManuellVurdering, vilkår)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `Hvis søkers arbeidsevne ikke er nedsatt med 50 prosent, er vilkår ikke oppfylt`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsning = LøsningParagraf_11_5(
            UUID.randomUUID(),
            "veileder",
            LocalDateTime.now(),
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = false,
                kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                nedsettelseSkyldesSykdomEllerSkade = true,
                nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                kilder = emptyList(),
                legeerklæringDato = null,
                sykmeldingDato = null,
            )
        )
        løsning.vurderNedsattArbeidsevne(Paragraf_11_5.AvventerManuellVurdering, vilkår)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
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

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt blir godkjent av kvalitetssiker blir tilstand satt til oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val løsning = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradOppfylt()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = kvalitetssikringOppfylt(løsningId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt blir godkjent av kvalitetssiker blir tilstand satt til ikke oppfylt kvalitetssikret`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val løsning = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradIkkeOppfylt()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = kvalitetssikringOppfylt(løsningId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_OPPFYLT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_KVALITETSSIKRET, vilkår)
    }

    @Test
    fun `Hvis tilstand oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val løsning = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradOppfylt()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = kvalitetssikringIkkeOppfylt(løsningId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis tilstand ikke oppfylt ikke blir godkjent av kvalitetssiker blir tilstand satt tilbake til avventer manuell vurdering`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val løsning = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradIkkeOppfylt()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikring = kvalitetssikringIkkeOppfylt(løsningId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertUtfall(Utfall.IKKE_VURDERT, vilkår)
        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.AVVENTER_MANUELL_VURDERING, vilkår)
    }

    @Test
    fun `Hvis paragrafen får en kvalitetssikring og et vedtak, vil vedtaket inneholde en totrinnskontroll`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val løsning = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradIkkeOppfylt()
        )
        vilkår.håndterLøsning(løsning)

        val kvalitetssikringId = UUID.randomUUID()
        val kvalitetssikring = kvalitetssikringOppfylt(løsningId, kvalitetssikringId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        val vedtak = Vedtak(
            vedtaksid = UUID.randomUUID(),
            innvilget = false,
            inntektsgrunnlag = Inntektsgrunnlag.inntektsgrunnlag(
                beregningsdato = LocalDate.now(),
                inntekterSiste3Kalenderår = emptyList(),
                fødselsdato = Fødselsdato(LocalDate.now())
            ),
            vedtaksdato = LocalDate.now(),
            virkningsdato = LocalDate.now()
        )

        vilkår.lagSnapshot(vedtak)
        val dto = vedtak.toDto()

        assertEquals(
            løsningId,
            (dto.etSettAvVurderteVilkårSomHarFørtTilDetteVedtaket[0].løsning as LøsningParagraf_11_5ModellApi).løsningId
        )
        assertEquals(
            kvalitetssikringId,
            (dto.etSettAvVurderteVilkårSomHarFørtTilDetteVedtaket[0].kvalitetssikring as KvalitetssikringParagraf_11_5ModellApi?)?.kvalitetssikringId
        )
    }

    @Test
    fun `I OppfyltAvventerKvalitetssikring, hvis vi mottar kvalitetssikring på en ukjent løsning, endres ikke tilstanden`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val ukjentLøsningId = UUID.randomUUID()
        val løsning1 = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradOppfylt()
        )
        vilkår.håndterLøsning(løsning1)

        val kvalitetssikring = kvalitetssikringIkkeOppfylt(ukjentLøsningId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    @Test
    fun `I IkkeOppfyltAvventerKvalitetssikring, hvis vi mottar kvalitetssikring på en ukjent løsning, endres ikke tilstanden`() {
        val personident = Personident("12345678910")
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(67))

        val vilkår = Paragraf_11_5()

        vilkår.håndterSøknad(Søknad(UUID.randomUUID(), personident, fødselsdato), fødselsdato, LocalDate.now())

        val løsningId = UUID.randomUUID()
        val ukjentLøsningId = UUID.randomUUID()
        val løsning1 = LøsningParagraf_11_5(
            løsningId,
            "saksbehandler",
            LocalDateTime.now(),
            løsningNedsattArbeidsevnegradIkkeOppfylt()
        )
        vilkår.håndterLøsning(løsning1)

        val kvalitetssikring = kvalitetssikringIkkeOppfylt(ukjentLøsningId)
        vilkår.håndterKvalitetssikring(kvalitetssikring)

        assertTilstand(Vilkårsvurdering.Tilstand.Tilstandsnavn.IKKE_OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING, vilkår)
    }

    private fun løsningNedsattArbeidsevnegradIkkeOppfylt() =
        LøsningParagraf_11_5.NedsattArbeidsevnegrad(
            kravOmNedsattArbeidsevneErOppfylt = false,
            kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
            nedsettelseSkyldesSykdomEllerSkade = false,
            nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
            kilder = emptyList(),
            legeerklæringDato = null,
            sykmeldingDato = null,
        )

    private fun løsningNedsattArbeidsevnegradOppfylt() =
        LøsningParagraf_11_5.NedsattArbeidsevnegrad(
            kravOmNedsattArbeidsevneErOppfylt = true,
            kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
            nedsettelseSkyldesSykdomEllerSkade = true,
            nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
            kilder = emptyList(),
            legeerklæringDato = null,
            sykmeldingDato = null,
        )

    private fun kvalitetssikringOppfylt(løsningId: UUID, kvalitetssikringId: UUID = UUID.randomUUID()) =
        KvalitetssikringParagraf_11_5(
            kvalitetssikringId = kvalitetssikringId,
            løsningId = løsningId,
            kvalitetssikretAv = "X",
            tidspunktForKvalitetssikring = LocalDateTime.now(),
            kravOmNedsattArbeidsevneErGodkjent = true,
            kravOmNedsattArbeidsevneErGodkjentBegrunnelse = null,
            nedsettelseSkyldesSykdomEllerSkadeErGodkjent = true,
            nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = null,
        )

    private fun kvalitetssikringIkkeOppfylt(løsningId: UUID) =
        KvalitetssikringParagraf_11_5(
            kvalitetssikringId = UUID.randomUUID(),
            løsningId = løsningId,
            kvalitetssikretAv = "X",
            tidspunktForKvalitetssikring = LocalDateTime.now(),
            kravOmNedsattArbeidsevneErGodkjent = false,
            kravOmNedsattArbeidsevneErGodkjentBegrunnelse = "NEI",
            nedsettelseSkyldesSykdomEllerSkadeErGodkjent = false,
            nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = "NEI",
        )

    private fun assertUtfall(utfall: Utfall, vilkårsvurdering: Paragraf_11_5) {
        assertEquals(utfall, listOf(vilkårsvurdering).toDto().first().utfall)
    }

    private fun assertTilstand(tilstand: Vilkårsvurdering.Tilstand.Tilstandsnavn, vilkårsvurdering: Paragraf_11_5) {
        assertEquals(tilstand.name, listOf(vilkårsvurdering).toDto().first().tilstand)
    }
}
