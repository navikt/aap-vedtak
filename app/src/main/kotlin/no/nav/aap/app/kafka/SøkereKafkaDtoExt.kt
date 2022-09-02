package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto.*
import no.nav.aap.modellapi.*

internal fun SøkereKafkaDto.toModellApi() = SøkerModellApi(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(Sak::toModellApi),
)

private fun Sak.toModellApi() = SakModellApi(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(Sakstype::toModellApi),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toModellApi()
)

private fun Sakstype.toModellApi() = SakstypeModellApi(
    type = type,
    aktiv = aktiv,
    vilkårsvurderinger = vilkårsvurderinger.map(Vilkårsvurdering::toModellApi)
)

private fun Vilkårsvurdering.toModellApi() = VilkårsvurderingModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = godkjentAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell?.map { it.toModellApi() },
    løsning_medlemskap_yrkesskade_manuell = løsning_medlemskap_yrkesskade_manuell?.map { it.toModellApi() },
    løsning_11_2_maskinell = løsning_11_2_maskinell?.map { it.toModellApi() },
    løsning_11_2_manuell = løsning_11_2_manuell?.map { it.toModellApi() },
    løsning_11_3_manuell = løsning_11_3_manuell?.map { it.toModellApi() },
    løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell?.map { it.toModellApi() },
    løsning_11_5_manuell = løsning_11_5_manuell?.map { it.toModellApi() },
    løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell?.map { it.toModellApi() },
    løsning_11_6_manuell = løsning_11_6_manuell?.map { it.toModellApi() },
    løsning_11_12_ledd1_manuell = løsning_11_12_ledd1_manuell?.map { it.toModellApi() },
    løsning_11_19_manuell = løsning_11_19_manuell?.map { it.toModellApi() },
    løsning_11_22_manuell = løsning_11_22_manuell?.map { it.toModellApi() },
    løsning_11_29_manuell = løsning_11_29_manuell?.map { it.toModellApi() },
    kvalitetssikringer_medlemskap_yrkesskade = kvalitetssikringer_medlemskap_yrkesskade?.map { it.toModellApi() },
    kvalitetssikringer_11_2 = kvalitetssikringer_11_2?.map { it.toModellApi() },
    kvalitetssikringer_11_3 = kvalitetssikringer_11_3?.map { it.toModellApi() },
    kvalitetssikringer_11_4_ledd2_ledd3 = kvalitetssikringer_11_4_ledd2_ledd3?.map { it.toModellApi() },
    kvalitetssikringer_11_5 = kvalitetssikringer_11_5?.map { it.toModellApi() },
    kvalitetssikringer_11_5_yrkesskade = kvalitetssikringer_11_5_yrkesskade?.map { it.toModellApi() },
    kvalitetssikringer_11_6 = kvalitetssikringer_11_6?.map { it.toModellApi() },
    kvalitetssikringer_11_12_ledd1 = kvalitetssikringer_11_12_ledd1?.map { it.toModellApi() },
    kvalitetssikringer_11_19 = kvalitetssikringer_11_19?.map { it.toModellApi() },
    kvalitetssikringer_11_22 = kvalitetssikringer_11_22?.map { it.toModellApi() },
    kvalitetssikringer_11_29 = kvalitetssikringer_11_29?.map { it.toModellApi() },
)

private fun LøsningMaskinellMedlemskapYrkesskade.toModellApi() = LøsningMaskinellMedlemskapYrkesskadeModellApi(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskade.toModellApi() = LøsningManuellMedlemskapYrkesskadeModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningMaskinellParagraf_11_2.toModellApi() = LøsningMaskinellParagraf_11_2ModellApi(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2.toModellApi() = LøsningParagraf_11_2ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3.toModellApi() = LøsningParagraf_11_3ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4_ledd2_ledd3.toModellApi() = LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_5.toModellApi() = LøsningParagraf_11_5ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
)

private fun LøsningParagraf_11_5_yrkesskade.toModellApi() = LøsningParagraf_11_5YrkesskadeModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6.toModellApi() = LøsningParagraf_11_6ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun LøsningParagraf_11_12_ledd1.toModellApi() = LøsningParagraf_11_12FørsteLeddModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun LøsningParagraf_11_19.toModellApi() = LøsningParagraf_11_19ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22.toModellApi() = LøsningParagraf_11_22ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningParagraf_11_29.toModellApi() = LøsningParagraf_11_29ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringMedlemskapYrkesskade.toModellApi() = KvalitetssikringMedlemskapYrkesskadeModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_2.toModellApi() = KvalitetssikringParagraf_11_2ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_3.toModellApi() = KvalitetssikringParagraf_11_3ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLedd.toModellApi() = KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_5.toModellApi() = KvalitetssikringParagraf_11_5ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_5Yrkesskade.toModellApi() = KvalitetssikringParagraf_11_5YrkesskadeModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_6.toModellApi() = KvalitetssikringParagraf_11_6ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_12FørsteLedd.toModellApi() = KvalitetssikringParagraf_11_12FørsteLeddModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_19.toModellApi() = KvalitetssikringParagraf_11_19ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_22.toModellApi() = KvalitetssikringParagraf_11_22ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_29.toModellApi() = KvalitetssikringParagraf_11_29ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)


private fun Vedtak.toModellApi() = VedtakModellApi(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toModellApi(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun Inntektsgrunnlag.toModellApi() = InntektsgrunnlagModellApi(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map { it.toModellApi() },
    yrkesskade = yrkesskade?.toModellApi(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregning.toModellApi() = InntekterForBeregningModellApi(
    inntekter = inntekter.map { it.toModellApi() },
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toModellApi(),
)

private fun Inntekt.toModellApi() = InntektModellApi(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅr.toModellApi() = InntektsgrunnlagForÅrModellApi(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun Yrkesskade.toModellApi() = YrkesskadeModellApi(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toModellApi(),
)

internal fun SøkerModellApi.toJson() = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map { sak ->
        Sak(
            saksid = sak.saksid,
            tilstand = sak.tilstand,
            sakstyper = sak.sakstyper.map { sakstype ->
                Sakstype(
                    type = sakstype.type,
                    aktiv = sakstype.aktiv,
                    vilkårsvurderinger = sakstype.vilkårsvurderinger.map { vilkår ->
                        Vilkårsvurdering(
                            vilkårsvurderingsid = vilkår.vilkårsvurderingsid,
                            vurdertAv = vilkår.vurdertAv,
                            godkjentAv = vilkår.kvalitetssikretAv,
                            paragraf = vilkår.paragraf,
                            ledd = vilkår.ledd,
                            tilstand = vilkår.tilstand,
                            utfall = vilkår.utfall.name,
                            vurdertMaskinelt = vilkår.vurdertMaskinelt,
                            løsning_medlemskap_yrkesskade_maskinell = vilkår.løsning_medlemskap_yrkesskade_maskinell?.map {
                                LøsningMaskinellMedlemskapYrkesskade(
                                    løsningId = it.løsningId,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_medlemskap_yrkesskade_manuell = vilkår.løsning_medlemskap_yrkesskade_manuell?.map {
                                LøsningManuellMedlemskapYrkesskade(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_2_maskinell = vilkår.løsning_11_2_maskinell?.map {
                                LøsningMaskinellParagraf_11_2(
                                    løsningId = it.løsningId,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_2_manuell = vilkår.løsning_11_2_manuell?.map {
                                LøsningManuellParagraf_11_2(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erMedlem = it.erMedlem
                                )
                            },
                            løsning_11_3_manuell = vilkår.løsning_11_3_manuell?.map {
                                LøsningParagraf_11_3(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            løsning_11_4_ledd2_ledd3_manuell = vilkår.løsning_11_4_ledd2_ledd3_manuell?.map {
                                LøsningParagraf_11_4_ledd2_ledd3(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            løsning_11_5_manuell = vilkår.løsning_11_5_manuell?.map {
                                LøsningParagraf_11_5(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    kravOmNedsattArbeidsevneErOppfylt = it.kravOmNedsattArbeidsevneErOppfylt,
                                    nedsettelseSkyldesSykdomEllerSkade = it.nedsettelseSkyldesSykdomEllerSkade,
                                )
                            },
                            løsning_11_5_yrkesskade_manuell = vilkår.løsning_11_5_yrkesskade_manuell?.map {
                                LøsningParagraf_11_5_yrkesskade(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    arbeidsevneErNedsattMedMinst30Prosent = it.arbeidsevneErNedsattMedMinst30Prosent,
                                    arbeidsevneErNedsattMedMinst50Prosent = it.arbeidsevneErNedsattMedMinst50Prosent,
                                )
                            },
                            løsning_11_6_manuell = vilkår.løsning_11_6_manuell?.map {
                                LøsningParagraf_11_6(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    harBehovForBehandling = it.harBehovForBehandling,
                                    harBehovForTiltak = it.harBehovForTiltak,
                                    harMulighetForÅKommeIArbeid = it.harMulighetForÅKommeIArbeid
                                )
                            },
                            løsning_11_12_ledd1_manuell = vilkår.løsning_11_12_ledd1_manuell?.map {
                                LøsningParagraf_11_12_ledd1(
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
                                LøsningParagraf_11_19(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    beregningsdato = it.beregningsdato
                                )
                            },
                            løsning_11_22_manuell = vilkår.løsning_11_22_manuell?.map {
                                LøsningParagraf_11_22(
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
                                LøsningParagraf_11_29(
                                    løsningId = it.løsningId,
                                    vurdertAv = it.vurdertAv,
                                    tidspunktForVurdering = it.tidspunktForVurdering,
                                    erOppfylt = it.erOppfylt
                                )
                            },
                            kvalitetssikringer_medlemskap_yrkesskade = vilkår.kvalitetssikringer_medlemskap_yrkesskade?.map {
                                KvalitetssikringMedlemskapYrkesskade(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_2 = vilkår.kvalitetssikringer_11_2?.map {
                                KvalitetssikringParagraf_11_2(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_3 = vilkår.kvalitetssikringer_11_3?.map {
                                KvalitetssikringParagraf_11_3(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_4_ledd2_ledd3 = vilkår.kvalitetssikringer_11_4_ledd2_ledd3?.map {
                                KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_5 = vilkår.kvalitetssikringer_11_5?.map {
                                KvalitetssikringParagraf_11_5(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_5_yrkesskade = vilkår.kvalitetssikringer_11_5_yrkesskade?.map {
                                KvalitetssikringParagraf_11_5Yrkesskade(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_6 = vilkår.kvalitetssikringer_11_6?.map {
                                KvalitetssikringParagraf_11_6(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_12_ledd1 = vilkår.kvalitetssikringer_11_12_ledd1?.map {
                                KvalitetssikringParagraf_11_12FørsteLedd(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_19 = vilkår.kvalitetssikringer_11_19?.map {
                                KvalitetssikringParagraf_11_19(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_22 = vilkår.kvalitetssikringer_11_22?.map {
                                KvalitetssikringParagraf_11_22(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
                                    kvalitetssikretAv = it.kvalitetssikretAv,
                                    tidspunktForKvalitetssikring = it.tidspunktForKvalitetssikring,
                                    erGodkjent = it.erGodkjent,
                                    begrunnelse = it.begrunnelse
                                )
                            },
                            kvalitetssikringer_11_29 = vilkår.kvalitetssikringer_11_29?.map {
                                KvalitetssikringParagraf_11_29(
                                    kvalitetssikringId = it.kvalitetssikringId,
                                    løsningId = it.løsningId,
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
                Vedtak(
                    vedtaksid = vedtak.vedtaksid,
                    innvilget = vedtak.innvilget,
                    inntektsgrunnlag = vedtak.inntektsgrunnlag.let { inntektsgrunnlag ->
                        Inntektsgrunnlag(
                            beregningsdato = inntektsgrunnlag.beregningsdato,
                            inntekterSiste3Kalenderår = inntektsgrunnlag.inntekterSiste3Kalenderår.map { siste3år ->
                                InntekterForBeregning(
                                    inntekter = siste3år.inntekter.map { inntekt ->
                                        Inntekt(
                                            arbeidsgiver = inntekt.arbeidsgiver,
                                            inntekstmåned = inntekt.inntekstmåned,
                                            beløp = inntekt.beløp,
                                        )
                                    },
                                    inntektsgrunnlagForÅr = siste3år.inntektsgrunnlagForÅr.let { år ->
                                        InntektsgrunnlagForÅr(
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
                                Yrkesskade(
                                    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = yrkesskade.gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
                                    inntektsgrunnlag = yrkesskade.inntektsgrunnlag.let { år ->
                                        InntektsgrunnlagForÅr(
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