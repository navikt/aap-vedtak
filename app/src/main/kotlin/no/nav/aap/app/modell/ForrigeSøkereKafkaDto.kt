package no.nav.aap.app.modell

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*

data class ForrigeSøkereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<Sak>,
    val version: Int = 5,
) {

    data class Sak(
        val saksid: UUID,
        val tilstand: String,
        val sakstyper: List<Sakstype>,
        val vurderingsdato: LocalDate,
        val søknadstidspunkt: LocalDateTime,
        val vedtak: Vedtak?
    )

    data class Sakstype(
        val type: String,
        val aktiv: Boolean,
        val vilkårsvurderinger: List<Vilkårsvurdering>,
    )

    data class Vilkårsvurdering(
        val vilkårsvurderingsid: UUID,
        val vurdertAv: String?,
        val godkjentAv: String?,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val løsning_medlemskap_yrkesskade_maskinell: List<LøsningMaskinellMedlemskapYrkesskade>? = null,
        val løsning_medlemskap_yrkesskade_manuell: List<LøsningManuellMedlemskapYrkesskade>? = null,
        val løsning_11_2_maskinell: List<LøsningMaskinellParagraf_11_2>? = null,
        val løsning_11_2_manuell: List<LøsningManuellParagraf_11_2>? = null,
        val løsning_11_3_manuell: List<LøsningParagraf_11_3>? = null,
        val løsning_11_4_ledd2_ledd3_manuell: List<LøsningParagraf_11_4_ledd2_ledd3>? = null,
        val løsning_11_5_manuell: List<LøsningParagraf_11_5>? = null,
        val løsning_11_5_yrkesskade_manuell: List<LøsningParagraf_11_5_yrkesskade>? = null,
        val løsning_11_6_manuell: List<LøsningParagraf_11_6>? = null,
        val løsning_11_12_ledd1_manuell: List<LøsningParagraf_11_12_ledd1>? = null,
        val løsning_11_19_manuell: List<LøsningParagraf_11_19>? = null,
        val løsning_11_22_manuell: List<LøsningParagraf_11_22>? = null,
        val løsning_11_29_manuell: List<LøsningParagraf_11_29>? = null,
    )

    data class LøsningMaskinellMedlemskapYrkesskade(val erMedlem: String)

    data class LøsningManuellMedlemskapYrkesskade(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningMaskinellParagraf_11_2(val erMedlem: String)

    data class LøsningManuellParagraf_11_2(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningParagraf_11_3(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_11_4_ledd2_ledd3(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_11_5(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean
    )

    data class LøsningParagraf_11_5_yrkesskade(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
        val arbeidsevneErNedsattMedMinst30Prosent: Boolean
    )

    data class LøsningParagraf_11_6(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean
    )

    data class LøsningParagraf_11_12_ledd1(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val bestemmesAv: String,
        val unntak: String,
        val unntaksbegrunnelse: String,
        val manueltSattVirkningsdato: LocalDate
    )

    data class LøsningParagraf_11_19(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val beregningsdato: LocalDate
    )

    data class LøsningParagraf_11_22(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean,
        val andelNedsattArbeidsevne: Int,
        val år: Year,
        val antattÅrligArbeidsinntekt: Double
    )

    data class LøsningParagraf_11_29(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class Vedtak(
        val vedtaksid: UUID,
        val innvilget: Boolean,
        val inntektsgrunnlag: Inntektsgrunnlag,
        val vedtaksdato: LocalDate,
        val virkningsdato: LocalDate
    )

    data class Inntektsgrunnlag(
        val beregningsdato: LocalDate,
        val inntekterSiste3Kalenderår: List<InntekterForBeregning>,
        val yrkesskade: Yrkesskade?,
        val fødselsdato: LocalDate,
        val sisteKalenderår: Year,
        val grunnlagsfaktor: Double
    )

    data class InntekterForBeregning(
        val inntekter: List<Inntekt>,
        val inntektsgrunnlagForÅr: InntektsgrunnlagForÅr
    )

    data class Inntekt(
        val arbeidsgiver: String,
        val inntekstmåned: YearMonth,
        val beløp: Double
    )

    data class InntektsgrunnlagForÅr(
        val år: Year,
        val beløpFørJustering: Double,
        val beløpJustertFor6G: Double,
        val erBeløpJustertFor6G: Boolean,
        val grunnlagsfaktor: Double
    )

    data class Yrkesskade(
        val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
        val inntektsgrunnlag: InntektsgrunnlagForÅr
    )
}
