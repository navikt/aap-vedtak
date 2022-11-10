package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto
import no.nav.aap.dto.kafka.ForrigeSøkereKafkaDto.*
import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.TotrinnskontrollKafkaDto

internal fun ForrigeSøkereKafkaDto.toDto() = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(Sak::toDto),
)

private fun Sak.toDto() = SøkereKafkaDto.Sak(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(Sakstype::toDto),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toDto()
)

private fun Sakstype.toDto() = SøkereKafkaDto.Sakstype(
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

private fun MedlemskapYrkesskade.toMedlemskapYrkesskade() = SøkereKafkaDto.MedlemskapYrkesskade(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell =
    løsning_medlemskap_yrkesskade_maskinell.map(LøsningMaskinellMedlemskapYrkesskade::toDto),
    løsning_medlemskap_yrkesskade_manuell =
    løsning_medlemskap_yrkesskade_manuell.map(LøsningManuellMedlemskapYrkesskade::toDto),
    kvalitetssikringer_medlemskap_yrkesskade =
    kvalitetssikringer_medlemskap_yrkesskade.map(KvalitetssikringMedlemskapYrkesskade::toDto),
)

private fun Paragraf_8_48.toParagraf_8_48() = SøkereKafkaDto.Paragraf_8_48(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(LøsningMaskinellParagraf_8_48::toDto),
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13::toDto),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13::toDto),
)

private fun Paragraf_11_2.toParagraf_11_2() = SøkereKafkaDto.Paragraf_11_2(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(LøsningMaskinellParagraf_11_2::toDto),
    løsning_11_2_manuell = løsning_11_2_manuell.map(LøsningManuellParagraf_11_2::toDto),
    kvalitetssikringer_11_2 = kvalitetssikringer_11_2.map(KvalitetssikringParagraf_11_2::toDto),
)

private fun Paragraf_11_3.toParagraf_11_3() = SøkereKafkaDto.Paragraf_11_3(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_3_manuell = løsning_11_3_manuell.map(LøsningParagraf_11_3::toDto),
    kvalitetssikringer_11_3 = kvalitetssikringer_11_3.map(KvalitetssikringParagraf_11_3::toDto),
)

private fun Paragraf_11_4FørsteLedd.toParagraf_11_4FørsteLedd() = SøkereKafkaDto.Paragraf_11_4FørsteLedd(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_4AndreOgTredjeLedd.toParagraf_11_4AndreOgTredjeLedd() =
    SøkereKafkaDto.Paragraf_11_4AndreOgTredjeLedd(
        vilkårsvurderingsid = vilkårsvurderingsid,
        vurdertAv = vurdertAv,
        kvalitetssikretAv = kvalitetssikretAv,
        paragraf = paragraf,
        ledd = ledd,
        tilstand = tilstand,
        utfall = utfall,
        vurdertMaskinelt = vurdertMaskinelt,
        løsning_11_4_ledd2_ledd3_manuell =
        løsning_11_4_ledd2_ledd3_manuell.map(LøsningParagraf_11_4AndreOgTredjeLedd::toDto),
        kvalitetssikringer_11_4_ledd2_ledd3 =
        kvalitetssikringer_11_4_ledd2_ledd3.map(KvalitetssikringParagraf_11_4AndreOgTredjeLedd::toDto),
    )

private fun Paragraf_11_5KafkaDto.toParagraf_11_5() = SøkereKafkaDto.Paragraf_11_5KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    // FIXME, fiks ved neste migrering
    totrinnskontroller = løsning_11_5_manuell.map {  løsning ->
        TotrinnskontrollKafkaDto(
            løsning = løsning.toDto(),
            kvalitetssikring = kvalitetssikringer_11_5.firstOrNull { kvalitetssikring -> kvalitetssikring.løsningId == løsning.løsningId }?.toDto()
        )
    }
)

private fun Paragraf_11_5Yrkesskade.toParagraf_11_5Yrkesskade() = SøkereKafkaDto.Paragraf_11_5Yrkesskade(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell.map(LøsningParagraf_11_5_yrkesskade::toDto),
    kvalitetssikringer_11_5_yrkesskade = kvalitetssikringer_11_5_yrkesskade.map(KvalitetssikringParagraf_11_5Yrkesskade::toDto),
)

private fun Paragraf_11_6.toParagraf_11_6() = SøkereKafkaDto.Paragraf_11_6(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(InnstillingParagraf_11_6::toDto),
    løsning_11_6_manuell = løsning_11_6_manuell.map(LøsningParagraf_11_6::toDto),
    kvalitetssikringer_11_6 = kvalitetssikringer_11_6.map(KvalitetssikringParagraf_11_6::toDto),
)

private fun Paragraf_11_14.toParagraf_11_14() = SøkereKafkaDto.Paragraf_11_14(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_19.toParagraf_11_19() = SøkereKafkaDto.Paragraf_11_19(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_19_manuell = løsning_11_19_manuell.map(LøsningParagraf_11_19::toDto),
    kvalitetssikringer_11_19 = kvalitetssikringer_11_19.map(KvalitetssikringParagraf_11_19::toDto),
)

private fun Paragraf_11_22.toParagraf_11_22() = SøkereKafkaDto.Paragraf_11_22(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_22_manuell = løsning_11_22_manuell.map(LøsningParagraf_11_22::toDto),
    kvalitetssikringer_11_22 = kvalitetssikringer_11_22.map(KvalitetssikringParagraf_11_22::toDto),
)

private fun Paragraf_11_27FørsteLedd.toParagraf_11_27FørsteLedd() = SøkereKafkaDto.Paragraf_11_27FørsteLedd(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_27_maskinell = løsning_11_27_maskinell.map(LøsningMaskinellParagraf_11_27FørsteLedd::toDto),
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13::toDto),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13::toDto),
)

private fun Paragraf_11_29.toParagraf_11_29() = SøkereKafkaDto.Paragraf_11_29(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_29_manuell = løsning_11_29_manuell.map(LøsningParagraf_11_29::toDto),
    kvalitetssikringer_11_29 = kvalitetssikringer_11_29.map(KvalitetssikringParagraf_11_29::toDto),
)

private fun Paragraf_22_13.toParagraf_22_13() = SøkereKafkaDto.Paragraf_22_13(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13::toDto),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13::toDto),
    søknadsdata = søknadsdata.map(SøknadsdataParagraf_22_13::toDto),
)

private fun SøknadsdataParagraf_22_13.toDto() = SøkereKafkaDto.SøknadsdataParagraf_22_13(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt
)

private fun LøsningMaskinellMedlemskapYrkesskade.toDto() = SøkereKafkaDto.LøsningMaskinellMedlemskapYrkesskade(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskade.toDto() = SøkereKafkaDto.LøsningManuellMedlemskapYrkesskade(
    løsningId = løsningId,
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

private fun LøsningMaskinellParagraf_8_48.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_8_48(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    sykepengedager = sykepengedager?.let { sykepenger ->
        SøkereKafkaDto.LøsningMaskinellParagraf_8_48.Sykepengedager(
            gjenståendeSykedager = sykepenger.gjenståendeSykedager,
            foreløpigBeregnetSluttPåSykepenger = sykepenger.foreløpigBeregnetSluttPåSykepenger,
            kilde = sykepenger.kilde,
        )
    },
)

private fun LøsningMaskinellParagraf_11_2.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_11_2(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningManuellParagraf_11_2.toDto() = SøkereKafkaDto.LøsningManuellParagraf_11_2(
    løsningId = løsningId,
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
    løsningId = løsningId,
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

private fun LøsningParagraf_11_4AndreOgTredjeLedd.toDto() = SøkereKafkaDto.LøsningParagraf_11_4AndreOgTredjeLedd(
    løsningId = løsningId,
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

private fun KvalitetssikringParagraf_11_5.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_5(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_5_yrkesskade.toDto() = SøkereKafkaDto.LøsningParagraf_11_5_yrkesskade(
    løsningId = løsningId,
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

private fun InnstillingParagraf_11_6.toDto() = SøkereKafkaDto.InnstillingParagraf_11_6(
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

private fun LøsningParagraf_11_6.toDto() = SøkereKafkaDto.LøsningParagraf_11_6(
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

private fun KvalitetssikringParagraf_11_6.toDto() = SøkereKafkaDto.KvalitetssikringParagraf_11_6(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse,
)

private fun LøsningParagraf_11_19.toDto() = SøkereKafkaDto.LøsningParagraf_11_19(
    løsningId = løsningId,
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
    løsningId = løsningId,
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

private fun LøsningMaskinellParagraf_11_27FørsteLedd.toDto() = SøkereKafkaDto.LøsningMaskinellParagraf_11_27FørsteLedd(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    svangerskapspenger = SøkereKafkaDto.LøsningMaskinellParagraf_11_27FørsteLedd.Svangerskapspenger(
        fom = svangerskapspenger.fom,
        tom = svangerskapspenger.tom,
        grad = svangerskapspenger.grad,
        vedtaksdato = svangerskapspenger.vedtaksdato,
    ),
)

private fun LøsningParagraf_11_29.toDto() = SøkereKafkaDto.LøsningParagraf_11_29(
    løsningId = løsningId,
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
    løsningId = løsningId,
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
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(InntekterForBeregning::toDto),
    yrkesskade = yrkesskade?.toDto(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregning.toDto() = SøkereKafkaDto.InntekterForBeregning(
    inntekter = inntekter.map(Inntekt::toDto),
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
