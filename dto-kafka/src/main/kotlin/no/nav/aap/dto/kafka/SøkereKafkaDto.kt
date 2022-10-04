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
    val saker: List<Sak>,
    val sekvensnummer: Long = INIT_SEKVENS,
    val version: Int = VERSION, // Denne bumpes ved hver migrering
) : Migratable, Bufferable<SøkereKafkaDto> {

    private var erMigrertAkkuratNå: Boolean = false

    companion object {
        const val VERSION = 11
        const val INIT_SEKVENS = 0L
    }

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

    sealed interface Vilkårsvurdering {
        val vilkårsvurderingsid: UUID
        val vurdertAv: String?
        val kvalitetssikretAv: String?
        val paragraf: String
        val ledd: List<String>
        val tilstand: String
        val utfall: String
        val vurdertMaskinelt: Boolean
    }

    data class MedlemskapYrkesskade(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_medlemskap_yrkesskade_maskinell: List<LøsningMaskinellMedlemskapYrkesskade>,
        val løsning_medlemskap_yrkesskade_manuell: List<LøsningManuellMedlemskapYrkesskade>,
        val kvalitetssikringer_medlemskap_yrkesskade: List<KvalitetssikringMedlemskapYrkesskade>,
    ) : Vilkårsvurdering

    data class Paragraf_8_48(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_8_48_maskinell: List<LøsningMaskinellParagraf_8_48>,
        val løsning_22_13_manuell: List<LøsningParagraf_22_13>,
        val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13>,
    ) : Vilkårsvurdering

    data class Paragraf_11_2(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_2_maskinell: List<LøsningMaskinellParagraf_11_2>,
        val løsning_11_2_manuell: List<LøsningManuellParagraf_11_2>,
        val kvalitetssikringer_11_2: List<KvalitetssikringParagraf_11_2>,
    ) : Vilkårsvurdering

    data class Paragraf_11_3(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_3_manuell: List<LøsningParagraf_11_3>,
        val kvalitetssikringer_11_3: List<KvalitetssikringParagraf_11_3>,
    ) : Vilkårsvurdering

    data class Paragraf_11_4FørsteLedd(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
    ) : Vilkårsvurdering

    data class Paragraf_11_4AndreOgTredjeLedd(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_4_ledd2_ledd3_manuell: List<LøsningParagraf_11_4AndreOgTredjeLedd>,
        val kvalitetssikringer_11_4_ledd2_ledd3: List<KvalitetssikringParagraf_11_4AndreOgTredjeLedd>,
    ) : Vilkårsvurdering

    data class Paragraf_11_5(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_5_manuell: List<LøsningParagraf_11_5>,
        val kvalitetssikringer_11_5: List<KvalitetssikringParagraf_11_5>,
    ) : Vilkårsvurdering

    data class Paragraf_11_5Yrkesskade(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_5_yrkesskade_manuell: List<LøsningParagraf_11_5_yrkesskade>,
        val kvalitetssikringer_11_5_yrkesskade: List<KvalitetssikringParagraf_11_5Yrkesskade>,
    ) : Vilkårsvurdering

    data class Paragraf_11_6(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_6_manuell: List<LøsningParagraf_11_6>,
        val kvalitetssikringer_11_6: List<KvalitetssikringParagraf_11_6>,
    ) : Vilkårsvurdering

    data class Paragraf_11_14(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
    ) : Vilkårsvurdering

    data class Paragraf_11_19(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_19_manuell: List<LøsningParagraf_11_19>,
        val kvalitetssikringer_11_19: List<KvalitetssikringParagraf_11_19>,
    ) : Vilkårsvurdering

    data class Paragraf_11_22(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_22_manuell: List<LøsningParagraf_11_22>,
        val kvalitetssikringer_11_22: List<KvalitetssikringParagraf_11_22>,
    ) : Vilkårsvurdering

    data class Paragraf_11_27FørsteLedd(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_27_maskinell: List<LøsningMaskinellParagraf_11_27FørsteLedd>,
        val løsning_22_13_manuell: List<LøsningParagraf_22_13>,
        val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13>,
    ) : Vilkårsvurdering

    data class Paragraf_11_29(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_11_29_manuell: List<LøsningParagraf_11_29>,
        val kvalitetssikringer_11_29: List<KvalitetssikringParagraf_11_29>,
    ) : Vilkårsvurdering

    data class Paragraf_22_13(
        override val vilkårsvurderingsid: UUID,
        override val vurdertAv: String?,
        override val kvalitetssikretAv: String?,
        override val paragraf: String,
        override val ledd: List<String>,
        override val tilstand: String,
        override val utfall: String,
        override val vurdertMaskinelt: Boolean,
        val løsning_22_13_manuell: List<LøsningParagraf_22_13>,
        val kvalitetssikringer_22_13: List<KvalitetssikringParagraf_22_13>,
    ) : Vilkårsvurdering

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

    data class LøsningMaskinellParagraf_8_48(
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

    data class LøsningParagraf_11_4AndreOgTredjeLedd(
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

    data class LøsningMaskinellParagraf_11_27FørsteLedd(
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

    data class LøsningParagraf_11_29(
        val løsningId: UUID,
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean
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

    override fun markerSomMigrertAkkuratNå() {
        erMigrertAkkuratNå = true
    }

    override fun erMigrertAkkuratNå(): Boolean = erMigrertAkkuratNå

    override fun erNyere(other: SøkereKafkaDto): Boolean = sekvensnummer > other.sekvensnummer
}
