package no.nav.aap.frontendView

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth

data class FrontendSak(
    val personident: String,
    val fødselsdato: LocalDate,
    val tilstand: String,
    val vilkårsvurderinger: List<FrontendVilkårsvurdering>,
    val vedtak: FrontendVedtak?
)

data class FrontendVilkårsvurdering(
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val harÅpenOppgave: Boolean
)

data class FrontendVedtak(
    val innvilget: Boolean,
    val inntektsgrunnlag: FrontendInntektsgrunnlag,
    val søknadstidspunkt: LocalDateTime,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate
)

data class FrontendInntektsgrunnlag(
    val beregningsdato: LocalDate,
    val inntekterSiste3Kalenderår: List<FrontendInntektsgrunnlagForÅr>,
    val fødselsdato: LocalDate,
    val sisteKalenderår: Year,
    val grunnlagsfaktor: Double
)

data class FrontendInntektsgrunnlagForÅr(
    val år: Year,
    val inntekter: List<FrontendInntekt>,
    val beløpFørJustering: Double,
    val beløpJustertFor6G: Double,
    val erBeløpJustertFor6G: Boolean,
    val grunnlagsfaktor: Double
)

data class FrontendInntekt(
    val arbeidsgiver: String,
    val inntekstmåned: YearMonth,
    val beløp: Double
)
