package no.nav.aap.app.modell

import no.nav.aap.avro.sokere.v1.*
import no.nav.aap.dto.*
import java.time.Year
import java.time.YearMonth

fun Soker.toDto(): DtoSøker = DtoSøker(
    personident = personident,
    fødselsdato = fodselsdato,
    saker = saker.map(Sak::toDto),
)

fun Sak.toDto(): DtoSak = DtoSak(
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(Sakstype::toDto),
    vurderingAvBeregningsdato = vurderingAvBeregningsdato.toDto(),
    vedtak = vedtak?.toDto()
)

fun Sakstype.toDto(): DtoSakstype = DtoSakstype(
    type = type,
    vilkårsvurderinger = vilkarsvurderinger.map(Vilkarsvurdering::toDto)
)

fun Vilkarsvurdering.toDto(): DtoVilkårsvurdering = DtoVilkårsvurdering(
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    måVurderesManuelt = maVurderesManuelt,
    løsning_11_2_manuell = losning112Manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_2_maskinell = losning112Maskinell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_3_manuell = losning113Manuell?.let { DtoLøsningParagraf_11_3(it.erOppfylt) },
    løsning_11_4_ledd2_ledd3_manuell = losning114L2L3Manuell?.let { DtoLøsningParagraf_11_4_ledd2_ledd3(it.erOppfylt) },
    løsning_11_5_manuell = losning115Manuell?.let { DtoLøsningParagraf_11_5(it.grad) },
    løsning_11_6_manuell = losning116Manuell?.let { DtoLøsningParagraf_11_6(it.erOppfylt) },
    løsning_11_12_ledd1_manuell = losning1112L1Manuell?.let { DtoLøsningParagraf_11_12_ledd1(it.erOppfylt) },
    løsning_11_29_manuell = losning1129Manuell?.let { DtoLøsningParagraf_11_29(it.erOppfylt) },
)

fun VurderingAvBeregningsdato.toDto(): DtoVurderingAvBeregningsdato = DtoVurderingAvBeregningsdato(
    tilstand = tilstand,
    løsningVurderingAvBeregningsdato = losningVurderingAvBeregningsdato?.toDto()
)

fun LosningVurderingAvBeregningsdato.toDto(): DtoLøsningVurderingAvBeregningsdato = DtoLøsningVurderingAvBeregningsdato(
    beregningsdato = beregningsdato
)

fun Inntekt.toDto(): DtoInntekt = DtoInntekt(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = YearMonth.from(inntektsmaned),
    beløp = belop
)

fun Vedtak.toDto(): DtoVedtak = DtoVedtak(
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
    søknadstidspunkt = soknadstidspunkt,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato
)

fun Inntektsgrunnlag.toDto(): DtoInntektsgrunnlag = DtoInntektsgrunnlag(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderar.map { it.toDto() },
    fødselsdato = fodselsdato,
    sisteKalenderår = Year.from(sisteKalenderar),
    grunnlagsfaktor = grunnlagsfaktor
)

fun InntektsgrunnlagForAr.toDto(): DtoInntektsgrunnlagForÅr = DtoInntektsgrunnlagForÅr(
    år = Year.from(ar),
    inntekter = inntekter.map { it.toDto() },
    beløpFørJustering = belopForJustering,
    beløpJustertFor6G = belopJustertFor6G,
    erBeløpJustertFor6G = erBelopJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor
)

fun DtoSøker.toAvro(): Soker = Soker.newBuilder()
    .setPersonident(personident)
    .setFodselsdato(fødselsdato)
    .setSaker(
        saker.map { sak ->
            Sak.newBuilder()
                .setTilstand(sak.tilstand)
                .setSakstyper(
                    sak.sakstyper.map { sakstype ->
                        Sakstype.newBuilder()
                            .setType(sakstype.type)
                            .setVilkarsvurderinger(
                                sakstype.vilkårsvurderinger.map { vilkår ->
                                    Vilkarsvurdering.newBuilder()
                                        .setLedd(vilkår.ledd)
                                        .setParagraf(vilkår.paragraf)
                                        .setTilstand(vilkår.tilstand)
                                        .setMaVurderesManuelt(vilkår.måVurderesManuelt)
                                        .setLosning112Manuell(vilkår.løsning_11_2_manuell?.let {
                                            Losning_11_2(it.erMedlem)
                                        }).setLosning112Maskinell(vilkår.løsning_11_2_maskinell?.let {
                                            Losning_11_2(it.erMedlem)
                                        }).setLosning113Manuell(vilkår.løsning_11_3_manuell?.let {
                                            Losning_11_3(it.erOppfylt)
                                        }).setLosning114L2L3Manuell(vilkår.løsning_11_4_ledd2_ledd3_manuell?.let {
                                            Losning_11_4_l2_l3(it.erOppfylt)
                                        }).setLosning115Manuell(vilkår.løsning_11_5_manuell?.let {
                                            Losning_11_5(it.grad)
                                        }).setLosning116Manuell(vilkår.løsning_11_6_manuell?.let {
                                            Losning_11_6(it.erOppfylt)
                                        }).setLosning1112L1Manuell(vilkår.løsning_11_12_ledd1_manuell?.let {
                                            Losning_11_12_l1(it.erOppfylt)
                                        }).setLosning1129Manuell(vilkår.løsning_11_29_manuell?.let {
                                            Losning_11_29(it.erOppfylt)
                                        }).build()
                                }
                            )
                            .build()
                    }
                )
                .setVurderingsdato(sak.vurderingsdato)
                .setVurderingAvBeregningsdato(
                    VurderingAvBeregningsdato.newBuilder()
                        .setTilstand(sak.vurderingAvBeregningsdato.tilstand)
                        .setLosningVurderingAvBeregningsdato(sak.vurderingAvBeregningsdato.løsningVurderingAvBeregningsdato?.let {
                            LosningVurderingAvBeregningsdato(it.beregningsdato)
                        })
                        .build()
                )
                .setVedtak(sak.vedtak?.let { vedtak ->
                    Vedtak(
                        vedtak.innvilget,
                        vedtak.inntektsgrunnlag.let { inntektsgrunnlag ->
                            Inntektsgrunnlag(
                                inntektsgrunnlag.beregningsdato,
                                inntektsgrunnlag.inntekterSiste3Kalenderår.map { inntektsgrunnlagForÅr ->
                                    InntektsgrunnlagForAr(
                                        inntektsgrunnlagForÅr.år.atDay(1),
                                        inntektsgrunnlagForÅr.inntekter.map { inntekt ->
                                            Inntekt(
                                                inntekt.arbeidsgiver,
                                                inntekt.inntekstmåned.atDay(1),
                                                inntekt.beløp
                                            )
                                        },
                                        inntektsgrunnlagForÅr.beløpFørJustering,
                                        inntektsgrunnlagForÅr.beløpJustertFor6G,
                                        inntektsgrunnlagForÅr.erBeløpJustertFor6G,
                                        inntektsgrunnlagForÅr.grunnlagsfaktor
                                    )
                                },
                                inntektsgrunnlag.fødselsdato,
                                inntektsgrunnlag.sisteKalenderår.atDay(1),
                                inntektsgrunnlag.grunnlagsfaktor
                            )
                        },
                        vedtak.søknadstidspunkt,
                        vedtak.vedtaksdato,
                        vedtak.virkningsdato
                    )
                })
                .build()
        }
    ).build()
