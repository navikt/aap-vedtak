package no.nav.aap.app.modell

import no.nav.aap.dto.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*

data class SøkereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<Sak>,
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
        fun toDto() = DtoSak(
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
        fun toDto() = DtoSakstype(
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
        fun toDto() = DtoVilkårsvurdering(
            vilkårsvurderingsid = vilkårsvurderingsid,
            vurdertAv = vurdertAv,
            godkjentAv = godkjentAv,
            paragraf = paragraf,
            ledd = ledd,
            tilstand = tilstand,
            utfall = enumValueOf(utfall),
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
        fun toDto() = DtoLøsningMaskinellMedlemskapYrkesskade(erMedlem)
    }

    data class LøsningManuellMedlemskapYrkesskade(val erMedlem: String) {
        fun toDto() = DtoLøsningManuellMedlemskapYrkesskade(erMedlem)
    }

    data class LøsningParagraf_11_2(val erMedlem: String) {
        fun toDto() = DtoLøsningParagraf_11_2(erMedlem)
    }

    data class LøsningParagraf_11_3(val erOppfylt: Boolean) {
        fun toDto() = DtoLøsningParagraf_11_3(erOppfylt)
    }

    data class LøsningParagraf_11_4_ledd2_ledd3(val erOppfylt: Boolean) {
        fun toDto() = DtoLøsningParagraf_11_4_ledd2_ledd3(erOppfylt)
    }

    data class LøsningParagraf_11_5(
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean
    ) {
        fun toDto() = DtoLøsningParagraf_11_5(
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    }

    data class LøsningParagraf_11_5_yrkesskade(
        val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
        val arbeidsevneErNedsattMedMinst30Prosent: Boolean
    ) {
        fun toDto() = DtoLøsningParagraf_11_5_yrkesskade(
            arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
            arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
        )
    }

    data class LøsningParagraf_11_6(
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean
    ) {
        fun toDto() = DtoLøsningParagraf_11_6(
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
        fun toDto() = DtoLøsningParagraf_11_12_ledd1(
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
        fun toDto() = DtoLøsningParagraf_11_22(
            erOppfylt = erOppfylt,
            andelNedsattArbeidsevne = andelNedsattArbeidsevne,
            år = år,
            antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
        )
    }

    data class LøsningParagraf_11_29(val erOppfylt: Boolean) {
        fun toDto() = DtoLøsningParagraf_11_29(erOppfylt)
    }

    data class Vedtak(
        val vedtaksid: UUID,
        val innvilget: Boolean,
        val inntektsgrunnlag: Inntektsgrunnlag,
        val vedtaksdato: LocalDate,
        val virkningsdato: LocalDate
    ) {
        fun toDto() = DtoVedtak(
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
        fun toDto() = DtoInntektsgrunnlag(
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
        fun toDto() = DtoInntekterForBeregning(
            inntekter = inntekter.map { it.toDto() },
            inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto(),
        )
    }

    data class VurderingAvBeregningsdato(
        val tilstand: String,
        val løsningVurderingAvBeregningsdato: LøsningVurderingAvBeregningsdato?
    ) {
        fun toDto() = DtoVurderingAvBeregningsdato(
            tilstand = tilstand,
            løsningVurderingAvBeregningsdato = løsningVurderingAvBeregningsdato?.toDto()
        )
    }

    data class LøsningVurderingAvBeregningsdato(
        val vurdertAv: String = "saksbehandler", //FIXME: Feltet mangler i oppgavestyring
        val beregningsdato: LocalDate
    ) {
        fun toDto() = DtoLøsningVurderingAvBeregningsdato(vurdertAv, beregningsdato)
    }

    data class Inntekt(
        val arbeidsgiver: String,
        val inntekstmåned: YearMonth,
        val beløp: Double
    ) {
        fun toDto() = DtoInntekt(
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
        fun toDto() = DtoInntektsgrunnlagForÅr(
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
        fun toDto() = DtoYrkesskade(
            gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
            inntektsgrunnlag = inntektsgrunnlag.toDto(),
        )
    }

    fun toDto() = DtoSøker(
        personident = personident,
        fødselsdato = fødselsdato,
        saker = saker.map(Sak::toDto),
    )
}

fun DtoSøker.toJson() = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map { sak ->
        SøkereKafkaDto.Sak(
            saksid = sak.saksid,
            tilstand = sak.tilstand,
            sakstyper = sak.sakstyper.map { sakstype ->
                SøkereKafkaDto.Sakstype(
                    type = sakstype.type,
                    aktiv = sakstype.aktiv,
                    vilkårsvurderinger = sakstype.vilkårsvurderinger.map { vilkår ->
                        SøkereKafkaDto.Vilkårsvurdering(
                            vilkårsvurderingsid = vilkår.vilkårsvurderingsid,
                            vurdertAv = vilkår.vurdertAv,
                            godkjentAv = vilkår.godkjentAv,
                            paragraf = vilkår.paragraf,
                            ledd = vilkår.ledd,
                            tilstand = vilkår.tilstand,
                            utfall = vilkår.utfall.name,
                            løsning_medlemskap_yrkesskade_maskinell = vilkår.løsning_medlemskap_yrkesskade_maskinell?.let {
                                SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade(erMedlem = it.erMedlem)
                            },
                            løsning_medlemskap_yrkesskade_manuell = vilkår.løsning_medlemskap_yrkesskade_manuell?.let {
                                SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade(erMedlem = it.erMedlem)
                            },
                            løsning_11_2_maskinell = vilkår.løsning_11_2_maskinell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_2(erMedlem = it.erMedlem)
                            },
                            løsning_11_2_manuell = vilkår.løsning_11_2_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_2(erMedlem = it.erMedlem)
                            },
                            løsning_11_3_manuell = vilkår.løsning_11_3_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_3(erOppfylt = it.erOppfylt)
                            },
                            løsning_11_4_ledd2_ledd3_manuell = vilkår.løsning_11_4_ledd2_ledd3_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_4_ledd2_ledd3(erOppfylt = it.erOppfylt)
                            },
                            løsning_11_5_manuell = vilkår.løsning_11_5_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_5(
                                    kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                                    nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                                )
                            },
                            løsning_11_5_yrkesskade_manuell = vilkår.løsning_11_5_yrkesskade_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
                                    arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent,
                                    arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                                )
                            },
                            løsning_11_6_manuell = vilkår.løsning_11_6_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_6(
                                    harBehovForBehandling = it.harBehovForBehandling,
                                    harBehovForTiltak = it.harBehovForTiltak,
                                    harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid
                                )
                            },
                            løsning_11_12_ledd1_manuell = vilkår.løsning_11_12_ledd1_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_12_ledd1(
                                    bestemmesAv = it.bestemmesAv,
                                    unntak = it.unntak,
                                    unntaksbegrunnelse = it.unntaksbegrunnelse,
                                    manueltSattVirkningsdato = it.manueltSattVirkningsdato
                                )
                            },
                            løsning_11_22_manuell = vilkår.løsning_11_22_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_22(
                                    erOppfylt = it.erOppfylt,
                                    andelNedsattArbeidsevne = it.andelNedsattArbeidsevne,
                                    år = it.år,
                                    antattÅrligArbeidsinntekt = it.antattÅrligArbeidsinntekt
                                )
                            },
                            løsning_11_29_manuell = vilkår.løsning_11_29_manuell?.let {
                                SøkereKafkaDto.LøsningParagraf_11_29(erOppfylt = it.erOppfylt)
                            },
                        )
                    }
                )
            },
            vurderingsdato = sak.vurderingsdato,
            vurderingAvBeregningsdato = SøkereKafkaDto.VurderingAvBeregningsdato(
                tilstand = sak.vurderingAvBeregningsdato.tilstand,
                løsningVurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.løsningVurderingAvBeregningsdato?.let {
                    SøkereKafkaDto.LøsningVurderingAvBeregningsdato(
                        vurdertAv = it.vurdertAv,
                        beregningsdato = it.beregningsdato
                    )
                }
            ),
            søknadstidspunkt = sak.søknadstidspunkt,
            vedtak = sak.vedtak?.let { vedtak ->
                SøkereKafkaDto.Vedtak(
                    vedtaksid = vedtak.vedtaksid,
                    innvilget = vedtak.innvilget,
                    inntektsgrunnlag = vedtak.inntektsgrunnlag.let { inntektsgrunnlag ->
                        SøkereKafkaDto.Inntektsgrunnlag(
                            beregningsdato = inntektsgrunnlag.beregningsdato,
                            inntekterSiste3Kalenderår = inntektsgrunnlag.inntekterSiste3Kalenderår.map { siste3år ->
                                SøkereKafkaDto.InntekterForBeregning(
                                    inntekter = siste3år.inntekter.map { inntekt ->
                                        SøkereKafkaDto.Inntekt(
                                            arbeidsgiver = inntekt.arbeidsgiver,
                                            inntekstmåned = inntekt.inntekstmåned,
                                            beløp = inntekt.beløp,
                                        )
                                    },
                                    inntektsgrunnlagForÅr = siste3år.inntektsgrunnlagForÅr.let { år ->
                                        SøkereKafkaDto.InntektsgrunnlagForÅr(
                                            år = år.år,
                                            beløpFørJustering = år.beløpFørJustering,
                                            beløpJustertFor6G = år.beløpJustertFor6G,
                                            erBeløpJustertFor6G = år.erBeløpJustertFor6G,
                                            grunnlagsfaktor = år.grunnlagsfaktor,
                                        )
                                    }
                                )
                            },
                            yrkesskade = inntektsgrunnlag.yrkesskade?.let { yrkesskade ->
                                SøkereKafkaDto.Yrkesskade(
                                    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = yrkesskade.gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
                                    inntektsgrunnlag = yrkesskade.inntektsgrunnlag.let { år ->
                                        SøkereKafkaDto.InntektsgrunnlagForÅr(
                                            år = år.år,
                                            beløpFørJustering = år.beløpFørJustering,
                                            beløpJustertFor6G = år.beløpJustertFor6G,
                                            erBeløpJustertFor6G = år.erBeløpJustertFor6G,
                                            grunnlagsfaktor = år.grunnlagsfaktor,
                                        )
                                    }
                                )
                            },
                            fødselsdato = inntektsgrunnlag.fødselsdato,
                            sisteKalenderår = inntektsgrunnlag.sisteKalenderår,
                            grunnlagsfaktor = inntektsgrunnlag.grunnlagsfaktor,
                        )
                    },
                    vedtaksdato = vedtak.vedtaksdato,
                    virkningsdato = vedtak.virkningsdato,
                )
            }
        )
    },
)
