package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto
import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto.*
import no.nav.aap.dto.kafka.SøkereKafkaDto

internal fun ForrigeSøkereKafkaDto.toDto() = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(SakKafkaDto::toDto),
)

private fun SakKafkaDto.toDto() = SøkereKafkaDto.SakKafkaDto(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(SakstypeKafkaDto::toDto),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toDto()
)

private fun SakstypeKafkaDto.toDto() = SøkereKafkaDto.SakstypeKafkaDto(
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

private fun MedlemskapYrkesskadeKafkaDto.toMedlemskapYrkesskade() = SøkereKafkaDto.MedlemskapYrkesskadeKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell =
    løsning_medlemskap_yrkesskade_maskinell.map(LøsningMaskinellMedlemskapYrkesskadeKafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(TotrinnskontrollMedlemskapYrkesskadeKafkaDto::toDto),
)

private fun TotrinnskontrollMedlemskapYrkesskadeKafkaDto.toDto() = SøkereKafkaDto.TotrinnskontrollMedlemskapYrkesskadeKafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_8_48KafkaDto.toParagraf_8_48() = SøkereKafkaDto.Paragraf_8_48KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(LøsningMaskinellParagraf_8_48KafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_22_13KafkaDto::toDto),
)

private fun Totrinnskontroll_22_13KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_22_13KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_2KafkaDto.toParagraf_11_2() = SøkereKafkaDto.Paragraf_11_2KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(LøsningMaskinellParagraf_11_2KafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_2KafkaDto::toDto),
)

private fun Totrinnskontroll_11_2KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_2KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_3KafkaDto.toParagraf_11_3() = SøkereKafkaDto.Paragraf_11_3KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_3KafkaDto::toDto),
)

private fun Totrinnskontroll_11_3KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_3KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_4FørsteLeddKafkaDto.toParagraf_11_4FørsteLedd() = SøkereKafkaDto.Paragraf_11_4FørsteLeddKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_4AndreOgTredjeLeddKafkaDto.toParagraf_11_4AndreOgTredjeLedd() =
    SøkereKafkaDto.Paragraf_11_4AndreOgTredjeLeddKafkaDto(
        vilkårsvurderingsid = vilkårsvurderingsid,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto::toDto)
    )

private fun Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_5KafkaDto.toParagraf_11_5() = SøkereKafkaDto.Paragraf_11_5KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_5KafkaDto::toDto)
)

private fun Totrinnskontroll_11_5KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_5KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_5YrkesskadeKafkaDto.toParagraf_11_5Yrkesskade() = SøkereKafkaDto.Paragraf_11_5YrkesskadeKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_5YrkesskadeKafkaDto::toDto),
)

private fun Totrinnskontroll_11_5YrkesskadeKafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_5YrkesskadeKafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_6KafkaDto.toParagraf_11_6() = SøkereKafkaDto.Paragraf_11_6KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(InnstillingParagraf_11_6KafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_6KafkaDto::toDto),
)

private fun Totrinnskontroll_11_6KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_6KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_14KafkaDto.toParagraf_11_14() = SøkereKafkaDto.Paragraf_11_14KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_19KafkaDto.toParagraf_11_19() = SøkereKafkaDto.Paragraf_11_19KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_19KafkaDto::toDto),
)

private fun Totrinnskontroll_11_19KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_19KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_22KafkaDto.toParagraf_11_22() = SøkereKafkaDto.Paragraf_11_22KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_22KafkaDto::toDto),
)

private fun Totrinnskontroll_11_22KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_22KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_11_27FørsteLeddKafkaDto.toParagraf_11_27FørsteLedd() = SøkereKafkaDto.Paragraf_11_27FørsteLeddKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_27_maskinell = løsning_11_27_maskinell.map(LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto::toDto),
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_22_13KafkaDto::toDto),
)

private fun Paragraf_11_29KafkaDto.toParagraf_11_29() = SøkereKafkaDto.Paragraf_11_29KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    // FIXME, fiks ved neste migrering
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_11_29KafkaDto::toDto),
)

private fun Totrinnskontroll_11_29KafkaDto.toDto() = SøkereKafkaDto.Totrinnskontroll_11_29KafkaDto(
    løsning = løsning.toDto(),
    kvalitetssikring = kvalitetssikring?.toDto(),
)

private fun Paragraf_22_13KafkaDto.toParagraf_22_13() = SøkereKafkaDto.Paragraf_22_13KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(Totrinnskontroll_22_13KafkaDto::toDto),
    søknadsdata = søknadsdata.map(SøknadsdataParagraf_22_13KafkaDto::toDto),
)

private fun SøknadsdataParagraf_22_13KafkaDto.toDto() = SøkereKafkaDto.SøknadsdataParagraf_22_13KafkaDto(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt
)

private fun LøsningMaskinellMedlemskapYrkesskadeKafkaDto.toDto() = SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskadeKafkaDto(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskadeKafkaDto.toDto() = SøkereKafkaDto.LøsningManuellMedlemskapYrkesskadeKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun KvalitetssikringMedlemskapYrkesskadeKafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringMedlemskapYrkesskadeKafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningMaskinellParagraf_8_48KafkaDto.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_8_48KafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    sykepengedager = sykepengedager?.let { sykepenger ->
        SøkereKafkaDto.LøsningMaskinellParagraf_8_48KafkaDto.Sykepengedager(
            gjenståendeSykedager = sykepenger.gjenståendeSykedager,
            foreløpigBeregnetSluttPåSykepenger = sykepenger.foreløpigBeregnetSluttPåSykepenger,
            kilde = sykepenger.kilde,
        )
    },
)

private fun LøsningMaskinellParagraf_11_2KafkaDto.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_11_2KafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2KafkaDto.toDto() = SøkereKafkaDto.LøsningManuellParagraf_11_2KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun KvalitetssikringParagraf_11_2KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_2KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_3KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_3KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringParagraf_11_3KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_3KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto.toDto() =
    SøkereKafkaDto.KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse,
    )

private fun LøsningParagraf_11_5KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_5KafkaDto(
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

private fun KvalitetssikringParagraf_11_5KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_5KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_5_yrkesskadeKafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_5_yrkesskadeKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun KvalitetssikringParagraf_11_5YrkesskadeKafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_5YrkesskadeKafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun InnstillingParagraf_11_6KafkaDto.toDto() = SøkereKafkaDto.InnstillingParagraf_11_6KafkaDto(
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

private fun LøsningParagraf_11_6KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_6KafkaDto(
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

private fun KvalitetssikringParagraf_11_6KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_6KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_19KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_19KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun KvalitetssikringParagraf_11_19KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_19KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_22KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_22KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun KvalitetssikringParagraf_11_22KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_22KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    svangerskapspenger = SøkereKafkaDto.LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto.Svangerskapspenger(
        fom = svangerskapspenger.fom,
        tom = svangerskapspenger.tom,
        grad = svangerskapspenger.grad,
        vedtaksdato = svangerskapspenger.vedtaksdato,
    ),
)

private fun LøsningParagraf_11_29KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_11_29KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun KvalitetssikringParagraf_11_29KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_29KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_22_13KafkaDto.toDto() = SøkereKafkaDto.LøsningParagraf_22_13KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun KvalitetssikringParagraf_22_13KafkaDto.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_22_13KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun VedtakKafkaDto.toDto() = SøkereKafkaDto.VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun InntektsgrunnlagKafkaDto.toDto() = SøkereKafkaDto.InntektsgrunnlagKafkaDto(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(InntekterForBeregningKafkaDto::toDto),
    yrkesskade = yrkesskade?.toDto(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregningKafkaDto.toDto() = SøkereKafkaDto.InntekterForBeregningKafkaDto(
    inntekter = inntekter.map(InntektKafkaDto::toDto),
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toDto(),
)

private fun InntektKafkaDto.toDto() = SøkereKafkaDto.InntektKafkaDto(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅrKafkaDto.toDto() = SøkereKafkaDto.InntektsgrunnlagForÅrKafkaDto(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun YrkesskadeKafkaDto.toDto() = SøkereKafkaDto.YrkesskadeKafkaDto(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toDto(),
)
