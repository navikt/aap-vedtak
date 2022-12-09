package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto as Til
import no.nav.aap.dto.kafka.SøkereKafkaDto as Fra

internal fun Fra.toForrigeDto() = Til(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(Fra.SakKafkaDto::toDto),
)

private fun Fra.SakKafkaDto.toDto() = Til.SakKafkaDto(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(Fra.SakstypeKafkaDto::toDto),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toDto()
)

private fun Fra.SakstypeKafkaDto.toDto() = Til.SakstypeKafkaDto(
    type = type,
    aktiv = aktiv,
    medlemskapYrkesskade = medlemskapYrkesskade?.toMedlemskapYrkesskade(),
    paragraf_8_48 = paragraf_8_48?.toParagraf_8_48(),
    paragraf_11_2 = paragraf_11_2?.toParagraf_11_2(),
    paragraf_11_3 = paragraf_11_3?.toParagraf_11_3(),
    paragraf_11_4FørsteLedd = paragraf_11_4FørsteLedd?.toParagraf_11_4FørsteLedd(),
    paragraf_11_4AndreOgTredjeLedd = paragraf_11_4AndreOgTredjeLedd?.toParagraf_11_4AndreOgTredjeLedd(),
    paragraf_11_5 = paragraf_11_5?.toParagraf_11_5(),
    paragraf_11_5Yrkesskade = paragraf_11_5Yrkesskade?.toParagraf_11_5Yrkesskade(),
    paragraf_11_6 = paragraf_11_6?.toParagraf_11_6(),
    paragraf_11_14 = paragraf_11_14?.toParagraf_11_14(),
    paragraf_11_19 = paragraf_11_19?.toParagraf_11_19(),
    paragraf_11_22 = paragraf_11_22?.toParagraf_11_22(),
    paragraf_11_27FørsteLedd = paragraf_11_27FørsteLedd?.toParagraf_11_27FørsteLedd(),
    paragraf_11_29 = paragraf_11_29?.toParagraf_11_29(),
    paragraf_22_13 = paragraf_22_13?.toParagraf_22_13(),
)

private fun Fra.MedlemskapYrkesskadeKafkaDto.toMedlemskapYrkesskade() = Til.MedlemskapYrkesskadeKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell =
    løsning_medlemskap_yrkesskade_maskinell.map(Fra.LøsningMaskinellMedlemskapYrkesskadeKafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Fra.TotrinnskontrollMedlemskapYrkesskadeKafkaDto::toDto),
)

private fun Fra.TotrinnskontrollMedlemskapYrkesskadeKafkaDto.toDto() =
    Til.TotrinnskontrollMedlemskapYrkesskadeKafkaDto(
        løsning = løsning.toDto(),
        kvalitetssikring = kvalitetssikring?.toDto(),
    )

private fun Fra.Paragraf_8_48KafkaDto.toParagraf_8_48() = Til.Paragraf_8_48KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(Fra.LøsningMaskinellParagraf_8_48KafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_22_13KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_22_13KafkaDto.toDto() = Til.Totrinnskontroll_22_13KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_2KafkaDto.toParagraf_11_2() = Til.Paragraf_11_2KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(Fra.LøsningMaskinellParagraf_11_2KafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_2KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_11_2KafkaDto.toDto() = Til.Totrinnskontroll_11_2KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_3KafkaDto.toParagraf_11_3() = Til.Paragraf_11_3KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_3KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_11_3KafkaDto.toDto() = Til.Totrinnskontroll_11_3KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_4FørsteLeddKafkaDto.toParagraf_11_4FørsteLedd() =
    Til.Paragraf_11_4FørsteLeddKafkaDto(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
    )

private fun Fra.Paragraf_11_4AndreOgTredjeLeddKafkaDto.toParagraf_11_4AndreOgTredjeLedd() =
    Til.Paragraf_11_4AndreOgTredjeLeddKafkaDto(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto::toDto)
    )

private fun Fra.Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto.toDto() =
    Til.Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto(
        løsning = løsning.toDto(),
        kvalitetssikring = kvalitetssikring?.toDto(),
    )

private fun Fra.Paragraf_11_5KafkaDto.toParagraf_11_5() = Til.Paragraf_11_5KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_5KafkaDto::toDto)
)

private fun Fra.Totrinnskontroll_11_5KafkaDto.toDto() = Til.Totrinnskontroll_11_5KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_5YrkesskadeKafkaDto.toParagraf_11_5Yrkesskade() =
    Til.Paragraf_11_5YrkesskadeKafkaDto(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_5YrkesskadeKafkaDto::toDto),
    )

private fun Fra.Totrinnskontroll_11_5YrkesskadeKafkaDto.toDto() = Til.Totrinnskontroll_11_5YrkesskadeKafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_6KafkaDto.toParagraf_11_6() = Til.Paragraf_11_6KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(Fra.InnstillingParagraf_11_6KafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_6KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_11_6KafkaDto.toDto() = Til.Totrinnskontroll_11_6KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_14KafkaDto.toParagraf_11_14() = Til.Paragraf_11_14KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Fra.Paragraf_11_19KafkaDto.toParagraf_11_19() = Til.Paragraf_11_19KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_19KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_11_19KafkaDto.toDto() = Til.Totrinnskontroll_11_19KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_22KafkaDto.toParagraf_11_22() = Til.Paragraf_11_22KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_22KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_11_22KafkaDto.toDto() = Til.Totrinnskontroll_11_22KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_11_27FørsteLeddKafkaDto.toParagraf_11_27FørsteLedd() =
    Til.Paragraf_11_27FørsteLeddKafkaDto(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_27_maskinell = løsning_11_27_maskinell.map(Fra.LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto::toDto),
        totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_22_13KafkaDto::toDto),
    )

private fun Fra.Paragraf_11_29KafkaDto.toParagraf_11_29() = Til.Paragraf_11_29KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_11_29KafkaDto::toDto),
)

private fun Fra.Totrinnskontroll_11_29KafkaDto.toDto() = Til.Totrinnskontroll_11_29KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Fra.Paragraf_22_13KafkaDto.toParagraf_22_13() =
    Til.Paragraf_22_13KafkaDto(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.map(Fra.Totrinnskontroll_22_13KafkaDto::toDto),
        søknadsdata = søknadsdata.map(Fra.SøknadsdataParagraf_22_13KafkaDto::toDto)
    )

private fun Fra.SøknadsdataParagraf_22_13KafkaDto.toDto() = Til.SøknadsdataParagraf_22_13KafkaDto(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt
)

private fun Fra.LøsningMaskinellMedlemskapYrkesskadeKafkaDto.toDto() =
    Til.LøsningMaskinellMedlemskapYrkesskadeKafkaDto(
        løsningId = løsningId,
        erMedlem = erMedlem
    )

private fun Fra.LøsningManuellMedlemskapYrkesskadeKafkaDto.toDto() =
    Til.LøsningManuellMedlemskapYrkesskadeKafkaDto(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erMedlem = erMedlem
    )

private fun Fra.KvalitetssikringMedlemskapYrkesskadeKafkaDto.toDto() =
    Til.KvalitetssikringMedlemskapYrkesskadeKafkaDto(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse,
    )

private fun Fra.LøsningMaskinellParagraf_8_48KafkaDto.toDto() = Til.LøsningMaskinellParagraf_8_48KafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    sykepengedager = sykepengedager?.let { sykepenger ->
        Til.LøsningMaskinellParagraf_8_48KafkaDto.Sykepengedager(
            gjenståendeSykedager = sykepenger.gjenståendeSykedager,
            foreløpigBeregnetSluttPåSykepenger = sykepenger.foreløpigBeregnetSluttPåSykepenger,
            kilde = sykepenger.kilde,
        )
    },
)

private fun Fra.LøsningMaskinellParagraf_11_2KafkaDto.toDto() = Til.LøsningMaskinellParagraf_11_2KafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun Fra.LøsningManuellParagraf_11_2KafkaDto.toDto() = Til.LøsningManuellParagraf_11_2KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun Fra.KvalitetssikringParagraf_11_2KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_2KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.LøsningParagraf_11_3KafkaDto.toDto() = Til.LøsningParagraf_11_3KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun Fra.KvalitetssikringParagraf_11_3KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_3KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto.toDto() =
    Til.LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto(
        løsningId = løsningId,
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt
    )

private fun Fra.KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto.toDto() =
    Til.KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse,
    )

private fun Fra.LøsningParagraf_11_5KafkaDto.toDto() = Til.LøsningParagraf_11_5KafkaDto(
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

private fun Fra.KvalitetssikringParagraf_11_5KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_5KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    kravOmNedsattArbeidsevneErGodkjent = kravOmNedsattArbeidsevneErGodkjent,
    kravOmNedsattArbeidsevneErGodkjentBegrunnelse = kravOmNedsattArbeidsevneErGodkjentBegrunnelse,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjent = nedsettelseSkyldesSykdomEllerSkadeErGodkjent,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse,
)

private fun Fra.LøsningParagraf_11_5_yrkesskadeKafkaDto.toDto() = Til.LøsningParagraf_11_5_yrkesskadeKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun Fra.KvalitetssikringParagraf_11_5YrkesskadeKafkaDto.toDto() =
    Til.KvalitetssikringParagraf_11_5YrkesskadeKafkaDto(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse,
    )

private fun Fra.InnstillingParagraf_11_6KafkaDto.toDto() = Til.InnstillingParagraf_11_6KafkaDto(
    innstillingId = innstillingId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun Fra.LøsningParagraf_11_6KafkaDto.toDto() = Til.LøsningParagraf_11_6KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun Fra.KvalitetssikringParagraf_11_6KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_6KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.LøsningParagraf_11_19KafkaDto.toDto() = Til.LøsningParagraf_11_19KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun Fra.KvalitetssikringParagraf_11_19KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_19KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.LøsningParagraf_11_22KafkaDto.toDto() = Til.LøsningParagraf_11_22KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun Fra.KvalitetssikringParagraf_11_22KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_22KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto.toDto() =
    Til.LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto(
        løsningId = løsningId,
        tidspunktForVurdering = tidspunktForVurdering,
        svangerskapspenger = Til.LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto.Svangerskapspenger(
            fom = svangerskapspenger.fom,
            tom = svangerskapspenger.tom,
            grad = svangerskapspenger.grad,
            vedtaksdato = svangerskapspenger.vedtaksdato,
        ),
    )

private fun Fra.LøsningParagraf_11_29KafkaDto.toDto() = Til.LøsningParagraf_11_29KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun Fra.KvalitetssikringParagraf_11_29KafkaDto.toDto() = Til.KvalitetssikringParagraf_11_29KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.LøsningParagraf_22_13KafkaDto.toDto() = Til.LøsningParagraf_22_13KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato,
    //FIXME: Legg til mapping av "begrunnelseForAnnet"
)

private fun Fra.KvalitetssikringParagraf_22_13KafkaDto.toDto() = Til.KvalitetssikringParagraf_22_13KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun Fra.VedtakKafkaDto.toDto() = Til.VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun Fra.InntektsgrunnlagKafkaDto.toDto() = Til.InntektsgrunnlagKafkaDto(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(Fra.InntekterForBeregningKafkaDto::toDto),
    yrkesskade = yrkesskade?.toDto(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun Fra.InntekterForBeregningKafkaDto.toDto() = Til.InntekterForBeregningKafkaDto(
    inntekter = inntekter.map(Fra.InntektKafkaDto::toDto),
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto(),
)

private fun Fra.InntektKafkaDto.toDto() = Til.InntektKafkaDto(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun Fra.InntektsgrunnlagForÅrKafkaDto.toDto() = Til.InntektsgrunnlagForÅrKafkaDto(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun Fra.YrkesskadeKafkaDto.toDto() = Til.YrkesskadeKafkaDto(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
)
