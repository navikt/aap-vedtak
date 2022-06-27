package no.nav.aap.component

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.*
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class SøkerComponentTest {

    private companion object {
        private val ARBEIDSGIVER = Arbeidsgiver("987654321")
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
    }

    @Test
    fun `Hvis vi mottar en søknad der søker er under 18 år får vi et ikke-oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(
            LøsningParagraf_11_5(
                "veileder",
                LocalDateTime.now(),
                LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            )
        )

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
    }

    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne og medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar en søknad med bruker over 62 får vi et oppfylt aldersvilkår og venter på løsning på behov om nedsatt arbeidsevne og medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(65))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "SØKNAD_MOTTATT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om nedsatt arbeidsevne med 50 prosent, blir vilkår om nedsatt arbeidsevne oppfylt, men ikke vilkår om medlemskap`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(
            LøsningParagraf_11_5(
                "veileder",
                LocalDateTime.now(),
                LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            )
        )

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar løsning på behov om der bruker er medlem, blir vilkår om medlemskap oppfylt, men ikke vilkår om nedsatt arbeidsevne`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.JA))

        val saker = søker.toDto().saker
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertEquals(8, vilkårsvurderinger.size) { "Feil antall vilkårsvurderinger" }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_3)
        assertTilstand(
            vilkårsvurderinger,
            "OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_RELEVANT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_2 + Vilkårsvurdering.Ledd.LEDD_3
        )
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "SØKNAD_MOTTATT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt`() {
        val fødselsdato = Fødselsdato(17 juli 1995)
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.JA))
        søker.håndterLøsning(LøsningParagraf_11_3("saksbehandler", LocalDateTime.now(),true))
        søker.håndterLøsning(
            LøsningParagraf_11_5(
                "veileder",
                LocalDateTime.now(),
                LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            )
        )
        søker.håndterLøsning(
            LøsningParagraf_11_6(
                vurdertAv = "saksbehandler",
                tidspunktForVurdering = LocalDateTime.now(),
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true
            )
        )
        søker.håndterLøsning(LøsningParagraf_11_12FørsteLedd("saksbehandler", LocalDateTime.now(), "SPS", "INGEN", "", LocalDate.now()))
        søker.håndterLøsning(LøsningParagraf_11_29("saksbehandler", LocalDateTime.now(),true))
        søker.håndterLøsning(LøsningVurderingAvBeregningsdato("saksbehandler", LocalDateTime.now(),13 september 2021))
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

        assertEquals("VEDTAK_FATTET", dtoSøker.saker.single().tilstand)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt - med serialisering og deserialisering`() {
        val fødselsdato = Fødselsdato(17 juli 1995)
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        var dtoSøker: DtoSøker = søknad.opprettSøker().apply { håndterSøknad(søknad) }.toDto()

        fun medSøker(block: Søker.() -> Unit) {
            val søker = Søker.gjenopprett(dtoSøker)
            block(søker)
            dtoSøker = søker.toDto()
        }

        medSøker { håndterLøsning(LøsningMaskinellParagraf_11_2(LøsningMaskinellParagraf_11_2.ErMedlem.JA)) }
        medSøker { håndterLøsning(LøsningParagraf_11_3("saksbehandler", LocalDateTime.now(), true)) }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_5(
                    "veileder",
                    LocalDateTime.now(),
                    LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                        kravOmNedsattArbeidsevneErOppfylt = true,
                        nedsettelseSkyldesSykdomEllerSkade = true
                    )
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_6(
                    vurdertAv = "saksbehandler",
                    tidspunktForVurdering = LocalDateTime.now(),
                    harBehovForBehandling = true,
                    harBehovForTiltak = true,
                    harMulighetForÅKommeIArbeid = true
                )
            )
        }
        medSøker {
            håndterLøsning(
                LøsningParagraf_11_12FørsteLedd(
                    "saksbehandler",
                    LocalDateTime.now(),
                    "SPS",
                    "INGEN",
                    "",
                    LocalDate.now()
                )
            )
        }
        medSøker { håndterLøsning(LøsningParagraf_11_29("saksbehandler", LocalDateTime.now(),true)) }
        medSøker { håndterLøsning(LøsningVurderingAvBeregningsdato("saksbehandler", LocalDateTime.now(),13 september 2021)) }
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

        assertEquals("VEDTAK_FATTET", dtoSøker.saker.single().tilstand)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    @Test
    fun `Alle relevante vilkår blir oppfylt og at vi beregner inntekt for student`() {
        val fødselsdato = Fødselsdato(17 juli 1995)
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato, erStudent = true)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningVurderingAvBeregningsdato("saksbehandler", LocalDateTime.now(), 13 september 2021))
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

        assertEquals("VEDTAK_FATTET", dtoSøker.saker.single().tilstand)
        assertEquals("STUDENT", dtoSøker.saker.single().sakstyper.last().type)
        assertEquals(5.078089, dtoSøker.saker.single().vedtak?.inntektsgrunnlag?.grunnlagsfaktor)
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<DtoVilkårsvurdering>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf
    ) {
        assertEquals(tilstand, vilkårsvurderinger.single(paragraf).tilstand)
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<DtoVilkårsvurdering>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: Vilkårsvurdering.Ledd
    ) {
        assertTilstand(vilkårsvurderinger, tilstand, paragraf, listOf(ledd))
    }

    private fun assertTilstand(
        vilkårsvurderinger: List<DtoVilkårsvurdering>,
        tilstand: String,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>
    ) {
        assertEquals(tilstand, vilkårsvurderinger.single(paragraf, ledd).tilstand)
    }

    private fun List<DtoVilkårsvurdering>.single(paragraf: Vilkårsvurdering.Paragraf) =
        single { it.paragraf == paragraf.name }

    private fun List<DtoVilkårsvurdering>.single(
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>
    ) = single { it.paragraf == paragraf.name && it.ledd == ledd.map(Vilkårsvurdering.Ledd::name) }
}
