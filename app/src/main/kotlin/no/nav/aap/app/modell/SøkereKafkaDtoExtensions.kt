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
    kvalitetssikringer_medlemskap_yrkesskade = kvalitetssikringer_medlemskap_yrkesskade?.map { it.toDto() },
    kvalitetssikringer_11_2 = kvalitetssikringer_11_2?.map { it.toDto() },
    kvalitetssikringer_11_3 = kvalitetssikringer_11_3?.map { it.toDto() },
    kvalitetssikringer_11_4_ledd2_ledd3 = kvalitetssikringer_11_4_ledd2_ledd3?.map { it.toDto() },
    kvalitetssikringer_11_5 = kvalitetssikringer_11_5?.map { it.toDto() },
    kvalitetssikringer_11_5_yrkesskade = kvalitetssikringer_11_5_yrkesskade?.map { it.toDto() },
    kvalitetssikringer_11_6 = kvalitetssikringer_11_6?.map { it.toDto() },
    kvalitetssikringer_11_12_ledd1 = kvalitetssikringer_11_12_ledd1?.map { it.toDto() },
    kvalitetssikringer_11_19 = kvalitetssikringer_11_19?.map { it.toDto() },
    kvalitetssikringer_11_22 = kvalitetssikringer_11_22?.map { it.toDto() },
    kvalitetssikringer_11_29 = kvalitetssikringer_11_29?.map { it.toDto() },
)

private fun LøsningMaskinellMedlemskapYrkesskade.toDto() = DtoLøsningMaskinellMedlemskapYrkesskade(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskade.toDto() = DtoLøsningManuellMedlemskapYrkesskade(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningMaskinellParagraf_11_2.toDto() = DtoLøsningMaskinellParagraf_11_2(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2.toDto() = DtoLøsningParagraf_11_2(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3.toDto() = DtoLøsningParagraf_11_3(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4_ledd2_ledd3.toDto() = DtoLøsningParagraf_11_4AndreOgTredjeLedd(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_5.toDto() = DtoLøsningParagraf_11_5(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
)

private fun LøsningParagraf_11_5_yrkesskade.toDto() = DtoLøsningParagraf_11_5Yrkesskade(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6.toDto() = DtoLøsningParagraf_11_6(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun LøsningParagraf_11_12_ledd1.toDto() = DtoLøsningParagraf_11_12FørsteLedd(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun LøsningParagraf_11_19.toDto() = DtoLøsningParagraf_11_19(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22.toDto() = DtoLøsningParagraf_11_22(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningParagraf_11_29.toDto() = DtoLøsningParagraf_11_29(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringMedlemskapYrkesskade.toDto() = DtoKvalitetssikringMedlemskapYrkesskade(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_2.toDto() = DtoKvalitetssikringParagraf_11_2(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_3.toDto() = DtoKvalitetssikringParagraf_11_3(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLedd.toDto() = DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_5.toDto() = DtoKvalitetssikringParagraf_11_5(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_5Yrkesskade.toDto() = DtoKvalitetssikringParagraf_11_5Yrkesskade(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_6.toDto() = DtoKvalitetssikringParagraf_11_6(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_12FørsteLedd.toDto() = DtoKvalitetssikringParagraf_11_12FørsteLedd(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_19.toDto() = DtoKvalitetssikringParagraf_11_19(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_22.toDto() = DtoKvalitetssikringParagraf_11_22(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_29.toDto() = DtoKvalitetssikringParagraf_11_29(
    kvalitetssikringId = kvalitetssikringId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
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
                            godkjentAv = vilkår.kvalitetssikretAv,
                            paragraf = vilkår.paragraf,
                            ledd = vilkår.ledd,
                            tilstand = vilkår.tilstand,
                            utfall = vilkår.utfall.name,
                            løsning_medlemskap_yrkesskade_maskinell = vilkår.løsning_medlemskap_yrkesskade_maskinell?.map {
                                SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade(
                                    løsningId = it.løsningId,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_medlemskap_yrkesskade_manuell = vilkår.løsning_medlemskap_yrkesskade_manuell?.map {
                                SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_2_maskinell = vilkår.løsning_11_2_maskinell?.map {
                                SøkereKafkaDto.LøsningMaskinellParagraf_11_2(
                                    løsningId = it.løsningId,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_2_manuell = vilkår.løsning_11_2_manuell?.map {
                                SøkereKafkaDto.LøsningManuellParagraf_11_2(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_3_manuell = vilkår.løsning_11_3_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_3(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            løsning_11_4_ledd2_ledd3_manuell = vilkår.løsning_11_4_ledd2_ledd3_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_4_ledd2_ledd3(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            løsning_11_5_manuell = vilkår.løsning_11_5_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_5(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                                    nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                                )
                            },
                            løsning_11_5_yrkesskade_manuell = vilkår.løsning_11_5_yrkesskade_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent,
                                    arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                                )
                            },
                            løsning_11_6_manuell = vilkår.løsning_11_6_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_6(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    harBehovForBehandling = it.harBehovForBehandling,
                                    harBehovForTiltak = it.harBehovForTiltak,
                                    harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid
                                )
                            },
                            løsning_11_12_ledd1_manuell = vilkår.løsning_11_12_ledd1_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_12_ledd1(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    bestemmesAv = it.bestemmesAv,
                                    unntak = it.unntak,
                                    unntaksbegrunnelse = it.unntaksbegrunnelse,
                                    manueltSattVirkningsdato = it.manueltSattVirkningsdato
                                )
                            },
                            løsning_11_19_manuell = vilkår.løsning_11_19_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_19(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    beregningsdato = it.beregningsdato
                                )
                            },
                            løsning_11_22_manuell = vilkår.løsning_11_22_manuell?.map {
                                SøkereKafkaDto.LøsningParagraf_11_22(
                                    løsningId = it.løsningId,
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
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            kvalitetssikringer_medlemskap_yrkesskade = vilkår.kvalitetssikringer_medlemskap_yrkesskade?.map {
                                SøkereKafkaDto.KvalitetssikringMedlemskapYrkesskade(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_2 = vilkår.kvalitetssikringer_11_2?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_2(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_3 = vilkår.kvalitetssikringer_11_3?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_3(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_4_ledd2_ledd3 = vilkår.kvalitetssikringer_11_4_ledd2_ledd3?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_5 = vilkår.kvalitetssikringer_11_5?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_5(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_5_yrkesskade = vilkår.kvalitetssikringer_11_5_yrkesskade?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_5Yrkesskade(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_6 = vilkår.kvalitetssikringer_11_6?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_6(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_12_ledd1 = vilkår.kvalitetssikringer_11_12_ledd1?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_12FørsteLedd(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_19 = vilkår.kvalitetssikringer_11_19?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_19(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_22 = vilkår.kvalitetssikringer_11_22?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_22(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_29 = vilkår.kvalitetssikringer_11_29?.map {
                                SøkereKafkaDto.KvalitetssikringParagraf_11_29(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                        )
                    }
                )
            },
            vurderingsdato = sak.vurderingsdato,
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
