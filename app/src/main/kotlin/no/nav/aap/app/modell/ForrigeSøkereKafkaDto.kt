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
    val version: Int = 1,
) {
    data class Sak(
        val saksid: UUID,
        val tilstand: String,
        val sakstyper: List<Sakstype>,
        val vurderingsdato: LocalDate,
        val vurderingAvBeregningsdato: VurderingAvBeregningsdato,
        val søknadstidspunkt: LocalDateTime,
        val vedtak: Vedtak?
    ) {
        fun toDto() = SøkereKafkaDto.Sak(
            saksid = saksid,
            tilstand = tilstand,
            vurderingsdato = vurderingsdato,
            sakstyper = sakstyper.map(Sakstype::toDto),
            vurderingAvBeregningsdato = vurderingAvBeregningsdato.toDto(),
            søknadstidspunkt = søknadstidspunkt,
            vedtak = vedtak?.toDto()
        )
    }

    data class Sakstype(
        val type: String,
        val aktiv: Boolean,
        val vilkårsvurderinger: List<Vilkårsvurdering>,
    ) {
        fun toDto() = SøkereKafkaDto.Sakstype(
            type = type,
            aktiv = aktiv,
            vilkårsvurderinger = vilkårsvurderinger.map(Vilkårsvurdering::toDto)
        )
    }

    data class Vilkårsvurdering(
        val vilkårsvurderingsid: UUID,
        val vurdertAv: String?,
        val godkjentAv: String?,
        val paragraf: String,
        val ledd: List<String>,
        val tilstand: String,
        val utfall: String,
        val løsning_medlemskap_yrkesskade_maskinell: LøsningMaskinellMedlemskapYrkesskade? = null,
        val løsning_medlemskap_yrkesskade_manuell: LøsningManuellMedlemskapYrkesskade? = null,
        val løsning_11_2_maskinell: LøsningParagraf_11_2? = null,
        val løsning_11_2_manuell: LøsningParagraf_11_2? = null,
        val løsning_11_3_manuell: LøsningParagraf_11_3? = null,
        val løsning_11_4_ledd2_ledd3_manuell: LøsningParagraf_11_4_ledd2_ledd3? = null,
        val løsning_11_5_manuell: LøsningParagraf_11_5? = null,
        val løsning_11_5_yrkesskade_manuell: LøsningParagraf_11_5_yrkesskade? = null,
        val løsning_11_6_manuell: LøsningParagraf_11_6? = null,
        val løsning_11_12_ledd1_manuell: LøsningParagraf_11_12_ledd1? = null,
        val løsning_11_22_manuell: LøsningParagraf_11_22? = null,
        val løsning_11_29_manuell: LøsningParagraf_11_29? = null,
    ) {
        fun toDto() = SøkereKafkaDto.Vilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurderingsid,
            vurdertAv = vurdertAv,
            godkjentAv = godkjentAv,
            paragraf = paragraf,
            ledd = ledd,
            tilstand = tilstand,
            utfall = utfall,
            løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell?.toDto(),
            løsning_medlemskap_yrkesskade_manuell = løsning_medlemskap_yrkesskade_manuell?.toDto(),
            løsning_11_2_maskinell = løsning_11_2_maskinell?.toDto(),
            løsning_11_2_manuell = løsning_11_2_manuell?.toDto(),
            løsning_11_3_manuell = løsning_11_3_manuell?.toDto(),
            løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell?.toDto(),
            løsning_11_5_manuell = løsning_11_5_manuell?.toDto(),
            løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell?.toDto(),
            løsning_11_6_manuell = løsning_11_6_manuell?.toDto(),
            løsning_11_12_ledd1_manuell = løsning_11_12_ledd1_manuell?.toDto(),
            løsning_11_22_manuell = løsning_11_22_manuell?.toDto(),
            løsning_11_29_manuell = løsning_11_29_manuell?.toDto(),
        )
    }

    data class LøsningMaskinellMedlemskapYrkesskade(val erMedlem: String) {
        fun toDto() = SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade(erMedlem)
    }

    data class LøsningManuellMedlemskapYrkesskade(val erMedlem: String) {
        fun toDto() = SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade("saksbehandler", erMedlem)
    }

    data class LøsningParagraf_11_2(val erMedlem: String) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_2("saksbehandler", erMedlem)
    }

    data class LøsningParagraf_11_3(val erOppfylt: Boolean) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_3("saksbehandler", erOppfylt)
    }

    data class LøsningParagraf_11_4_ledd2_ledd3(val erOppfylt: Boolean) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_4_ledd2_ledd3("saksbehandler", erOppfylt)
    }

    data class LøsningParagraf_11_5(
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean
    ) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_5(
            vurdertAv = "veileder",
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    }

    data class LøsningParagraf_11_5_yrkesskade(
        val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
        val arbeidsevneErNedsattMedMinst30Prosent: Boolean
    ) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
            vurdertAv = "veileder",
            arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
            arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
        )
    }

    data class LøsningParagraf_11_6(
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean
    ) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_6(
            vurdertAv = "saksbehandler",
            harBehovForBehandling = harBehovForBehandling,
            harBehovForTiltak = harBehovForTiltak,
            harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
        )
    }

    data class LøsningParagraf_11_12_ledd1(
        val bestemmesAv: String,
        val unntak: String,
        val unntaksbegrunnelse: String,
        val manueltSattVirkningsdato: LocalDate
    ) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_12_ledd1(
            vurdertAv = "saksbehandler",
            bestemmesAv = bestemmesAv,
            unntak = unntak,
            unntaksbegrunnelse = unntaksbegrunnelse,
            manueltSattVirkningsdato = manueltSattVirkningsdato
        )
    }

    data class LøsningParagraf_11_22(
        val erOppfylt: Boolean,
        val andelNedsattArbeidsevne: Int,
        val år: Year,
        val antattÅrligArbeidsinntekt: Double
    ) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_22(
            vurdertAv = "saksbehandler",
            erOppfylt = erOppfylt,
            andelNedsattArbeidsevne = andelNedsattArbeidsevne,
            år = år,
            antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
        )
    }

    data class LøsningParagraf_11_29(val erOppfylt: Boolean) {
        fun toDto() = SøkereKafkaDto.LøsningParagraf_11_29("saksbehandler", erOppfylt)
    }

    data class Vedtak(
        val vedtaksid: UUID,
        val innvilget: Boolean,
        val inntektsgrunnlag: Inntektsgrunnlag,
        val vedtaksdato: LocalDate,
        val virkningsdato: LocalDate
    ) {
        fun toDto() = SøkereKafkaDto.Vedtak(
            vedtaksid = vedtaksid,
            innvilget = innvilget,
            inntektsgrunnlag = inntektsgrunnlag.toDto(),
            vedtaksdato = vedtaksdato,
            virkningsdato = virkningsdato,
        )
    }

    data class Inntektsgrunnlag(
        val beregningsdato: LocalDate,
        val inntekterSiste3Kalenderår: List<InntekterForBeregning>,
        val yrkesskade: Yrkesskade?,
        val fødselsdato: LocalDate,
        val sisteKalenderår: Year,
        val grunnlagsfaktor: Double
    ) {
        fun toDto() = SøkereKafkaDto.Inntektsgrunnlag(
            beregningsdato = beregningsdato,
            inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map { it.toDto() },
            yrkesskade = yrkesskade?.toDto(),
            fødselsdato = fødselsdato,
            sisteKalenderår = sisteKalenderår,
            grunnlagsfaktor = grunnlagsfaktor,
        )
    }

    data class InntekterForBeregning(
        val inntekter: List<Inntekt>,
        val inntektsgrunnlagForÅr: InntektsgrunnlagForÅr
    ) {
        fun toDto() = SøkereKafkaDto.InntekterForBeregning(
            inntekter = inntekter.map { it.toDto() },
            inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto(),
        )
    }

    data class VurderingAvBeregningsdato(
        val tilstand: String,
        val løsningVurderingAvBeregningsdato: LøsningVurderingAvBeregningsdato?
    ) {
        fun toDto() = SøkereKafkaDto.VurderingAvBeregningsdato(
            tilstand = tilstand,
            løsningVurderingAvBeregningsdato = løsningVurderingAvBeregningsdato?.toDto()
        )
    }

    data class LøsningVurderingAvBeregningsdato(
        val beregningsdato: LocalDate
    ) {
        fun toDto() = SøkereKafkaDto.LøsningVurderingAvBeregningsdato("saksbehandler", beregningsdato)
    }

    data class Inntekt(
        val arbeidsgiver: String,
        val inntekstmåned: YearMonth,
        val beløp: Double
    ) {
        fun toDto() = SøkereKafkaDto.Inntekt(
            arbeidsgiver = arbeidsgiver,
            inntekstmåned = inntekstmåned,
            beløp = beløp,
        )
    }

    data class InntektsgrunnlagForÅr(
        val år: Year,
        val beløpFørJustering: Double,
        val beløpJustertFor6G: Double,
        val erBeløpJustertFor6G: Boolean,
        val grunnlagsfaktor: Double
    ) {
        fun toDto() = SøkereKafkaDto.InntektsgrunnlagForÅr(
            år = år,
            beløpFørJustering = beløpFørJustering,
            beløpJustertFor6G = beløpJustertFor6G,
            erBeløpJustertFor6G = erBeløpJustertFor6G,
            grunnlagsfaktor = grunnlagsfaktor,
        )
    }

    data class Yrkesskade(
        val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
        val inntektsgrunnlag: InntektsgrunnlagForÅr
    ) {
        fun toDto() = SøkereKafkaDto.Yrkesskade(
            gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
            inntektsgrunnlag = inntektsgrunnlag.toDto(),
        )
    }

    fun toDto() = SøkereKafkaDto(
        personident = personident,
        fødselsdato = fødselsdato,
        saker = saker.map(Sak::toDto),
    )
}
