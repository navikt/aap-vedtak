package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*

data class ForrigeSøkereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<Sak>,
    val version: Int = 9,
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
        val vurdertMaskinelt: Boolean,
        val løsning_medlemskap_yrkesskade_maskinell: List<LøsningMaskinellMedlemskapYrkesskade>? = null,
        val løsning_medlemskap_yrkesskade_manuell: List<LøsningManuellMedlemskapYrkesskade>? = null,
        val løsning_11_2_maskinell: List<LøsningMaskinellParagraf_11_2>? = null,
        val løsning_11_2_manuell: List<LøsningManuellParagraf_11_2>? = null,
        val løsning_11_3_manuell: List<LøsningParagraf_11_3>? = null,
        val løsning_11_4_ledd2_ledd3_manuell: List<LøsningParagraf_11_4_ledd2_ledd3>? = null,
        val løsning_11_5_manuell: List<LøsningParagraf_11_5>? = null,
        val løsning_11_5_yrkesskade_manuell: List<LøsningParagraf_11_5_yrkesskade>? = null,
        val løsning_11_6_manuell: List<LøsningParagraf_11_6>? = null,
        val løsning_22_13_manuell: List<LøsningParagraf_22_13>? = null,
        val løsning_11_19_manuell: List<LøsningParagraf_11_19>? = null,
        val løsning_11_22_manuell: List<LøsningParagraf_11_22>? = null,
        val løsning_11_29_manuell: List<LøsningParagraf_11_29>? = null,
        val kvalitetssikringer_medlemskap_yrkesskade: List<KvalitetssikringMedlemskapYrkesskade>? = null,
        val kvalitetssikringer_11_2: List<KvalitetssikringParagraf_11_2>? = null,
        val kvalitetssikringer_11_3: List<KvalitetssikringParagraf_11_3>? = null,
        val kvalitetssikringer_11_4_ledd2_ledd3: List<KvalitetssikringParagraf_11_4AndreOgTredjeLedd>? = null,
        val kvalitetssikringer_11_5: List<KvalitetssikringParagraf_11_5>? = null,
        val kvalitetssikringer_11_5_yrkesskade: List<KvalitetssikringParagraf_11_5Yrkesskade>? = null,
        val kvalitetssikringer_11_6: List<KvalitetssikringParagraf_11_6>? = null,
        val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13>? = null,
        val kvalitetssikringer_11_19: List<KvalitetssikringParagraf_11_19>? = null,
        val kvalitetssikringer_11_22: List<KvalitetssikringParagraf_11_22>? = null,
        val kvalitetssikringer_11_29: List<KvalitetssikringParagraf_11_29>? = null,
    )

    data class LøsningMaskinellMedlemskapYrkesskade(
        val løsningId: UUID,
        val erMedlem: String
    )

    data class LøsningManuellMedlemskapYrkesskade(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningMaskinellParagraf_11_2(
        val løsningId: UUID,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningManuellParagraf_11_2(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningParagraf_11_3(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_11_4_ledd2_ledd3(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_11_5(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean
    )

    data class LøsningParagraf_11_5_yrkesskade(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
        val arbeidsevneErNedsattMedMinst30Prosent: Boolean
    )

    data class LøsningParagraf_11_6(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean
    )

    data class LøsningParagraf_22_13(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val bestemmesAv: String,
        val unntak: String,
        val unntaksbegrunnelse: String,
        val manueltSattVirkningsdato: LocalDate?,
    )

    data class LøsningParagraf_11_19(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val beregningsdato: LocalDate
    )

    data class LøsningParagraf_11_22(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean,
        val andelNedsattArbeidsevne: Int,
        val år: Year,
        val antattÅrligArbeidsinntekt: Double
    )

    data class LøsningParagraf_11_29(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class KvalitetssikringMedlemskapYrkesskade(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_2(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_3(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_5(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_5Yrkesskade(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_6(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_22_13(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_22(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_19(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
    )

    data class KvalitetssikringParagraf_11_29(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String
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
