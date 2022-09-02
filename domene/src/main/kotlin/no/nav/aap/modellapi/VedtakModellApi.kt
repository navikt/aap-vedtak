package no.nav.aap.modellapi

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.hendelse.LøsningInntekter
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*

data class VedtakModellApi(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val inntektsgrunnlag: InntektsgrunnlagModellApi,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate
)

data class InntektsgrunnlagModellApi(
    val beregningsdato: LocalDate,
    val inntekterSiste3Kalenderår: List<InntekterForBeregningModellApi>,
    val yrkesskade: YrkesskadeModellApi?,
    val fødselsdato: LocalDate,
    val sisteKalenderår: Year,
    val grunnlagsfaktor: Double
)

data class InntekterForBeregningModellApi(
    val inntekter: List<InntektModellApi>,
    val inntektsgrunnlagForÅr: InntektsgrunnlagForÅrModellApi
)

data class InntektsgrunnlagForÅrModellApi(
    val år: Year,
    val beløpFørJustering: Double,
    val beløpJustertFor6G: Double,
    val erBeløpJustertFor6G: Boolean,
    val grunnlagsfaktor: Double
)

data class YrkesskadeModellApi(
    val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
    val inntektsgrunnlag: InntektsgrunnlagForÅrModellApi
)

data class InntektModellApi(
    val arbeidsgiver: String,
    val inntekstmåned: YearMonth,
    val beløp: Double
)

data class InntekterModellApi(
    val inntekter: List<InntektModellApi>
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