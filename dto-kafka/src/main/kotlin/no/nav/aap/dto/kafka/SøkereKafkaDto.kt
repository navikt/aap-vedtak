package no.nav.aap.dto.kafka

import no.nav.aap.kafka.serde.json.Migratable
import no.nav.aap.kafka.streams.concurrency.Bufferable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*


data class SøkereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<SakKafkaDto>,
    val sekvensnummer: Long = INIT_SEKVENS,
    val version: Int = VERSION, // Denne bumpes ved hver migrering
) : Migratable, Bufferable<SøkereKafkaDto> {

    private var erMigrertAkkuratNå: Boolean = false

    companion object {
        const val VERSION = 22
        const val INIT_SEKVENS = 0L
    }

    data class SakKafkaDto(
        val saksid: UUID,
        val tilstand: String,
        val sakstyper: List<SakstypeKafkaDto>,
        val vurderingsdato: LocalDate,
        val søknadstidspunkt: LocalDateTime,
        val vedtak: VedtakKafkaDto?
    )

    data class SakstypeKafkaDto(
        val type: String,
        val aktiv: Boolean,
        val medlemskapYrkesskade: MedlemskapYrkesskadeKafkaDto?,
        val paragraf_8_48: Paragraf_8_48KafkaDto?,
        val paragraf_11_2: Paragraf_11_2KafkaDto?,
        val paragraf_11_3: Paragraf_11_3KafkaDto?,
        val paragraf_11_4FørsteLedd: Paragraf_11_4FørsteLeddKafkaDto?,
        val paragraf_11_4AndreOgTredjeLedd: Paragraf_11_4AndreOgTredjeLeddKafkaDto?,
        val paragraf_11_5: Paragraf_11_5KafkaDto?,
        val paragraf_11_5Yrkesskade: Paragraf_11_5YrkesskadeKafkaDto?,
        val paragraf_11_6: Paragraf_11_6KafkaDto?,
        val paragraf_11_14: Paragraf_11_14KafkaDto?,
        val paragraf_11_19: Paragraf_11_19KafkaDto?,
        val paragraf_11_22: Paragraf_11_22KafkaDto?,
        val paragraf_11_27FørsteLedd: Paragraf_11_27FørsteLeddKafkaDto?,
        val paragraf_11_29: Paragraf_11_29KafkaDto?,
        val paragraf_22_13: Paragraf_22_13KafkaDto?,
    )

    data class MedlemskapYrkesskadeKafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val løsning_medlemskap_yrkesskade_maskinell: List<LøsningMaskinellMedlemskapYrkesskadeKafkaDto>,
        val totrinnskontroller: List<TotrinnskontrollMedlemskapYrkesskadeKafkaDto>,
    )

    data class TotrinnskontrollMedlemskapYrkesskadeKafkaDto(
        val løsning: LøsningManuellMedlemskapYrkesskadeKafkaDto,
        val kvalitetssikring: KvalitetssikringMedlemskapYrkesskadeKafkaDto?
    )

    data class Paragraf_8_48KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val løsning_8_48_maskinell: List<LøsningMaskinellParagraf_8_48KafkaDto>,
        val totrinnskontroller: List<Totrinnskontroll_22_13KafkaDto>,
    )

    data class Paragraf_11_2KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val løsning_11_2_maskinell: List<LøsningMaskinellParagraf_11_2KafkaDto>,
        val totrinnskontroller: List<Totrinnskontroll_11_2KafkaDto>,
    )

    data class Totrinnskontroll_11_2KafkaDto(
        val løsning: LøsningManuellParagraf_11_2KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_2KafkaDto?
    )

    data class Paragraf_11_3KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_3KafkaDto>,
    )

    data class Totrinnskontroll_11_3KafkaDto(
        val løsning: LøsningParagraf_11_3KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_3KafkaDto?
    )

    data class Paragraf_11_4FørsteLeddKafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
    )

    data class Paragraf_11_4AndreOgTredjeLeddKafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto>,
    )

    data class Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto(
        val løsning: LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto?
    )

    data class Paragraf_11_5KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_5KafkaDto>,
    )

    data class Totrinnskontroll_11_5KafkaDto(
        val løsning: LøsningParagraf_11_5KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_5KafkaDto?
    )

    data class Paragraf_11_5YrkesskadeKafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_5YrkesskadeKafkaDto>,
    )

    data class Totrinnskontroll_11_5YrkesskadeKafkaDto(
        val løsning: LøsningParagraf_11_5_yrkesskadeKafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_5YrkesskadeKafkaDto?
    )

    data class Paragraf_11_6KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val innstillinger_11_6: List<InnstillingParagraf_11_6KafkaDto>,
        val totrinnskontroller: List<Totrinnskontroll_11_6KafkaDto>,
    )

    data class Totrinnskontroll_11_6KafkaDto(
        val løsning: LøsningParagraf_11_6KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_6KafkaDto?
    )

    data class Paragraf_11_14KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
    )

    data class Paragraf_11_19KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_19KafkaDto>,
    )

    data class Totrinnskontroll_11_19KafkaDto(
        val løsning: LøsningParagraf_11_19KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_19KafkaDto?
    )

    data class Paragraf_11_22KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_22KafkaDto>,
    )

    data class Totrinnskontroll_11_22KafkaDto(
        val løsning: LøsningParagraf_11_22KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_22KafkaDto?
    )

    data class Paragraf_11_27FørsteLeddKafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val løsning_11_27_maskinell: List<LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto>,
        val totrinnskontroller: List<Totrinnskontroll_22_13KafkaDto>,
    )

    data class Paragraf_11_29KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_11_29KafkaDto>,
    )

    data class Totrinnskontroll_11_29KafkaDto(
        val løsning: LøsningParagraf_11_29KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_11_29KafkaDto?
    )

    data class Paragraf_22_13KafkaDto(
        val vilkårsvurderingsid: UUID,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val vurdertMaskinelt: Boolean,
        val totrinnskontroller: List<Totrinnskontroll_22_13KafkaDto>,
        val søknadsdata: List<SøknadsdataParagraf_22_13KafkaDto>,
    )

    data class Totrinnskontroll_22_13KafkaDto(
        val løsning: LøsningParagraf_22_13KafkaDto,
        val kvalitetssikring: KvalitetssikringParagraf_22_13KafkaDto?
    )

    data class SøknadsdataParagraf_22_13KafkaDto(
        val søknadId: UUID,
        val søknadstidspunkt: LocalDateTime,
    )

    data class InnstillingParagraf_11_6KafkaDto(
        val innstillingId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean,
        val individuellBegrunnelse: String?,
    )

    data class LøsningMaskinellMedlemskapYrkesskadeKafkaDto(
        val løsningId: UUID,
        val erMedlem: String
    )

    data class LøsningManuellMedlemskapYrkesskadeKafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningMaskinellParagraf_8_48KafkaDto(
        val løsningId: UUID,
        val tidspunktForVurdering: LocalDateTime,
        val sykepengedager: Sykepengedager?
    ) {
        data class Sykepengedager(
            val gjenståendeSykedager: Int,
            val foreløpigBeregnetSluttPåSykepenger: LocalDate,
            val kilde: String,
        )
    }

    data class LøsningMaskinellParagraf_11_2KafkaDto(
        val løsningId: UUID,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningManuellParagraf_11_2KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erMedlem: String
    )

    data class LøsningParagraf_11_3KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_11_5KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val kravOmNedsattArbeidsevneErOppfyltBegrunnelse: String,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean,
        val nedsettelseSkyldesSykdomEllerSkadeBegrunnelse: String,
        val kilder: List<String>,
        //FIXME: Skal denne være nullable?
        val legeerklæringDato: LocalDate?,
        //FIXME: Skal denne være nullable?
        val sykmeldingDato: LocalDate?,
    )

    data class LøsningParagraf_11_5_yrkesskadeKafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
        val arbeidsevneErNedsattMedMinst30Prosent: Boolean
    )

    data class LøsningParagraf_11_6KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean,
        val individuellBegrunnelse: String?,
    )

    data class LøsningParagraf_11_19KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val beregningsdato: LocalDate
    )

    data class LøsningParagraf_11_22KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean,
        val andelNedsattArbeidsevne: Int,
        val år: Year,
        val antattÅrligArbeidsinntekt: Double
    )

    data class LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto(
        val løsningId: UUID,
        val tidspunktForVurdering: LocalDateTime,
        val svangerskapspenger: Svangerskapspenger
    ) {
        data class Svangerskapspenger(
            val fom: LocalDate?,
            val tom: LocalDate?,
            val grad: Double?,
            val vedtaksdato: LocalDate?
        )
    }

    data class LøsningParagraf_11_29KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
    )

    data class LøsningParagraf_22_13KafkaDto(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val bestemmesAv: String,
        val unntak: String?,
        val unntaksbegrunnelse: String?,
        val manueltSattVirkningsdato: LocalDate?,
    )

    data class KvalitetssikringMedlemskapYrkesskadeKafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_2KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_3KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_5KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val kravOmNedsattArbeidsevneErGodkjent: Boolean,
        val kravOmNedsattArbeidsevneErGodkjentBegrunnelse: String?,
        val nedsettelseSkyldesSykdomEllerSkadeErGodkjent: Boolean,
        val nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse: String?
    )

    data class KvalitetssikringParagraf_11_5YrkesskadeKafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_6KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_22_13KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_22KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_19KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class KvalitetssikringParagraf_11_29KafkaDto(
        val kvalitetssikringId: UUID,
        val løsningId: UUID,
        val kvalitetssikretAv: String,
        val tidspunktForKvalitetssikring: LocalDateTime,
        val erGodkjent: Boolean,
        val begrunnelse: String?,
    )

    data class VedtakKafkaDto(
        val vedtaksid: UUID,
        val innvilget: Boolean,
        val inntektsgrunnlag: InntektsgrunnlagKafkaDto,
        val vedtaksdato: LocalDate,
        val virkningsdato: LocalDate
    )

    data class InntektsgrunnlagKafkaDto(
        val beregningsdato: LocalDate,
        val inntekterSiste3Kalenderår: List<InntekterForBeregningKafkaDto>,
        val yrkesskade: YrkesskadeKafkaDto?,
        val fødselsdato: LocalDate,
        val sisteKalenderår: Year,
        val grunnlagsfaktor: Double
    )

    data class InntekterForBeregningKafkaDto(
        val inntekter: List<InntektKafkaDto>,
        val inntektsgrunnlagForÅr: InntektsgrunnlagForÅrKafkaDto
    )

    data class InntektKafkaDto(
        val arbeidsgiver: String,
        val inntekstmåned: YearMonth,
        val beløp: Double
    )

    data class InntektsgrunnlagForÅrKafkaDto(
        val år: Year,
        val beløpFørJustering: Double,
        val beløpJustertFor6G: Double,
        val erBeløpJustertFor6G: Boolean,
        val grunnlagsfaktor: Double
    )

    data class YrkesskadeKafkaDto(
        val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
        val inntektsgrunnlag: InntektsgrunnlagForÅrKafkaDto
    )

    override fun markerSomMigrertAkkuratNå() {
        erMigrertAkkuratNå = true
    }

    override fun erMigrertAkkuratNå(): Boolean = erMigrertAkkuratNå

    override fun erNyere(other: SøkereKafkaDto): Boolean = sekvensnummer > other.sekvensnummer
}
