package no.nav.aap.app.modell

import no.nav.aap.app.modell.SøkereKafkaDto.*
import no.nav.aap.dto.*

internal fun SøkereKafkaDto.toDto() = DtoSøker(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(Sak::toDto),
)

private fun Sak.toDto() = DtoSak(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(Sakstype::toDto),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toDto()
)

private fun Sakstype.toDto() = DtoSakstype(
    type = type,
    aktiv = aktiv,
    vilkårsvurderinger = vilkårsvurderinger.map(Vilkårsvurdering::toDto)
)

private fun Vilkårsvurdering.toDto() = DtoVilkårsvurdering(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = godkjentAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell?.map { it.toDto() },
    løsning_medlemskap_yrkesskade_manuell = løsning_medlemskap_yrkesskade_manuell?.map { it.toDto() },
    løsning_11_2_maskinell = løsning_11_2_maskinell?.map { it.toDto() },
    løsning_11_2_manuell = løsning_11_2_manuell?.map { it.toDto() },
    løsning_11_3_manuell = løsning_11_3_manuell?.map { it.toDto() },
    løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell?.map { it.toDto() },
    løsning_11_5_manuell = løsning_11_5_manuell?.map { it.toDto() },
    løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell?.map { it.toDto() },
    løsning_11_6_manuell = løsning_11_6_manuell?.map { it.toDto() },
    løsning_11_12_ledd1_manuell = løsning_11_12_ledd1_manuell?.map { it.toDto() },
    løsning_11_19_manuell = løsning_11_19_manuell?.map { it.toDto() },
    løsning_11_22_manuell = løsning_11_22_manuell?.map { it.toDto() },
    løsning_11_29_manuell = løsning_11_29_manuell?.map { it.toDto() },
)

private fun LøsningMaskinellMedlemskapYrkesskade.toDto() = DtoLøsningMaskinellMedlemskapYrkesskade(
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskade.toDto() = DtoLøsningManuellMedlemskapYrkesskade(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningMaskinellParagraf_11_2.toDto() = DtoLøsningMaskinellParagraf_11_2(
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2.toDto() = DtoLøsningParagraf_11_2(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3.toDto() = DtoLøsningParagraf_11_3(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4_ledd2_ledd3.toDto() = DtoLøsningParagraf_11_4AndreOgTredjeLedd(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_5.toDto() = DtoLøsningParagraf_11_5(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
)

private fun LøsningParagraf_11_5_yrkesskade.toDto() = DtoLøsningParagraf_11_5Yrkesskade(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6.toDto() = DtoLøsningParagraf_11_6(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun LøsningParagraf_11_12_ledd1.toDto() = DtoLøsningParagraf_11_12FørsteLedd(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun LøsningParagraf_11_19.toDto() = DtoLøsningParagraf_11_19(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22.toDto() = DtoLøsningParagraf_11_22(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningParagraf_11_29.toDto() = DtoLøsningParagraf_11_29(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun Vedtak.toDto() = DtoVedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun Inntektsgrunnlag.toDto() = DtoInntektsgrunnlag(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map { it.toDto() },
    yrkesskade = yrkesskade?.toDto(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregning.toDto() = DtoInntekterForBeregning(
    inntekter = inntekter.map { it.toDto() },
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto(),
)

private fun Inntekt.toDto() = DtoInntekt(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅr.toDto() = DtoInntektsgrunnlagForÅr(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun Yrkesskade.toDto() = DtoYrkesskade(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
)
