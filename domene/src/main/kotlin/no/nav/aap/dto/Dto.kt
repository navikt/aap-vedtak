package no.nav.aap.dto

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.hendelse.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth

data class DtoSøker(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<DtoSak>
)

data class DtoSak(
    val tilstand: String,
    val sakstyper: List<DtoSakstype>,
    val vurderingsdato: LocalDate,
    val vurderingAvBeregningsdato: DtoVurderingAvBeregningsdato,
    val vedtak: DtoVedtak?
)

data class DtoSakstype(
    val type: String,
    val vilkårsvurderinger: List<DtoVilkårsvurdering>
)

data class DtoVilkårsvurdering(
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val måVurderesManuelt: Boolean,
    val løsning_11_2_maskinell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_2_manuell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_3_manuell: DtoLøsningParagraf_11_3? = null,
    val løsning_11_4_ledd2_ledd3_manuell: DtoLøsningParagraf_11_4_ledd2_ledd3? = null,
    val løsning_11_5_manuell: DtoLøsningParagraf_11_5? = null,
    val løsning_11_6_manuell: DtoLøsningParagraf_11_6? = null,
    val løsning_11_12_ledd1_manuell: DtoLøsningParagraf_11_12_ledd1? = null,
    val løsning_11_29_manuell: DtoLøsningParagraf_11_29? = null,
)

data class DtoManuell(
    val løsning_11_2_manuell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_3_manuell: DtoLøsningParagraf_11_3? = null,
    val løsning_11_4_ledd2_ledd3_manuell: DtoLøsningParagraf_11_4_ledd2_ledd3? = null,
    val løsning_11_5_manuell: DtoLøsningParagraf_11_5? = null,
    val løsning_11_6_manuell: DtoLøsningParagraf_11_6? = null,
    val løsning_11_12_ledd1_manuell: DtoLøsningParagraf_11_12_ledd1? = null,
    val løsning_11_29_manuell: DtoLøsningParagraf_11_29? = null,
    val løsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato? = null,
)

data class DtoLøsningParagraf_11_2(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_2(enumValueOf(erMedlem))
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_3(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_3(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_4_ledd2_ledd3(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_4AndreOgTredjeLedd(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_5(val grad: Int) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_5(LøsningParagraf_11_5.NedsattArbeidsevnegrad(grad))
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_6(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_6(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_12_ledd1(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_12FørsteLedd(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_29(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_29(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoVurderingAvBeregningsdato(
    val tilstand: String,
    val løsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato?
)

data class DtoLøsningVurderingAvBeregningsdato(
    val beregningsdato: LocalDate
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningVurderingAvBeregningsdato(beregningsdato)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoVedtak(
    val innvilget: Boolean,
    val inntektsgrunnlag: DtoInntektsgrunnlag,
    val søknadstidspunkt: LocalDateTime,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate
)

data class DtoInntektsgrunnlag(
    val beregningsdato: LocalDate,
    val inntekterSiste3Kalenderår: List<DtoInntektsgrunnlagForÅr>,
    val fødselsdato: LocalDate,
    val sisteKalenderår: Year,
    val grunnlagsfaktor: Double
)

data class DtoInntektsgrunnlagForÅr(
    val år: Year,
    val inntekter: List<DtoInntekt>,
    val beløpFørJustering: Double,
    val beløpJustertFor6G: Double,
    val erBeløpJustertFor6G: Boolean,
    val grunnlagsfaktor: Double
)

data class DtoInntekt(
    val arbeidsgiver: String,
    val inntekstmåned: YearMonth,
    val beløp: Double
)

data class DtoInntekter(
    val inntekter: List<DtoInntekt>
) {
    fun håndter(søker: Søker) {
        søker.håndterLøsning(LøsningInntekter(inntekter.map {
            Inntekt(
                arbeidsgiver = Arbeidsgiver(it.arbeidsgiver),
                inntekstmåned = it.inntekstmåned,
                beløp = it.beløp.beløp
            )
        }))
    }
}
