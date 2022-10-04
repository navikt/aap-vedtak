package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto
import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto.*
import no.nav.aap.dto.kafka.SøkereKafkaDto
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

private enum class Paragraf {
    MEDLEMSKAP_YRKESSKADE,
    PARAGRAF_8_48,
    PARAGRAF_11_2,
    PARAGRAF_11_3,
    PARAGRAF_11_4,
    PARAGRAF_11_5,
    PARAGRAF_11_5_YRKESSKADE,
    PARAGRAF_11_6,
    PARAGRAF_11_14,
    PARAGRAF_11_19,
    PARAGRAF_11_22,
    PARAGRAF_11_27,
    PARAGRAF_11_29,
    PARAGRAF_22_13,
}

private enum class Ledd {
    LEDD_1,
    LEDD_2,
    LEDD_3;
}

private fun Vilkårsvurdering.toDto() = when (enumValueOf<Paragraf>(paragraf)) {
    Paragraf.MEDLEMSKAP_YRKESSKADE -> SøkereKafkaDto.MedlemskapYrkesskade(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell?.map { it.toDto() }
            ?: emptyList(),
        løsning_medlemskap_yrkesskade_manuell = løsning_medlemskap_yrkesskade_manuell?.map { it.toDto() }
            ?: emptyList(),
        kvalitetssikringer_medlemskap_yrkesskade = kvalitetssikringer_medlemskap_yrkesskade?.map { it.toDto() }
            ?: emptyList(),
    )

    Paragraf.PARAGRAF_8_48 -> TODO() //Paragraf_8_48(
//                                vilkårsvurderingsid = vilkår.vilkårsvurderingsid,
//                                vurdertAv = vilkår.vurdertAv,
//                                godkjentAv = vilkår.kvalitetssikretAv,
//                                paragraf = vilkår.paragraf,
//                                ledd = vilkår.ledd,
//                                tilstand = vilkår.tilstand,
//                                utfall = vilkår.utfall.name,
//                                vurdertMaskinelt = vilkår.vurdertMaskinelt,
//                                løsning_medlemskap_yrkesskade_maskinell = vilkår.løsning_medlemskap_yrkesskade_maskinell.map {
//                                    LøsningMaskinellMedlemskapYrkesskade(
//                                        løsningId = it.løsningId,
//                                        erMedlem = it.erMedlem
//                                    )
//                                },
//                                løsning_medlemskap_yrkesskade_manuell = vilkår.løsning_medlemskap_yrkesskade_manuell.map {
//                                    LøsningManuellMedlemskapYrkesskade(
//                                        løsningId = it.løsningId,
//                                        vurdertAv = it.vurdertAv,
//                                        tidspunktForVurdering = it.tidspunktForVurdering,
//                                        erMedlem = it.erMedlem
//                                    )
//                                },
//                            )

    Paragraf.PARAGRAF_11_2 -> SøkereKafkaDto.Paragraf_11_2(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_2_maskinell = løsning_11_2_maskinell?.map { it.toDto() } ?: emptyList(),
        løsning_11_2_manuell = løsning_11_2_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_2 = kvalitetssikringer_11_2?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_3 -> SøkereKafkaDto.Paragraf_11_3(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_3_manuell = løsning_11_3_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_3 = kvalitetssikringer_11_3?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_4 -> when (ledd.map<String, Ledd>(::enumValueOf)) {
        listOf(Ledd.LEDD_1) -> SøkereKafkaDto.Paragraf_11_4FørsteLedd(
            vilkårsvurderingsid = vilkårsvurderingsid,
            vurdertAv = vurdertAv,
            kvalitetssikretAv = godkjentAv,
            paragraf = paragraf,
            ledd = ledd,
            tilstand = tilstand,
            utfall = utfall,
            vurdertMaskinelt = vurdertMaskinelt,
        )

        else ->
            SøkereKafkaDto.Paragraf_11_4AndreOgTredjeLedd(
                vilkårsvurderingsid = vilkårsvurderingsid,
                vurdertAv = vurdertAv,
                kvalitetssikretAv = godkjentAv,
                paragraf = paragraf,
                ledd = ledd,
                tilstand = tilstand,
                utfall = utfall,
                vurdertMaskinelt = vurdertMaskinelt,
                løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell?.map { it.toDto() } ?: emptyList(),
                kvalitetssikringer_11_4_ledd2_ledd3 = kvalitetssikringer_11_4_ledd2_ledd3?.map { it.toDto() }
                    ?: emptyList(),
            )
    }

    Paragraf.PARAGRAF_11_5 -> SøkereKafkaDto.Paragraf_11_5(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_5_manuell = løsning_11_5_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_5 = kvalitetssikringer_11_5?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_5_YRKESSKADE -> SøkereKafkaDto.Paragraf_11_5Yrkesskade(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_5_yrkesskade = kvalitetssikringer_11_5_yrkesskade?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_6 -> SøkereKafkaDto.Paragraf_11_6(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_6_manuell = løsning_11_6_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_6 = kvalitetssikringer_11_6?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_14 -> SøkereKafkaDto.Paragraf_11_14(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
    )

    Paragraf.PARAGRAF_11_19 -> SøkereKafkaDto.Paragraf_11_19(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_19_manuell = løsning_11_19_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_19 = kvalitetssikringer_11_19?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_22 -> SøkereKafkaDto.Paragraf_11_22(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_22_manuell = løsning_11_22_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_22 = kvalitetssikringer_11_22?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_27 -> SøkereKafkaDto.Paragraf_11_27FørsteLedd(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_27_maskinell = emptyList(),
        løsning_22_13_manuell = løsning_22_13_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_22_13 = kvalitetssikringer_22_13?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_11_29 -> SøkereKafkaDto.Paragraf_11_29(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_29_manuell = løsning_11_29_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_11_29 = kvalitetssikringer_11_29?.map { it.toDto() } ?: emptyList(),
    )

    Paragraf.PARAGRAF_22_13 -> SøkereKafkaDto.Paragraf_22_13(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = godkjentAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_22_13_manuell = løsning_22_13_manuell?.map { it.toDto() } ?: emptyList(),
        kvalitetssikringer_22_13 = kvalitetssikringer_22_13?.map { it.toDto() } ?: emptyList(),
    )
}

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

private fun KvalitetssikringMedlemskapYrkesskade.toDto() = SøkereKafkaDto.KvalitetssikringMedlemskapYrkesskade(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
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

private fun KvalitetssikringParagraf_11_2.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_2(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_3.toDto() = SøkereKafkaDto.LøsningParagraf_11_3(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringParagraf_11_3.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_3(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_4_ledd2_ledd3.toDto() = SøkereKafkaDto.LøsningParagraf_11_4AndreOgTredjeLedd(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLedd.toDto() =
    SøkereKafkaDto.KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse,
    )

private fun LøsningParagraf_11_5.toDto() = SøkereKafkaDto.LøsningParagraf_11_5(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
)

private fun KvalitetssikringParagraf_11_5.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_5(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_5_yrkesskade.toDto() = SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun KvalitetssikringParagraf_11_5Yrkesskade.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_5Yrkesskade(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_6.toDto() = SøkereKafkaDto.LøsningParagraf_11_6(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun KvalitetssikringParagraf_11_6.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_6(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_19.toDto() = SøkereKafkaDto.LøsningParagraf_11_19(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun KvalitetssikringParagraf_11_19.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_19(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
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

private fun KvalitetssikringParagraf_11_22.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_22(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_29.toDto() = SøkereKafkaDto.LøsningParagraf_11_29(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringParagraf_11_29.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_29(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_22_13.toDto() = SøkereKafkaDto.LøsningParagraf_22_13(
    løsningId = UUID.randomUUID(),
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun KvalitetssikringParagraf_22_13.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_22_13(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
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
