package no.nav.aap.app.modell

import no.nav.aap.app.modell.SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningManuellParagraf_11_2.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningMaskinellParagraf_11_2.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_12_ledd1.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_22.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_29.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_3.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_4_ledd2_ledd3.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_5.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningParagraf_11_6.Companion.toDto
import no.nav.aap.app.modell.SøkereKafkaDto.LøsningVurderingAvBeregningsdato.Companion.toDto
import no.nav.aap.dto.*
import no.nav.aap.kafka.serde.json.Migratable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*

data class SøkereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<Sak>,
    val version: Int = VERSION, // Denne bumpes ved hver migrering
) : Migratable {

    private var erMigrertAkkuratNå: Boolean = false

    internal companion object {
        internal const val VERSION = 4
    }

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
        val løsning_11_22_manuell: List<LøsningParagraf_11_22>? = null,
        val løsning_11_29_manuell: List<LøsningParagraf_11_29>? = null,
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
        internal companion object {
            internal fun Iterable<LøsningMaskinellMedlemskapYrkesskade>.toDto() = map(LøsningMaskinellMedlemskapYrkesskade::toDto)
        }

        fun toDto() = DtoLøsningMaskinellMedlemskapYrkesskade(erMedlem)
    }

    data class LøsningManuellMedlemskapYrkesskade(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime, val erMedlem: String) {
        internal companion object {
            internal fun Iterable<LøsningManuellMedlemskapYrkesskade>.toDto() = map(LøsningManuellMedlemskapYrkesskade::toDto)
        }
        fun toDto() = DtoLøsningManuellMedlemskapYrkesskade(vurdertAv, tidspunktForVurdering, erMedlem)
    }

    data class LøsningMaskinellParagraf_11_2(val erMedlem: String) {
        internal companion object {
            internal fun Iterable<LøsningMaskinellParagraf_11_2>.toDto() = map(LøsningMaskinellParagraf_11_2::toDto)
        }
        fun toDto() = DtoLøsningMaskinellParagraf_11_2(erMedlem)
    }

    data class LøsningManuellParagraf_11_2(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime, val erMedlem: String) {
        internal companion object {
            internal fun Iterable<LøsningManuellParagraf_11_2>.toDto() = map(LøsningManuellParagraf_11_2::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_2(vurdertAv, tidspunktForVurdering, erMedlem)
    }

    data class LøsningParagraf_11_3(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime, val erOppfylt: Boolean) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_3>.toDto() = map(LøsningParagraf_11_3::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_3(vurdertAv, tidspunktForVurdering, erOppfylt)
    }

    data class LøsningParagraf_11_4_ledd2_ledd3(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime, val erOppfylt: Boolean) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_4_ledd2_ledd3>.toDto() = map(LøsningParagraf_11_4_ledd2_ledd3::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_4_ledd2_ledd3(vurdertAv, tidspunktForVurdering, erOppfylt)
    }

    data class LøsningParagraf_11_5(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val kravOmNedsattArbeidsevneErOppfylt: Boolean,
        val nedsettelseSkyldesSykdomEllerSkade: Boolean
    ) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_5>.toDto() = map(LøsningParagraf_11_5::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_5(
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    }

    data class LøsningParagraf_11_5_yrkesskade(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
        val arbeidsevneErNedsattMedMinst30Prosent: Boolean
    ) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_5_yrkesskade>.toDto() = map(LøsningParagraf_11_5_yrkesskade::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_5_yrkesskade(
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
            arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
        )
    }

    data class LøsningParagraf_11_6(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val harBehovForBehandling: Boolean,
        val harBehovForTiltak: Boolean,
        val harMulighetForÅKommeIArbeid: Boolean
    ) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_6>.toDto() = map(LøsningParagraf_11_6::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_6(
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            harBehovForBehandling = harBehovForBehandling,
            harBehovForTiltak = harBehovForTiltak,
            harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
        )
    }

    data class LøsningParagraf_11_12_ledd1(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val bestemmesAv: String,
        val unntak: String,
        val unntaksbegrunnelse: String,
        val manueltSattVirkningsdato: LocalDate
    ) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_12_ledd1>.toDto() = map(LøsningParagraf_11_12_ledd1::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_12_ledd1(
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            bestemmesAv = bestemmesAv,
            unntak = unntak,
            unntaksbegrunnelse = unntaksbegrunnelse,
            manueltSattVirkningsdato = manueltSattVirkningsdato
        )
    }

    data class LøsningParagraf_11_22(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val erOppfylt: Boolean,
        val andelNedsattArbeidsevne: Int,
        val år: Year,
        val antattÅrligArbeidsinntekt: Double
    ) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_22>.toDto() = map(LøsningParagraf_11_22::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_22(
            vurdertAv = vurdertAv,
            tidspunktForVurdering = tidspunktForVurdering,
            erOppfylt = erOppfylt,
            andelNedsattArbeidsevne = andelNedsattArbeidsevne,
            år = år,
            antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
        )
    }

    data class LøsningParagraf_11_29(val vurdertAv: String,  val tidspunktForVurdering: LocalDateTime, val erOppfylt: Boolean) {
        internal companion object {
            internal fun Iterable<LøsningParagraf_11_29>.toDto() = map(LøsningParagraf_11_29::toDto)
        }
        fun toDto() = DtoLøsningParagraf_11_29(vurdertAv, tidspunktForVurdering, erOppfylt)
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
        val løsningVurderingAvBeregningsdato: List<LøsningVurderingAvBeregningsdato>?
    ) {
        fun toDto() = DtoVurderingAvBeregningsdato(
            tilstand = tilstand,
            løsningVurderingAvBeregningsdato = løsningVurderingAvBeregningsdato?.toDto()
        )
    }

    data class LøsningVurderingAvBeregningsdato(
        val vurdertAv: String,
        val tidspunktForVurdering: LocalDateTime,
        val beregningsdato: LocalDate
    ) {
        internal companion object {
            internal fun Iterable<LøsningVurderingAvBeregningsdato>.toDto() = map(LøsningVurderingAvBeregningsdato::toDto)
        }
        fun toDto() = DtoLøsningVurderingAvBeregningsdato(vurdertAv, tidspunktForVurdering, beregningsdato)
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

    override fun markerSomMigrertAkkuratNå() {
        erMigrertAkkuratNå = true
    }

    override fun erMigrertAkkuratNå(): Boolean = erMigrertAkkuratNå
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
                            løsning_medlemskap_yrkesskade_maskinell = vilkår.løsning_medlemskap_yrkesskade_maskinell?.map {
                                SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade(erMedlem = it.erMedlem)
                            },
                            løsning_medlemskap_yrkesskade_manuell = vilkår.løsning_medlemskap_yrkesskade_manuell?.map {
                                SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_2_maskinell = vilkår.løsning_11_2_maskinell?.map {
                                SøkereKafkaDto.LøsningMaskinellParagraf_11_2(
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_2_manuell = vilkår.løsning_11_2_manuell?.map {
                                SøkereKafkaDto.LøsningManuellParagraf_11_2(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_3_manuell = vilkår.løsning_11_3_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_3(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            løsning_11_4_ledd2_ledd3_manuell = vilkår.løsning_11_4_ledd2_ledd3_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_4_ledd2_ledd3(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            løsning_11_5_manuell = vilkår.løsning_11_5_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_5(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                                    nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                                )
                            },
                            løsning_11_5_yrkesskade_manuell = vilkår.løsning_11_5_yrkesskade_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent,
                                    arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                                )
                            },
                            løsning_11_6_manuell = vilkår.løsning_11_6_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_6(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    harBehovForBehandling = it.harBehovForBehandling,
                                    harBehovForTiltak = it.harBehovForTiltak,
                                    harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid
                                )
                            },
                            løsning_11_12_ledd1_manuell = vilkår.løsning_11_12_ledd1_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_12_ledd1(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    bestemmesAv = it.bestemmesAv,
                                    unntak = it.unntak,
                                    unntaksbegrunnelse = it.unntaksbegrunnelse,
                                    manueltSattVirkningsdato = it.manueltSattVirkningsdato
                                )
                            },
                            løsning_11_22_manuell = vilkår.løsning_11_22_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_22(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt,
                                    andelNedsattArbeidsevne = it.andelNedsattArbeidsevne,
                                    år = it.år,
                                    antattÅrligArbeidsinntekt = it.antattÅrligArbeidsinntekt
                                )
                            },
                            løsning_11_29_manuell = vilkår.løsning_11_29_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_29(
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                        )
                    }
                )
            },
            vurderingsdato = sak.vurderingsdato,
            vurderingAvBeregningsdato = SøkereKafkaDto.VurderingAvBeregningsdato(
                tilstand = sak.vurderingAvBeregningsdato.tilstand,
                løsningVurderingAvBeregningsdato = sak.vurderingAvBeregningsdato.løsningVurderingAvBeregningsdato?.map {
                    SøkereKafkaDto.LøsningVurderingAvBeregningsdato(
                        vurdertAv = it.vurdertAv,
                        tidspunktForVurdering = it.tidspunktForVurdering,
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
