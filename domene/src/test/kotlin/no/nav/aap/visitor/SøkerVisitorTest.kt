package no.nav.aap.visitor

import no.nav.aap.domene.Sakstype
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Grunnlagsfaktor
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.domene.vilkår.Vilkårsvurdering
import no.nav.aap.dto.*
import no.nav.aap.hendelse.*
import no.nav.aap.januar
import no.nav.aap.juli
import no.nav.aap.september
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SøkerVisitorTest {

    @Test
    fun `no required implementations`() {
        object : SøkerVisitor {}
    }

    @Test
    fun `inntektsgrunnlag til dto`() {
        val visitor = TestVisitor()

        val fødselsdato = Fødselsdato(17 juli 1995)
        val personident = Personident("12345678910")
        val søknad = Søknad(personident, fødselsdato)
        val søker = søknad.opprettSøker()
        søker.håndterSøknad(søknad)

        søker.håndterLøsning(LøsningParagraf_11_2(LøsningParagraf_11_2.ErMedlem.JA))
        søker.håndterLøsning(LøsningParagraf_11_3(true))
        søker.håndterLøsning(LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(50)))
        søker.håndterLøsning(LøsningParagraf_11_6(true))
        søker.håndterLøsning(LøsningParagraf_11_12FørsteLedd(true))
        søker.håndterLøsning(LøsningParagraf_11_29(true))
        søker.håndterLøsning(LøsningVurderingAvBeregningsdato(13 september 2021))
        søker.håndterLøsning(
            LøsningInntekter(
                listOf(
                    Inntekt(Arbeidsgiver("987654321"), januar(2020), 500000.beløp),
                    Inntekt(Arbeidsgiver("987654321"), januar(2019), 500000.beløp),
                    Inntekt(Arbeidsgiver("987654321"), januar(2018), 500000.beløp)
                )
            )
        )

        søker.accept(visitor)
        val dtoInntektsgrunnlag = visitor.søker.saker.mapNotNull { it.vedtak }.map { it.inntektsgrunnlag }.single()
        assertEquals(Year.of(2020), dtoInntektsgrunnlag.sisteKalenderår)
        assertEquals(13 september 2021, dtoInntektsgrunnlag.beregningsdato)
        assertEquals(17 juli 1995, dtoInntektsgrunnlag.fødselsdato)
        assertEquals(5.078089, dtoInntektsgrunnlag.grunnlagsfaktor)
        assertNull(dtoInntektsgrunnlag.yrkesskade)
        assertEquals(
            listOf(
                DtoInntekterForBeregning(
                    inntekter = listOf(DtoInntekt("987654321", januar(2020), 500000.0)),
                    inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
                        år = Year.of(2020),
                        beløpFørJustering = 500000.0,
                        beløpJustertFor6G = 500000.0,
                        erBeløpJustertFor6G = false,
                        grunnlagsfaktor = 4.957710,
                    )
                ),
                DtoInntekterForBeregning(
                    inntekter = listOf(DtoInntekt("987654321", januar(2019), 500000.0)),
                    inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
                        år = Year.of(2019),
                        beløpFørJustering = 500000.0,
                        beløpJustertFor6G = 500000.0,
                        erBeløpJustertFor6G = false,
                        grunnlagsfaktor = 5.057350,
                    )
                ),
                DtoInntekterForBeregning(
                    inntekter = listOf(DtoInntekt("987654321", januar(2018), 500000.0)),
                    inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
                        år = Year.of(2018),
                        beløpFørJustering = 500000.0,
                        beløpJustertFor6G = 500000.0,
                        erBeløpJustertFor6G = false,
                        grunnlagsfaktor = 5.219206,
                    )
                )
            ),
            dtoInntektsgrunnlag.inntekterSiste3Kalenderår
        )
    }
}

private class TestVisitor : SøkerVisitor {
    lateinit var søker: DtoSøker

    // søker
    private lateinit var søkerPersonident: String
    private lateinit var fødselsdato: LocalDate
    private val saker = mutableListOf<DtoSak>()

    // sak
    private lateinit var sakstilstand: String
    private val sakstyper = mutableListOf<DtoSakstype>()
    private lateinit var vurderingAvBeregningsdato: DtoVurderingAvBeregningsdato
    private var vurderingAvBeregningsdatoBeregningsdato: LocalDate? = null
    private lateinit var vurderingAvBeregningsdatoTilstand: String
    private lateinit var vedtak: DtoVedtak

    // sakstype
    private val vilkårsvurderinger = mutableListOf<DtoVilkårsvurdering>()

    // vilkårsvurdering
    private val inntekterSiste3Kalenderår = mutableListOf<DtoInntekterForBeregning>()
    private lateinit var vilkårsvurdering: DtoVilkårsvurdering

    // inntektsgrunnlag
    private lateinit var inntektsgrunnlag: DtoInntektsgrunnlag
    private var yrkesskade: DtoYrkesskade? = null
    private var grunnlagsfaktor: Double = Double.NaN

    override fun visitPersonident(ident: String) = let { søkerPersonident = ident }
    override fun visitFødselsdato(dato: LocalDate) = let { fødselsdato = dato }

    override fun preVisitSøker() {
        saker.clear()
    }

    override fun postVisitSøker() = let { søker = DtoSøker(søkerPersonident, fødselsdato, saker) }

    override fun visitSakTilstandStart() = let { sakstilstand = "START" }
    override fun visitSakTilstandSøknadMottatt() = let { sakstilstand = "SØKNAD_MOTTATT" }
    override fun visitSakTilstandBeregnInntekt() = let { sakstilstand = "BEREGN_INNTEKT" }
    override fun visitSakTilstandVedtakFattet() = let { sakstilstand = "VEDTAK_FATTET" }
    override fun visitSakTilstandIkkeOppfylt() = let { sakstilstand = "IKKE_OPPFYLT" }

    override fun preVisitSakstype(sakstypenavn: Sakstype.Type, aktiv: Boolean) = vilkårsvurderinger.clear()
    override fun postVisitSakstype(sakstypenavn: Sakstype.Type, aktiv: Boolean) {
        sakstyper.add(DtoSakstype(type = sakstypenavn.name, aktiv = aktiv, vilkårsvurderinger = vilkårsvurderinger))
    }

    override fun preVisitInntektsgrunnlag(beregningsdato: LocalDate, sisteKalenderår: Year) {
        inntekterSiste3Kalenderår.clear()
        yrkesskade = null
        grunnlagsfaktor = Double.NaN
    }

    // i kontekst av preVisitInntektsgrunnlag
    override fun preVisitInntekterForBeregning() {

    }

    // i kontekst av preVisitInntekterForBeregning
    override fun visitInntektsgrunnlagForÅr(
        år: Year,
        beløpFørJustering: Beløp,
        beløpJustertFor6G: Beløp,
        erBeløpJustertFor6G: Boolean,
        grunnlagsfaktor: Grunnlagsfaktor
    ) {
        inntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
            år,
                    beløpFørJustering,
                    beløpJustertFor6G,
                    erBeløpJustertFor6G,
                    grunnlagsfaktor ,
        )
    }

    // i kontekst av preVisitInntekterForBeregning eller preVisitInntektshistorikk
    override fun visitInntektshistorikk(arbeidsgiver: Arbeidsgiver, inntekstmåned: YearMonth, beløp: Beløp) {
        super.visitInntektshistorikk(arbeidsgiver, inntekstmåned, beløp)
    }

    override fun postVisitInntekterForBeregning() {
        super.postVisitInntekterForBeregning()
    }

    override fun visitVilkårsvurdering(
        tilstandsnavn: String,
        vilkårsvurderingsid: UUID,
        paragraf: Vilkårsvurdering.Paragraf,
        ledd: List<Vilkårsvurdering.Ledd>,
        måVurderesManuelt: Boolean
    ) {
        vilkårsvurdering = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurderingsid,
            paragraf = paragraf.name,
            ledd = ledd.map { it.name },
            tilstand = tilstandsnavn,
            måVurderesManuelt = måVurderesManuelt,
        )
    }

    override fun `postVisit §2`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §2`(maskinell: LøsningMaskinellMedlemskapYrkesskade) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_medlemskap_yrkesskade_maskinell = DtoLøsningMaskinellMedlemskapYrkesskade(
                    when {
                        !maskinell.erMedlem() && !maskinell.erIkkeMedlem() -> "UAVKLART"
                        maskinell.erMedlem() -> "JA"
                        else -> "NEI"
                    }
                ),
            )
        )
    }

    override fun `postVisit §2`(
        maskinell: LøsningMaskinellMedlemskapYrkesskade,
        manuell: LøsningManuellMedlemskapYrkesskade
    ) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_medlemskap_yrkesskade_maskinell = DtoLøsningMaskinellMedlemskapYrkesskade(
                    when {
                        !maskinell.erMedlem() && !maskinell.erIkkeMedlem() -> "UAVKLART"
                        maskinell.erMedlem() -> "JA"
                        else -> "NEI"
                    }
                ),
                løsning_medlemskap_yrkesskade_manuell = DtoLøsningManuellMedlemskapYrkesskade(
                    if (manuell.erMedlem()) "JA" else "NEI"
                )
            )
        )
    }

    override fun `postVisit §11-2`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-2`(maskinellLøsning: LøsningParagraf_11_2) {
        fun erMedlem(løsning: LøsningParagraf_11_2) = when {
            !løsning.erMedlem() && !løsning.erIkkeMedlem() -> "UAVKLART"
            løsning.erMedlem() -> "JA"
            else -> "NEI"
        }
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_2_maskinell = DtoLøsningParagraf_11_2(erMedlem(maskinellLøsning)),
            )
        )
    }

    override fun `postVisit §11-2`(maskinellLøsning: LøsningParagraf_11_2, manuellLøsning: LøsningParagraf_11_2) {
        fun erMedlem(løsning: LøsningParagraf_11_2) = when {
            !løsning.erMedlem() && !løsning.erIkkeMedlem() -> "UAVKLART"
            løsning.erMedlem() -> "JA"
            else -> "NEI"
        }

        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_2_maskinell = DtoLøsningParagraf_11_2(erMedlem(maskinellLøsning)),
                løsning_11_2_manuell = DtoLøsningParagraf_11_2(erMedlem(manuellLøsning)),
            )
        )
    }

    override fun `postVisit §11-3`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-3`(manuellLøsning: LøsningParagraf_11_3) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_3_manuell = DtoLøsningParagraf_11_3(manuellLøsning.erManueltOppfylt()),
            )
        )
    }

    override fun `preVisit §11-4 L1`(vurderingsdato: LocalDate) {
    }

    override fun `postVisit §11-4 L1`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-4 L1`(vurderingsdato: LocalDate) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                // todo: mangler støtte for vurderingstdato på aldersvurdering
                // fødselsdato skal være satt pga State.fødselsdato = State.Fødselsdato.P11_4_Ledd1
                // løsning_11_4_ledd1_manuell
            )
        )
    }

    override fun `postVisit §11-4 L2 L3`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-4 L2 L3`(manuellLøsning: LøsningParagraf_11_4AndreOgTredjeLedd) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_4_ledd2_ledd3_manuell = DtoLøsningParagraf_11_4_ledd2_ledd3(manuellLøsning.erManueltOppfylt()),
            )
        )
    }

    override fun `postVisit §11-5`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-5`(nedsattArbeidsevnegrad: LøsningParagraf_11_5.NedsattArbeidsevnegrad) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_5_manuell = DtoLøsningParagraf_11_5(
                    grad = -1, // fixme: deprecation
                    kravOmNedsattArbeidsevneErOppfylt = nedsattArbeidsevnegrad.erNedsattMedMinstHalvparten(), // todo: bruk riktig felt når det er implementert
                    nedsettelseSkyldesSykdomEllerSkade = nedsattArbeidsevnegrad.erNedsattMedMinstHalvparten(), // todo: bruk riktig felt når det er implementert
                ),
            )
        )
    }

    override fun `postVisit §11-5 yrkesskade`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-5 yrkesskade`(manuellLøsning: LøsningParagraf_11_5_yrkesskade) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_5_yrkesskade_manuell = DtoLøsningParagraf_11_5_yrkesskade(
                    arbeidsevneErNedsattMedMinst50Prosent = manuellLøsning.erNedsattMedMinst50Prosent(),
                    arbeidsevneErNedsattMedMinst30Prosent = manuellLøsning.erNedsattMedMinst30Prosent()
                )
            )
        )
    }

    override fun `postVisit §11-6`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `postVisit §11-6`(manuellLøsning: LøsningParagraf_11_6) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_6_manuell = DtoLøsningParagraf_11_6(erOppfylt = manuellLøsning.erManueltOppfylt())
            )
        )
    }

    override fun `postVisit §11-12 L1`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun `preVisit §11-12 L1`(løsning: LøsningParagraf_11_12FørsteLedd) {
        vilkårsvurderinger.add(
            vilkårsvurdering.copy(
                løsning_11_12_ledd1_manuell = DtoLøsningParagraf_11_12_ledd1(erOppfylt = løsning.erManueltOppfylt())
            )
        )
    }

    override fun `preVisit §11-22`() {
    }

    override fun `preVisit §11-22 løsning`(andelNedsattArbeidsevne: Int, år: Year) {

    }

    override fun `postVisit §11-22 løsning`(andelNedsattArbeidsevne: Int, år: Year) {

    }

    override fun visitBeløp(verdi: Double) {
        super.visitBeløp(verdi)
    }

    override fun `postVisit §11-22`() {
        vilkårsvurderinger.add(vilkårsvurdering)
    }

    override fun preVisitVurderingAvBeregningsdato() = let { vurderingAvBeregningsdatoBeregningsdato = null }
    override fun visitVurderingAvBeregningsdatoTilstandStart() = let { vurderingAvBeregningsdatoTilstand = "START" }
    override fun visitVurderingAvBeregningsdatoTilstandSøknadMottatt() =
        let { vurderingAvBeregningsdatoTilstand = "SØKNAD_MOTTATT" }

    override fun visitVurderingAvBeregningsdatoTilstandFerdig(beregningsdato: LocalDate) {
        vurderingAvBeregningsdatoTilstand = "FERDIG"
        vurderingAvBeregningsdatoBeregningsdato = beregningsdato
    }

    override fun postVisitVurderingAvBeregningsdato() {
        vurderingAvBeregningsdato = DtoVurderingAvBeregningsdato(
            tilstand = vurderingAvBeregningsdatoTilstand,
            løsningVurderingAvBeregningsdato = vurderingAvBeregningsdatoBeregningsdato?.let {
                DtoLøsningVurderingAvBeregningsdato(beregningsdato = it)
            },
        )
    }

    override fun postVisitVedtak(
        vedtaksid: UUID,
        innvilget: Boolean,
        vedtaksdato: LocalDate,
        virkningsdato: LocalDate
    ) {
        vedtak = DtoVedtak(
            vedtaksid = vedtaksid,
            innvilget = innvilget,
            inntektsgrunnlag = inntektsgrunnlag,
            vedtaksdato = vedtaksdato,
            virkningsdato = virkningsdato,
        )
    }

    override fun visitGrunnlagsfaktor(verdi: Double) {
        grunnlagsfaktor = verdi
    }

    override fun postVisitInntektsgrunnlag(beregningsdato: LocalDate, sisteKalenderår: Year) {
        inntektsgrunnlag = DtoInntektsgrunnlag(
            beregningsdato = beregningsdato,
            inntekterSiste3Kalenderår = listOf(),
            yrkesskade = yrkesskade,
            fødselsdato = fødselsdato,
            sisteKalenderår = sisteKalenderår,
            grunnlagsfaktor = grunnlagsfaktor,
        )
    }


    override fun preVisitSak(saksid: UUID, vurderingsdato: LocalDate, søknadstidspunkt: LocalDateTime) =
        sakstyper.clear()

    override fun postVisitSak(saksid: UUID, vurderingsdato: LocalDate, søknadstidspunkt: LocalDateTime) {
        saker.add(
            DtoSak(
                saksid = saksid,
                tilstand = sakstilstand,
                sakstyper = sakstyper,
                vurderingsdato = vurderingsdato,
                vurderingAvBeregningsdato = vurderingAvBeregningsdato,
                søknadstidspunkt = søknadstidspunkt,
                vedtak = vedtak,
            )
        )
    }
}
