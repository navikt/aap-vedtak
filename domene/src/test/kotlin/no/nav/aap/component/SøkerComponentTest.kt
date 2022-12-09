package no.nav.aap.component

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.hendelse.*
import no.nav.aap.hendelse.behov.Behov_8_48AndreLedd
import no.nav.aap.hendelse.innstilling.InnstillingParagraf_11_6
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.modellapi.SøkerModellApi
import no.nav.aap.modellapi.VilkårsvurderingModellApi
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class SøkerComponentTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(18), "12345678910")

        val saker = søker.toDto().saker
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
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(17), "12345678910")

        val saker = søker.toDto().saker
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
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(18), "12345678910")

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        //FIXME: Midlertidig fjernet 11-29
//        assertEquals(11, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals(10, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
    }

    @Test
    fun `Hvis vi mottar en søknad skal behov om sykependedager opprettes`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)
        val behov = søknad.behov()

        val behovSykependedager = behov.filterIsInstance<Behov_8_48AndreLedd>()
        assertEquals(1, behovSykependedager.size)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(18), "12345678910")

        søker.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                "veileder",
                LocalDateTime.now(),
                LøsningParagraf_11_5.NedsattArbeidsevnegrad(
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

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        //FIXME: Midlertidig fjernet 11-29
//        assertEquals(11, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals(10, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne og medlemskap`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(18), "12345678910")

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        //FIXME: Midlertidig fjernet 11-29
//        assertEquals(11, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals(10, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
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
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "AVVENTER_INNSTILLING", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
//        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    @Test
    fun `Hvis vi mottar en søknad med bruker over 62 får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne og medlemskap`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(65), "12345678910")

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        //FIXME: Midlertidig fjernet 11-29
//        assertEquals(11, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals(10, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MASKINELT_KVALITETSSIKRET",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "AVVENTER_MANUELL_VURDERING",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "AVVENTER_INNSTILLING", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
//        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt, men ikke vilkår om medlemskap`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(18), "12345678910")

        søker.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                "veileder",
                LocalDateTime.now(),
                LøsningParagraf_11_5.NedsattArbeidsevnegrad(
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

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        //FIXME: Midlertidig fjernet 11-29
//        assertEquals(11, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals(10, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
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
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT_AVVENTER_KVALITETSSIKRING", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "AVVENTER_INNSTILLING", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
//        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om der bruker er medlem, blir vilkår om medlemskap oppfylt, men ikke vilkår om nedsatt arbeidsevne`() {
        val søker = opprettSøkerMedSøknad(LocalDate.now().minusYears(18), "12345678910")

        søker.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        //FIXME: Midlertidig fjernet 11-29
//        assertEquals(11, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertEquals(10, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_8_48)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT_KVALITETSSIKRET", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
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
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "AVVENTER_INNSTILLING", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MASKINELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_27)
//        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
        assertTilstand(vilkårsvurderinger, "AVVENTER_MANUELL_VURDERING", Vilkårsvurdering.Paragraf.PARAGRAF_22_13)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt`() {
        val søker = opprettSøkerMedSøknad(17 juli 1995, "12345678910")

        søker.håndterLøsning(
            LøsningMaskinellParagraf_11_2(
                UUID.randomUUID(),
                LocalDateTime.now(),
                LøsningMaskinellParagraf_11_2.ErMedlem.JA
            )
        )
        søker.håndterLøsning(
            LøsningSykepengedager(
                løsningId = UUID.randomUUID(),
                tidspunktForVurdering = LocalDateTime.now(),
                sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
            )
        )
        søker.håndterLøsning(
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
        søker.håndterLøsning(
            LøsningParagraf_11_3(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        søker.håndterLøsning(
            LøsningParagraf_11_5(
                løsningId = UUID.randomUUID(),
                "veileder",
                LocalDateTime.now(),
                LøsningParagraf_11_5.NedsattArbeidsevnegrad(
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
        søker.håndterInnstilling(
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
        søker.håndterLøsning(
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
        søker.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                unntak = "INGEN",
                unntaksbegrunnelse = "",
                manueltSattVirkningsdato = LocalDate.now(),
                begrunnelseForAnnet = null,
            )
        )
        søker.håndterLøsning(
            LøsningParagraf_11_29(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                true
            )
        )
        søker.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        søker.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(ARBEIDSGIVER, januar(2020), 500000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2019), 500000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2018), 500000.beløp)
                )
            )
        )

        val dtoSøker = søker.toDto()

        assertEquals("AVVENTER_KVALITETSSIKRING", dtoSøker.saker.single().tilstand)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt - med serialisering og deserialisering`() {
        var søkerModellApi: SøkerModellApi = opprettSøkerMedSøknad(17 juli 1995, "12345678910").toDto()

        fun medSøker(block: Søker.() -> Unit) {
            val søker = Søker.gjenopprett(søkerModellApi)
            block(søker)
            søkerModellApi = søker.toDto()
        }

        medSøker {
            håndterLøsning(
                LøsningMaskinellParagraf_11_2(
                    UUID.randomUUID(),
                    LocalDateTime.now(),
                    LøsningMaskinellParagraf_11_2.ErMedlem.JA
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningSykepengedager(
                    løsningId = UUID.randomUUID(),
                    tidspunktForVurdering = LocalDateTime.now(),
                    sykepengedager = LøsningSykepengedager.Sykepengedager.HarIkke
                )
            )
        }
        medSøker {
            håndterLøsning(
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
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_3(
                    løsningId = UUID.randomUUID(),
                    "saksbehandler",
                    LocalDateTime.now(),
                    true
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_5(
                    løsningId = UUID.randomUUID(),
                    "veileder",
                    LocalDateTime.now(),
                    LøsningParagraf_11_5.NedsattArbeidsevnegrad(
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
        }
        medSøker {
            håndterInnstilling(
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
        }
        medSøker {
            håndterLøsning(
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
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_22_13(
                    løsningId = UUID.randomUUID(),
                    vurdertAv = "saksbehandler",
                    tidspunktForVurdering = LocalDateTime.now(),
                    bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                    unntak = "INGEN",
                    unntaksbegrunnelse = "",
                    manueltSattVirkningsdato = LocalDate.now(),
                    begrunnelseForAnnet = null,
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_29(
                    løsningId = UUID.randomUUID(),
                    "saksbehandler",
                    LocalDateTime.now(),
                    true
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_19(
                    løsningId = UUID.randomUUID(),
                    "saksbehandler",
                    LocalDateTime.now(),
                    13 september 2021
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningInntekter(
                    listOf(
                        Inntekt(ARBEIDSGIVER, januar(2020), 500000.beløp),
                        Inntekt(ARBEIDSGIVER, januar(2019), 500000.beløp),
                        Inntekt(ARBEIDSGIVER, januar(2018), 500000.beløp)
                    )
                )
            )
        }
        medSøker { }//Map frem og tilbake enda en gang for å sjekke at vedtak også blir mappet korrekt

        assertEquals("AVVENTER_KVALITETSSIKRING", søkerModellApi.saker.single().tilstand)
        assertEquals(5.078089, søkerModellApi.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt for student`() {
        val søker = opprettSøkerMedSøknad(17 juli 1995, "12345678910", erStudent = true)

        søker.håndterLøsning(
            LøsningParagraf_11_19(
                løsningId = UUID.randomUUID(),
                "saksbehandler",
                LocalDateTime.now(),
                13 september 2021
            )
        )
        søker.håndterLøsning(
            LøsningParagraf_22_13(
                løsningId = UUID.randomUUID(),
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                bestemmesAv = LøsningParagraf_22_13.BestemmesAv.soknadstidspunkt,
                unntak = "INGEN",
                unntaksbegrunnelse = "",
                manueltSattVirkningsdato = LocalDate.now(),
                begrunnelseForAnnet = null,
            )
        )
        søker.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(ARBEIDSGIVER, januar(2020), 500000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2019), 500000.beløp),
                    Inntekt(ARBEIDSGIVER, januar(2018), 500000.beløp)
                )
            )
        )

        val dtoSøker = søker.toDto()

        assertEquals("AVVENTER_KVALITETSSIKRING", dtoSøker.saker.single().tilstand)
        assertEquals("STUDENT", dtoSøker.saker.single().sakstyper.last().type)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    private fun opprettSøkerMedSøknad(
        dato: LocalDate,
        ident: String,
        søknadstidspunkt: LocalDateTime = LocalDateTime.now(),
        erStudent: Boolean = false,
        harTidligereYrkesskade: Søknad.HarYrkesskade = Søknad.HarYrkesskade.NEI
    ): Søker {
        val fødselsdato = Fødselsdato(dato)
        val personident = Personident(ident)
        val søknad = Søknad(UUID.randomUUID(), personident, fødselsdato, søknadstidspunkt, erStudent, harTidligereYrkesskade)
        return søknad.opprettSøker().apply { håndterSøknad(søknad) }
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

    private fun Søker.håndterLøsning(løsning: LøsningMaskinellParagraf_11_2) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_3) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_5) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterInnstilling(innstilling: InnstillingParagraf_11_6) {
        håndterLøsning(innstilling, Vilkårsvurdering<*, *>::håndterInnstilling)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_6) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_19) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_27_FørsteLedd) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_29) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_22_13) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningMaskinellMedlemskapYrkesskade) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_5Yrkesskade) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterLøsning(løsning: LøsningParagraf_11_22) {
        håndterLøsning(løsning, Vilkårsvurdering<*, *>::håndterLøsning)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_2) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_3) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_5) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_6) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_22_13) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_19) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }

    private fun Søker.håndterKvalitetssikring(kvalitetssikring: KvalitetssikringParagraf_11_29) {
        håndterKvalitetssikring(kvalitetssikring, Vilkårsvurdering<*, *>::håndterKvalitetssikring)
    }
}
