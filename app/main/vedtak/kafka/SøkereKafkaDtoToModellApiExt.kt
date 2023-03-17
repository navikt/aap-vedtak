package vedtak.kafka

import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto.*
import no.nav.aap.modellapi.*

internal fun SøkereKafkaDto.toModellApi() = SøkerModellApi(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(SakKafkaDto::toModellApi),
)

private fun SakKafkaDto.toModellApi() = SakModellApi(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(SakstypeKafkaDto::toModellApi),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toModellApi()
)

private fun SakstypeKafkaDto.toModellApi() = SakstypeModellApi(
    type = type,
    aktiv = aktiv,
    vilkårsvurderinger = listOfNotNull(
        medlemskapYrkesskade?.toModellApi(),
        paragraf_8_48?.toModellApi(),
        paragraf_11_2?.toModellApi(),
        paragraf_11_3?.toModellApi(),
        paragraf_11_4FørsteLedd?.toModellApi(),
        paragraf_11_4AndreOgTredjeLedd?.toModellApi(),
        paragraf_11_5?.toModellApi(),
        paragraf_11_5Yrkesskade?.toModellApi(),
        paragraf_11_6?.toModellApi(),
        paragraf_11_14?.toModellApi(),
        paragraf_11_19?.toModellApi(),
        paragraf_11_22?.toModellApi(),
        paragraf_11_27FørsteLedd?.toModellApi(),
        paragraf_11_29?.toModellApi(),
        paragraf_22_13?.toModellApi()
    )
)

private fun Paragraf_8_48KafkaDto.toModellApi() = Paragraf_8_48ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(LøsningMaskinellParagraf_8_48KafkaDto::toModellApi),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_22_13KafkaDto::toModellApi),
)

private fun MedlemskapYrkesskadeKafkaDto.toModellApi() = MedlemskapYrkesskadeModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell
        .map(LøsningMaskinellMedlemskapYrkesskadeKafkaDto::toModellApi),
    totrinnskontroller = totrinnskontroller.map(TotrinnskontrollMedlemskapYrkesskadeKafkaDto::toModellApi),
)

private fun Paragraf_11_2KafkaDto.toModellApi() = Paragraf_11_2ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(LøsningMaskinellParagraf_11_2KafkaDto::toModellApi),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_2KafkaDto::toModellApi),
)

private fun Paragraf_11_3KafkaDto.toModellApi() = Paragraf_11_3ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_3KafkaDto::toModellApi),
)

private fun Paragraf_11_4FørsteLeddKafkaDto.toModellApi() = Paragraf_11_4FørsteLeddModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_4AndreOgTredjeLeddKafkaDto.toModellApi() = Paragraf_11_4AndreOgTredjeLeddModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto::toModellApi),
)

private fun Paragraf_11_5KafkaDto.toModellApi() = Paragraf_11_5ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_5KafkaDto::toModellApi)
)

private fun Paragraf_11_5YrkesskadeKafkaDto.toModellApi() = Paragraf_11_5YrkesskadeModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_5YrkesskadeKafkaDto::toModellApi),
)

private fun Paragraf_11_6KafkaDto.toModellApi() = Paragraf_11_6ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(InnstillingParagraf_11_6KafkaDto::toModellApi),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_6KafkaDto::toModellApi),
)

private fun Paragraf_11_14KafkaDto.toModellApi() = Paragraf_11_14ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_19KafkaDto.toModellApi() = Paragraf_11_19ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_19KafkaDto::toModellApi),
)

private fun Paragraf_11_22KafkaDto.toModellApi() = Paragraf_11_22ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_22KafkaDto::toModellApi),
)

private fun Paragraf_11_27FørsteLeddKafkaDto.toModellApi() = Paragraf_11_27FørsteLeddModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_27_maskinell = løsning_11_27_maskinell.map(LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto::toModellApi),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_22_13KafkaDto::toModellApi),
)

private fun Paragraf_11_29KafkaDto.toModellApi() = Paragraf_11_29ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_29KafkaDto::toModellApi),
)

private fun Paragraf_22_13KafkaDto.toModellApi() = Paragraf_22_13ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_22_13KafkaDto::toModellApi),
    søknadsdata = søknadsdata.map(SøknadsdataParagraf_22_13KafkaDto::toModellApi),
)

private fun SøknadsdataParagraf_22_13KafkaDto.toModellApi() = Paragraf_22_13ModellApi.SøknadsdataModellApi(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt,
)

private fun InnstillingParagraf_11_6KafkaDto.toModellApi() = InnstillingParagraf_11_6ModellApi(
    innstillingId = innstillingId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = when (individuellBegrunnelse) {
        "<Mangler støtte for null backend>" -> null
        else -> individuellBegrunnelse
    },
)

private fun LøsningMaskinellMedlemskapYrkesskadeKafkaDto.toModellApi() = LøsningMaskinellMedlemskapYrkesskadeModellApi(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskadeKafkaDto.toModellApi() = LøsningManuellMedlemskapYrkesskadeModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningMaskinellParagraf_8_48KafkaDto.toModellApi() = SykepengedagerModellApi(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    sykepengedager = sykepengedager?.let { sykepengedager ->
        SykepengedagerModellApi.Sykepengedager(
            gjenståendeSykedager = sykepengedager.gjenståendeSykedager,
            foreløpigBeregnetSluttPåSykepenger = sykepengedager.foreløpigBeregnetSluttPåSykepenger,
            kilde = sykepengedager.kilde,
        )
    }
)

private fun LøsningMaskinellParagraf_11_2KafkaDto.toModellApi() = LøsningMaskinellParagraf_11_2ModellApi(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2KafkaDto.toModellApi() = LøsningParagraf_11_2ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3KafkaDto.toModellApi() = LøsningParagraf_11_3ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto.toModellApi() =
    LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )

private fun LøsningParagraf_11_5KafkaDto.toModellApi() = LøsningParagraf_11_5ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = kravOmNedsattArbeidsevneErOppfyltBegrunnelse,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeBegrunnelse,
    kilder = kilder,
    legeerklæringDato = legeerklæringDato,
    sykmeldingDato = sykmeldingDato,
)

private fun LøsningParagraf_11_5_yrkesskadeKafkaDto.toModellApi() = LøsningParagraf_11_5YrkesskadeModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6KafkaDto.toModellApi() = LøsningParagraf_11_6ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = when (individuellBegrunnelse) {
        "<Mangler støtte for null backend>" -> null
        else -> individuellBegrunnelse
    },
)

private fun LøsningParagraf_11_19KafkaDto.toModellApi() = LøsningParagraf_11_19ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22KafkaDto.toModellApi() = LøsningParagraf_11_22ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto.toModellApi() = LøsningParagraf_11_27_FørsteLedd_ModellApi(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    svangerskapspenger = SvangerskapspengerModellApi(
        fom = svangerskapspenger.fom,
        tom = svangerskapspenger.tom,
        grad = svangerskapspenger.grad,
        vedtaksdato = svangerskapspenger.vedtaksdato,
    )
)

private fun LøsningParagraf_11_29KafkaDto.toModellApi() = LøsningParagraf_11_29ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_22_13KafkaDto.toModellApi() = LøsningParagraf_22_13ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato,
    begrunnelseForAnnet = begrunnelseForAnnet,
)

private fun KvalitetssikringMedlemskapYrkesskadeKafkaDto.toModellApi() = KvalitetssikringMedlemskapYrkesskadeModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_2KafkaDto.toModellApi() = KvalitetssikringParagraf_11_2ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_3KafkaDto.toModellApi() = KvalitetssikringParagraf_11_3ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto.toModellApi() =
    KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

private fun KvalitetssikringParagraf_11_5KafkaDto.toModellApi() = KvalitetssikringParagraf_11_5ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    kravOmNedsattArbeidsevneErGodkjent = kravOmNedsattArbeidsevneErGodkjent,
    kravOmNedsattArbeidsevneErGodkjentBegrunnelse = kravOmNedsattArbeidsevneErGodkjentBegrunnelse,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjent = nedsettelseSkyldesSykdomEllerSkadeErGodkjent,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse,
)

private fun KvalitetssikringParagraf_11_5YrkesskadeKafkaDto.toModellApi() =
    KvalitetssikringParagraf_11_5YrkesskadeModellApi(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

private fun KvalitetssikringParagraf_11_6KafkaDto.toModellApi() = KvalitetssikringParagraf_11_6ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_22_13KafkaDto.toModellApi() = KvalitetssikringParagraf_22_13ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_19KafkaDto.toModellApi() = KvalitetssikringParagraf_11_19ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_22KafkaDto.toModellApi() = KvalitetssikringParagraf_11_22ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_29KafkaDto.toModellApi() = KvalitetssikringParagraf_11_29ModellApi(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)


private fun VedtakKafkaDto.toModellApi() = VedtakModellApi(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toModellApi(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    etSettAvVurderteVilkårSomHarFørtTilDetteVedtaket = emptyList() // FIXME
)

private fun InntektsgrunnlagKafkaDto.toModellApi() = InntektsgrunnlagModellApi(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(InntekterForBeregningKafkaDto::toModellApi),
    yrkesskade = yrkesskade?.toModellApi(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregningKafkaDto.toModellApi() = InntekterForBeregningModellApi(
    inntekter = inntekter.map(InntektKafkaDto::toModellApi),
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toModellApi(),
)

private fun InntektKafkaDto.toModellApi() = InntektModellApi(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅrKafkaDto.toModellApi() = InntektsgrunnlagForÅrModellApi(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun YrkesskadeKafkaDto.toModellApi() = YrkesskadeModellApi(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toModellApi(),
)

private fun TotrinnskontrollMedlemskapYrkesskadeKafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_2KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_3KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_5KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_5YrkesskadeKafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_6KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_19KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_22KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_11_29KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)

private fun Totrinnskontroll_22_13KafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)
