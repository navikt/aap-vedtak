package no.nav.aap.app.modell

import no.nav.aap.app.modell.ForrigeSøkereKafkaDto.*
import java.time.LocalDateTime
import java.util.*

internal fun ForrigeSøkereKafkaDto.toDto() = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(Sak::toDto),
)

private fun Sak.toDto() = SøkereKafkaDto.Sak(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map { it.toDto() },
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toDto()
)

private fun Sakstype.toDto() = SøkereKafkaDto.Sakstype(
    type = type,
    aktiv = aktiv,
    vilkårsvurderinger = vilkårsvurderinger.map(Vilkårsvurdering::toDto)
)

private fun Vilkårsvurdering.toDto() = SøkereKafkaDto.Vilkårsvurdering(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    godkjentAv = godkjentAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
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

private fun LøsningMaskinellMedlemskapYrkesskade.toDto() = SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade(
    løsningId = UUID.randomUUID(),
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskade.toDto() = SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningMaskinellParagraf_11_2.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_11_2(
    løsningId = UUID.randomUUID(),
    tidspunktForVurdering = LocalDateTime.now(),
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2.toDto() = SøkereKafkaDto.LøsningManuellParagraf_11_2(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3.toDto() = SøkereKafkaDto.LøsningParagraf_11_3(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4_ledd2_ledd3.toDto() = SøkereKafkaDto.LøsningParagraf_11_4_ledd2_ledd3(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_5.toDto() = SøkereKafkaDto.LøsningParagraf_11_5(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
)

private fun LøsningParagraf_11_5_yrkesskade.toDto() = SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6.toDto() = SøkereKafkaDto.LøsningParagraf_11_6(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun LøsningParagraf_11_12_ledd1.toDto() = SøkereKafkaDto.LøsningParagraf_11_12_ledd1(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun LøsningParagraf_11_19.toDto() = SøkereKafkaDto.LøsningParagraf_11_19(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22.toDto() = SøkereKafkaDto.LøsningParagraf_11_22(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningParagraf_11_29.toDto() = SøkereKafkaDto.LøsningParagraf_11_29(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun Vedtak.toDto() = SøkereKafkaDto.Vedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun Inntektsgrunnlag.toDto() = SøkereKafkaDto.Inntektsgrunnlag(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map { it.toDto() },
    yrkesskade = yrkesskade?.toDto(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregning.toDto() = SøkereKafkaDto.InntekterForBeregning(
    inntekter = inntekter.map { it.toDto() },
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto(),
)

private fun Inntekt.toDto() = SøkereKafkaDto.Inntekt(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅr.toDto() = SøkereKafkaDto.InntektsgrunnlagForÅr(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun Yrkesskade.toDto() = SøkereKafkaDto.Yrkesskade(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
)
