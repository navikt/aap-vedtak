package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toDto
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Periode.Companion.til
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.BehovIverksettVedtak
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6
import no.nav.aap.januar
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import no.nav.aap.oktober
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.util.*

internal class SakTest {
    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er under 18 år får vi et ikke-oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("IKKE_OPPFYLT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er over 18 år, er medlem og har nedsatt arbeidsevne med 50 prosent vil saken gå videre i behandlingen`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_5
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_6
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_22_13
        )
//        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar en søknad der søker har oppgitt yrkesskade`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato, harTidligereYrkesskade = Søknad.HarYrkesskade.JA)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellMedlemskapYrkesskade(
                UUID.randomUUID(),
                LøsningMaskinellMedlemskapYrkesskade.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5Yrkesskade(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                arbeidsevneErNedsattMedMinst50Prosent = true,
                arbeidsevneErNedsattMedMinst30Prosent = true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_22(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                erOppfylt = true,
                andelNedsattArbeidsevne = 50,
                år = Year.of(2018),
                antattÅrligArbeidsinntekt = 400000.beløp
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_5_YRKESSKADE
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_6
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_22_13
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_22
        )
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_29
        )
    }

    @Test
    fun `alle behov blir kansellert dersom et vilkår blir satt til ikke oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("IKKE_OPPFYLT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTrue(søknad.behov().isEmpty())
    }

    @Test
    fun `Kvalitetssikrer vilkår før vedtak fattes`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
//        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)
//
//
//        val kvalitetssikringparagraf1129 = KvalitetssikringParagraf_11_29(
//            kvalitetssikringId = UUID.randomUUID(),
//            løsningId = UUID.randomUUID(),
//            kvalitetssikretAv = "beslutter",
//            tidspunktForKvalitetssikring = LocalDateTime.now(),
//            erGodkjent = true,
//            begrunnelse = "JA"
//        )
//        sak.håndterKvalitetssikring(kvalitetssikringparagraf1129)
        assertTilstand("VEDTAK_FATTET", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
//        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Sender behov ved iverksetting`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
//        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)
//
//
//        sak.håndterKvalitetssikring(
//            KvalitetssikringParagraf_11_29(
//                kvalitetssikringId = UUID.randomUUID(),
//                løsningId = UUID.randomUUID(),
//                kvalitetssikretAv = "beslutter",
//                tidspunktForKvalitetssikring = LocalDateTime.now(),
//                erGodkjent = true,
//                begrunnelse = "JA"
//            )
//        )
        assertTilstand("VEDTAK_FATTET", sak)

        val iverksettelse = IverksettelseAvVedtak("saksbehandler@nav.no")
        sak.håndterIverksettelse(iverksettelse)

        assertTilstand("VEDTAK_IVERKSATT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
//        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)

        val vedtak = saker[0].vedtak!!
        val behov = iverksettelse.behov()
        val expectedVedtakBehov = BehovIverksettVedtak(
            vedtak = Vedtak.gjenopprett(vedtak)
        )
        assertEquals(expectedVedtakBehov, behov.single())
    }

    @Test
    fun `Underkjent kvalitetssikring sender saken tilbake til AVVENTER_VURDERING og hindrer ikke videre kvalitetssikring`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                false,
                "NEI"
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_29(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        // Behandle underkjent løsning på nytt
        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("VEDTAK_FATTET", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
//        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis virkningsdato skal bestemmes av når sykepenger er brukt opp, vil sak vente på at gjenstående sykedager er 0`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                    gjenståendeSykedager = 0,
                    foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                    kilde = LøsningSykepengedager.Kilde.SPLEIS,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                bestemmesAv = LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger,
                unntak = "INGEN",
                unntaksbegrunnelse = "",
                manueltSattVirkningsdato = null
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
//        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)
//
//        sak.håndterKvalitetssikring(
//            KvalitetssikringParagraf_11_29(
//                kvalitetssikringId = UUID.randomUUID(),
//                UUID.randomUUID(),
//                "beslutter",
//                LocalDateTime.now(),
//                true,
//                "JA"
//            )
//        )

        assertTilstand("VEDTAK_FATTET", sak)

        sak.håndterIverksettelse(
            IverksettelseAvVedtak(
                iverksattAv = "saksbehandler@nav.no"
            )
        )

        assertTilstand("VENTER_SYKEPENGER", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                    gjenståendeSykedager = 0,
                    foreløpigBeregnetSluttPåSykepenger = LocalDate.now(),
                    kilde = LøsningSykepengedager.Kilde.SPLEIS,
                )
            )
        )

        assertTilstand("VEDTAK_IVERKSATT", sak)
    }

    @Test
    fun `Virkningsdato bestemmes av søknadsdato`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(
            søknadId = UUID.randomUUID(),
            personident = personident,
            fødselsdato = fødselsdato,
            søknadstidspunkt = (25 oktober 2022).atTime(12, 0),
        )
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
//        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)
//
//
//        sak.håndterKvalitetssikring(
//            KvalitetssikringParagraf_11_29(
//                kvalitetssikringId = UUID.randomUUID(),
//                løsningId = UUID.randomUUID(),
//                kvalitetssikretAv = "beslutter",
//                tidspunktForKvalitetssikring = LocalDateTime.now(),
//                erGodkjent = true,
//                begrunnelse = "JA"
//            )
//        )
        assertTilstand("VEDTAK_FATTET", sak)

        val iverksettelse = IverksettelseAvVedtak("saksbehandler@nav.no")
        sak.håndterIverksettelse(iverksettelse)

        assertTilstand("VEDTAK_IVERKSATT", sak)

        val saker = listOf(sak).toDto()
        val vedtak = saker[0].vedtak!!
        assertEquals(25 oktober 2022, vedtak.virkningsdato)

        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "IKKE_RELEVANT", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "IKKE_RELEVANT", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    @Test
    fun `Virkningsdato bestemmes av svangerskapspenger`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = 1 september 2022 til (30 september 2022),
                    grad = 100.0,
                    vedtaksdato = 1 september 2022,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.svangerskapspenger,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
//        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)
//
//
//        sak.håndterKvalitetssikring(
//            KvalitetssikringParagraf_11_29(
//                kvalitetssikringId = UUID.randomUUID(),
//                løsningId = UUID.randomUUID(),
//                kvalitetssikretAv = "beslutter",
//                tidspunktForKvalitetssikring = LocalDateTime.now(),
//                erGodkjent = true,
//                begrunnelse = "JA"
//            )
//        )
        assertTilstand("VEDTAK_FATTET", sak)

        val iverksettelse = IverksettelseAvVedtak("saksbehandler@nav.no")
        sak.håndterIverksettelse(iverksettelse)

        assertTilstand("VEDTAK_IVERKSATT", sak)

        val saker = listOf(sak).toDto()
        val vedtak = saker[0].vedtak!!
        assertEquals(1 oktober 2022, vedtak.virkningsdato)

        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "IKKE_RELEVANT", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
        assertTilstand(vilkårsvurderinger, "IKKE_RELEVANT", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    @Test
    fun `Virkningsdato bestemmes av maksdato fra sykepenger`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                    gjenståendeSykedager = 0,
                    foreløpigBeregnetSluttPåSykepenger = 30 september 2022,
                    kilde = LøsningSykepengedager.Kilde.SPLEIS
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_27_FørsteLedd(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                svangerskapspenger = LøsningParagraf_11_27_FørsteLedd.Svangerskapspenger(
                    periode = null,
                    grad = null,
                    vedtaksdato = null,
                ),
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                vurdertAv = "veileder",
                tidspunktForVurdering = LocalDateTime.now(),
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = "Begrunnelse",
                    nedsettelseSkyldesSykdomEllerSkade = true,
                    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = "Begrunnelse",
                    kilder = emptyList(),
                    legeerklæringDato = null,
                    sykmeldingDato = null,
                )
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterInnstilling(
            InnstillingParagraf_11_6(
                innstillingId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        sak.håndterLøsning(
            LøsningParagraf_11_6(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true,
                individuellBegrunnelse = "Begrunnelse",
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                LøsningParagraf_22_13.BestemmesAv.maksdatoSykepenger,
                "INGEN",
                "",
                LocalDate.now()
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        assertTilstand("AVVENTER_VURDERING", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        assertTilstand("BEREGN_INNTEKT", sak)

        sak.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("123456789"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("123456789"), januar(2018), 500000.beløp)
                )
            ),
            fødselsdato
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_2(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_3(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_5(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "fatter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_6(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_22_13(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)

        sak.håndterKvalitetssikring(
            KvalitetssikringParagraf_11_19(
                kvalitetssikringId = UUID.randomUUID(),
                UUID.randomUUID(),
                "beslutter",
                LocalDateTime.now(),
                true,
                "JA"
            )
        )
//        assertTilstand("AVVENTER_KVALITETSSIKRING", sak)
//
//
//        sak.håndterKvalitetssikring(
//            KvalitetssikringParagraf_11_29(
//                kvalitetssikringId = UUID.randomUUID(),
//                løsningId = UUID.randomUUID(),
//                kvalitetssikretAv = "beslutter",
//                tidspunktForKvalitetssikring = LocalDateTime.now(),
//                erGodkjent = true,
//                begrunnelse = "JA"
//            )
//        )
        assertTilstand("VEDTAK_FATTET", sak)

        val iverksettelse = IverksettelseAvVedtak("saksbehandler@nav.no")
        sak.håndterIverksettelse(iverksettelse)

        assertTilstand("VENTER_SYKEPENGER", sak)

        sak.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.Har(
                    gjenståendeSykedager = 0,
                    foreløpigBeregnetSluttPåSykepenger = 30 september 2022,
                    kilde = LøsningSykepengedager.Kilde.SPLEIS
                )
            )
        )

        assertTilstand("VEDTAK_IVERKSATT", sak)

        val saker = listOf(sak).toDto()
        val vedtak = saker[0].vedtak!!
        assertEquals(1 oktober 2022, vedtak.virkningsdato)

        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "IKKE_RELEVANT", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
        assertTilstand(vilkårsvurderinger, "IKKE_RELEVANT", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    private fun assertTilstand(actual: String, expected: Sak) {
        val dtoSak = listOf(expected).toDto().first()
        assertEquals(actual, dtoSak.tilstand)
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<VilkårsvurderingModellApi>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf
    ) {
        assertEquals(tilstand, vilkårsvurderinger.single(paragraf).tilstand)
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<VilkårsvurderingModellApi>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: Vilkårsvurdering.Ledd
    ) {
        assertTilstand(vilkårsvurderinger, tilstand, paragraf, listOf(ledd))
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<VilkårsvurderingModellApi>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>
    ) {
        assertEquals(tilstand, vilkårsvurderinger.single(paragraf, ledd).tilstand)
    }

    private fun List<VilkårsvurderingModellApi>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.paragraf == paragraf.name }

    private fun List<VilkårsvurderingModellApi>.single(
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>
    ) = single { it.paragraf == paragraf.name && it.ledd == ledd.map(Vilkårsvurdering.Ledd::name) }

    private fun Sak.håndterLøsning(løsning: LøsningMaskinellParagraf_11_2) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_3) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_5) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterInnstilling(innstilling: InnstillingParagraf_11_6) {
        håndterLøsning(innstilling, Vilkårsvurdering<*>::håndterInnstilling)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_6) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_22_13) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_27_FørsteLedd) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_29) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_19) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_5Yrkesskade) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningParagraf_11_22) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterLøsning(løsning: LøsningSykepengedager) {
        håndterLøsning(løsning, Vilkårsvurdering<*>::håndterLøsning)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_2) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_3) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_6) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_22_13) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_19) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }

    private fun Sak.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_29) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*>::håndterKvalitetssikring)
    }
}
