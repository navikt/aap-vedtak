package no.nav.aap.domene

import no.nav.aap.domene.Sak.Companion.toDto
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.*
import no.nav.aap.september
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Year
import kotlin.test.assertTrue

internal class SakTest {
    @Test
    fun `Hvis vi mottar en søknad får vi et oppfylt aldersvilkår`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SØKNAD_MOTTATT", sak)

        val saker = listOf(sak).toDto()
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
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("IKKE_OPPFYLT", sak)

        val saker = listOf(sak).toDto()
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
    fun `Hvis vi mottar en søknad der søker er over 18 år, er medlem og har nedsatt arbeidsevne med 50 prosent vil saken gå videre i behandlingen`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningManuellParagraf_11_2("saksbehandler", LøsningManuellParagraf_11_2.ErMedlem.JA))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningParagraf_11_3("saksbehandler", true))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5(
                vurdertAv = "veileder",
                nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                    kravOmNedsattArbeidsevneErOppfylt = true,
                    nedsettelseSkyldesSykdomEllerSkade = true
                )
            )
        )
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_6(
                vurdertAv = "saksbehandler",
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true
            )
        )
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningParagraf_11_12FørsteLedd("saksbehandler", "SPS", "INGEN", "", LocalDate.now()))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningParagraf_11_29("saksbehandler", true))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningVurderingAvBeregningsdato("saksbehandler", 13 september 2021))
        assertTilstand("BEREGN_INNTEKT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MASKINELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_2)
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
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `Hvis vi mottar en søknad der søker har oppgitt yrkesskade`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(18))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato, harTidligereYrkesskade = Søknad.HarYrkesskade.JA)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningMaskinellMedlemskapYrkesskade(LøsningMaskinellMedlemskapYrkesskade.ErMedlem.JA))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningParagraf_11_3("saksbehandler", true))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_5_yrkesskade(
                vurdertAv = "veileder",
                arbeidsevneErNedsattMedMinst50Prosent = true,
                arbeidsevneErNedsattMedMinst30Prosent = true
            )
        )
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_6(
                vurdertAv = "saksbehandler",
                harBehovForBehandling = true,
                harBehovForTiltak = true,
                harMulighetForÅKommeIArbeid = true
            )
        )
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningParagraf_11_12FørsteLedd("saksbehandler", "SPS", "INGEN", "", LocalDate.now()))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(
            LøsningParagraf_11_22(
                vurdertAv = "saksbehandler",
                erOppfylt = true,
                andelNedsattArbeidsevne = 50,
                år = Year.of(2018),
                antattÅrligArbeidsinntekt = 400000.beløp
            )
        )
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningParagraf_11_29("saksbehandler", true))
        assertTilstand("SØKNAD_MOTTATT", sak)

        sak.håndterLøsning(LøsningVurderingAvBeregningsdato("saksbehandler", 13 september 2021))
        assertTilstand("BEREGN_INNTEKT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
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
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_5_YRKESSKADE)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_6)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_12)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_22)
        assertTilstand(vilkårsvurderinger, "OPPFYLT_MANUELT", Vilkårsvurdering.Paragraf.PARAGRAF_11_29)
    }

    @Test
    fun `alle behov blir kansellert dersom et vilkår blir satt til ikke oppfylt`() {
        val fødselsdato = Fødselsdato(LocalDate.now().minusYears(17))
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val sak = Sak()

        sak.håndterSøknad(søknad, fødselsdato)
        assertTilstand("IKKE_OPPFYLT", sak)

        val saker = listOf(sak).toDto()
        val sakstype = requireNotNull(saker.first().sakstyper) { "Mangler sakstype" }
        val vilkårsvurderinger = sakstype.flatMap { it.vilkårsvurderinger }
        assertTilstand(
            vilkårsvurderinger,
            "IKKE_OPPFYLT_MANUELT",
            Vilkårsvurdering.Paragraf.PARAGRAF_11_4,
            Vilkårsvurdering.Ledd.LEDD_1
        )
        assertTrue(søknad.behov().isEmpty())
    }

    private fun assertTilstand(actual: String, expected: Sak) {
        val dtoSak = listOf(expected).toDto().first()
        assertEquals(actual, dtoSak.tilstand)
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
