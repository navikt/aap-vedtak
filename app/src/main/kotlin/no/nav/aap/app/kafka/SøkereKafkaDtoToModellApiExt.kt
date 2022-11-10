package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto.*
import no.nav.aap.dto.kafka.TotrinnskontrollKafkaDto
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

private fun Paragraf_8_48.toModellApi() = Paragraf_8_48ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(LøsningMaskinellParagraf_8_48::toModellApi),
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13::toModellApi),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13::toModellApi),
)

private fun MedlemskapYrkesskade.toModellApi() = MedlemskapYrkesskadeModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell
        .map(LøsningMaskinellMedlemskapYrkesskade::toModellApi),
    løsning_medlemskap_yrkesskade_manuell = løsning_medlemskap_yrkesskade_manuell
        .map(LøsningManuellMedlemskapYrkesskade::toModellApi),
    kvalitetssikringer_medlemskap_yrkesskade = kvalitetssikringer_medlemskap_yrkesskade
        .map(KvalitetssikringMedlemskapYrkesskade::toModellApi),
)

private fun Paragraf_11_2.toModellApi() = Paragraf_11_2ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(LøsningMaskinellParagraf_11_2::toModellApi),
    løsning_11_2_manuell = løsning_11_2_manuell.map(LøsningManuellParagraf_11_2::toModellApi),
    kvalitetssikringer_11_2 = kvalitetssikringer_11_2.map(KvalitetssikringParagraf_11_2::toModellApi),
)

private fun Paragraf_11_3.toModellApi() = Paragraf_11_3ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_3_manuell = løsning_11_3_manuell.map(LøsningParagraf_11_3::toModellApi),
    kvalitetssikringer_11_3 = kvalitetssikringer_11_3.map(KvalitetssikringParagraf_11_3::toModellApi),
)

private fun Paragraf_11_4FørsteLedd.toModellApi() = Paragraf_11_4FørsteLeddModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_4AndreOgTredjeLedd.toModellApi() = Paragraf_11_4AndreOgTredjeLeddModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell
        .map(LøsningParagraf_11_4AndreOgTredjeLedd::toModellApi),
    kvalitetssikringer_11_4_ledd2_ledd3 = kvalitetssikringer_11_4_ledd2_ledd3
        .map(KvalitetssikringParagraf_11_4AndreOgTredjeLedd::toModellApi),
)

private fun Paragraf_11_5KafkaDto.toModellApi() = Paragraf_11_5ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller.map(TotrinnskontrollKafkaDto::toModellApi)
)

private fun Paragraf_11_5Yrkesskade.toModellApi() = Paragraf_11_5YrkesskadeModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell
        .map(LøsningParagraf_11_5_yrkesskade::toModellApi),
    kvalitetssikringer_11_5_yrkesskade = kvalitetssikringer_11_5_yrkesskade
        .map(KvalitetssikringParagraf_11_5Yrkesskade::toModellApi),
)

private fun Paragraf_11_6.toModellApi() = Paragraf_11_6ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(InnstillingParagraf_11_6::toModellApi),
    løsning_11_6_manuell = løsning_11_6_manuell.map(LøsningParagraf_11_6::toModellApi),
    kvalitetssikringer_11_6 = kvalitetssikringer_11_6.map(KvalitetssikringParagraf_11_6::toModellApi),
)

private fun Paragraf_11_14.toModellApi() = Paragraf_11_14ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_19.toModellApi() = Paragraf_11_19ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_19_manuell = løsning_11_19_manuell.map(LøsningParagraf_11_19::toModellApi),
    kvalitetssikringer_11_19 = kvalitetssikringer_11_19.map(KvalitetssikringParagraf_11_19::toModellApi),
)

private fun Paragraf_11_22.toModellApi() = Paragraf_11_22ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_22_manuell = løsning_11_22_manuell.map(LøsningParagraf_11_22::toModellApi),
    kvalitetssikringer_11_22 = kvalitetssikringer_11_22.map(KvalitetssikringParagraf_11_22::toModellApi),
)

private fun Paragraf_11_27FørsteLedd.toModellApi() = Paragraf_11_27FørsteLeddModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_27_maskinell = løsning_11_27_maskinell.map(LøsningMaskinellParagraf_11_27FørsteLedd::toModellApi),
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13::toModellApi),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13::toModellApi),
)

private fun Paragraf_11_29.toModellApi() = Paragraf_11_29ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_29_manuell = løsning_11_29_manuell.map(LøsningParagraf_11_29::toModellApi),
    kvalitetssikringer_11_29 = kvalitetssikringer_11_29.map(KvalitetssikringParagraf_11_29::toModellApi),
)

private fun Paragraf_22_13.toModellApi() = Paragraf_22_13ModellApi(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = enumValueOf(utfall),
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13::toModellApi),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13::toModellApi),
    søknadsdata = søknadsdata.map(SøknadsdataParagraf_22_13::toModellApi),
)

private fun SøknadsdataParagraf_22_13.toModellApi() = Paragraf_22_13ModellApi.SøknadsdataModellApi(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt,
)

private fun InnstillingParagraf_11_6.toModellApi() = InnstillingParagraf_11_6ModellApi(
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

private fun LøsningMaskinellParagraf_8_48.toModellApi() = SykepengedagerModellApi(
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

private fun LøsningParagraf_11_4AndreOgTredjeLedd.toModellApi() = LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
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
    kravOmNedsattArbeidsevneErOppfyltBegrunnelse = kravOmNedsattArbeidsevneErOppfyltBegrunnelse,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
    nedsettelseSkyldesSykdomEllerSkadeBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeBegrunnelse,
    kilder = kilder,
    legeerklæringDato = legeerklæringDato,
    sykmeldingDato = sykmeldingDato,
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
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = when (individuellBegrunnelse) {
        "<Mangler støtte for null backend>" -> null
        else -> individuellBegrunnelse
    },
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

private fun LøsningMaskinellParagraf_11_27FørsteLedd.toModellApi() = LøsningParagraf_11_27_FørsteLedd_ModellApi(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    svangerskapspenger = SvangerskapspengerModellApi(
        fom = svangerskapspenger.fom,
        tom = svangerskapspenger.tom,
        grad = svangerskapspenger.grad,
        vedtaksdato = svangerskapspenger.vedtaksdato,
    )
)

private fun LøsningParagraf_11_29.toModellApi() = LøsningParagraf_11_29ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_22_13.toModellApi() = LøsningParagraf_22_13ModellApi(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
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

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLedd.toModellApi() =
    KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
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

private fun KvalitetssikringParagraf_22_13.toModellApi() = KvalitetssikringParagraf_22_13ModellApi(
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
    etSettAvVurderteVilkårSomHarFørtTilDetteVedtaket = emptyList() // FIXME
)

private fun Inntektsgrunnlag.toModellApi() = InntektsgrunnlagModellApi(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(InntekterForBeregning::toModellApi),
    yrkesskade = yrkesskade?.toModellApi(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregning.toModellApi() = InntekterForBeregningModellApi(
    inntekter = inntekter.map(Inntekt::toModellApi),
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

private fun TotrinnskontrollKafkaDto.toModellApi() = TotrinnskontrollModellApi(
    løsning = løsning.toModellApi(),
    kvalitetssikring = kvalitetssikring?.toModellApi()
)