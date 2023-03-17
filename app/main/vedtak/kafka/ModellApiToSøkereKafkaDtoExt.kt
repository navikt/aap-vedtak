package vedtak.kafka

import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto.*
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.modellapi.*

internal fun SøkerModellApi.toSøkereKafkaDtoHistorikk(gammeltSekvensnummer: Long): SøkereKafkaDtoHistorikk {
    val søkereKafkaDto = toJson()
    val forrigeSøkereKafkaDto = søkereKafkaDto.toForrigeDto()
    return SøkereKafkaDtoHistorikk(
        søkereKafkaDto = søkereKafkaDto,
        forrigeSøkereKafkaDto = forrigeSøkereKafkaDto,
        sekvensnummer = gammeltSekvensnummer + 1
    )
}

private fun SøkerModellApi.toJson() = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(SakModellApi::toJson),
)

private fun SakModellApi.toJson() = SakKafkaDto(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(SakstypeModellApi::toJson),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toJson()
)

private fun SakstypeModellApi.toJson(): SakstypeKafkaDto {
    val visitor = object : VilkårsvurderingModellApiVisitor {
        var medlemskapYrkesskade: MedlemskapYrkesskadeKafkaDto? = null
        var paragraf_8_48: Paragraf_8_48KafkaDto? = null
        var paragraf_11_2: Paragraf_11_2KafkaDto? = null
        var paragraf_11_3: Paragraf_11_3KafkaDto? = null
        var paragraf_11_4FørsteLedd: Paragraf_11_4FørsteLeddKafkaDto? = null
        var paragraf_11_4AndreOgTredjeLedd: Paragraf_11_4AndreOgTredjeLeddKafkaDto? = null
        var paragraf_11_5KafkaDto: Paragraf_11_5KafkaDto? = null
        var paragraf_11_5Yrkesskade: Paragraf_11_5YrkesskadeKafkaDto? = null
        var paragraf_11_6: Paragraf_11_6KafkaDto? = null
        var paragraf_11_14: Paragraf_11_14KafkaDto? = null
        var paragraf_11_19: Paragraf_11_19KafkaDto? = null
        var paragraf_11_22: Paragraf_11_22KafkaDto? = null
        var paragraf_11_27FørsteLedd: Paragraf_11_27FørsteLeddKafkaDto? = null
        var paragraf_11_29: Paragraf_11_29KafkaDto? = null
        var paragraf_22_13: Paragraf_22_13KafkaDto? = null

        override fun visitMedlemskapYrkesskade(modellApi: MedlemskapYrkesskadeModellApi) {
            medlemskapYrkesskade = modellApi.toJson()
        }

        override fun visitParagraf_8_48(modellApi: Paragraf_8_48ModellApi) {
            paragraf_8_48 = modellApi.toJson()
        }

        override fun visitParagraf_11_2(modellApi: Paragraf_11_2ModellApi) {
            paragraf_11_2 = modellApi.toJson()
        }

        override fun visitParagraf_11_3(modellApi: Paragraf_11_3ModellApi) {
            paragraf_11_3 = modellApi.toJson()
        }

        override fun visitParagraf_11_4FørsteLedd(modellApi: Paragraf_11_4FørsteLeddModellApi) {
            paragraf_11_4FørsteLedd = modellApi.toJson()
        }

        override fun visitParagraf_11_4AndreOgTredjeLedd(modellApi: Paragraf_11_4AndreOgTredjeLeddModellApi) {
            paragraf_11_4AndreOgTredjeLedd = modellApi.toJson()
        }

        override fun visitParagraf_11_5(modellApi: Paragraf_11_5ModellApi) {
            paragraf_11_5KafkaDto = modellApi.toJson()
        }

        override fun visitParagraf_11_5Yrkesskade(modellApi: Paragraf_11_5YrkesskadeModellApi) {
            paragraf_11_5Yrkesskade = modellApi.toJson()
        }

        override fun visitParagraf_11_6(modellApi: Paragraf_11_6ModellApi) {
            paragraf_11_6 = modellApi.toJson()
        }

        override fun visitParagraf_11_14(modellApi: Paragraf_11_14ModellApi) {
            paragraf_11_14 = modellApi.toJson()
        }

        override fun visitParagraf_11_19(modellApi: Paragraf_11_19ModellApi) {
            paragraf_11_19 = modellApi.toJson()
        }

        override fun visitParagraf_11_22(modellApi: Paragraf_11_22ModellApi) {
            paragraf_11_22 = modellApi.toJson()
        }

        override fun visitParagraf_11_27FørsteLedd(modellApi: Paragraf_11_27FørsteLeddModellApi) {
            paragraf_11_27FørsteLedd = modellApi.toJson()
        }

        override fun visitParagraf_11_29(modellApi: Paragraf_11_29ModellApi) {
            paragraf_11_29 = modellApi.toJson()
        }

        override fun visitParagraf_22_13(modellApi: Paragraf_22_13ModellApi) {
            paragraf_22_13 = modellApi.toJson()
        }
    }

    vilkårsvurderinger.forEach { it.accept(visitor) }

    return SakstypeKafkaDto(
        type = type,
        aktiv = aktiv,
        medlemskapYrkesskade = visitor.medlemskapYrkesskade,
        paragraf_8_48 = visitor.paragraf_8_48,
        paragraf_11_2 = visitor.paragraf_11_2,
        paragraf_11_3 = visitor.paragraf_11_3,
        paragraf_11_4FørsteLedd = visitor.paragraf_11_4FørsteLedd,
        paragraf_11_4AndreOgTredjeLedd = visitor.paragraf_11_4AndreOgTredjeLedd,
        paragraf_11_5 = visitor.paragraf_11_5KafkaDto,
        paragraf_11_5Yrkesskade = visitor.paragraf_11_5Yrkesskade,
        paragraf_11_6 = visitor.paragraf_11_6,
        paragraf_11_14 = visitor.paragraf_11_14,
        paragraf_11_19 = visitor.paragraf_11_19,
        paragraf_11_22 = visitor.paragraf_11_22,
        paragraf_11_27FørsteLedd = visitor.paragraf_11_27FørsteLedd,
        paragraf_11_29 = visitor.paragraf_11_29,
        paragraf_22_13 = visitor.paragraf_22_13,
    )
}

private fun MedlemskapYrkesskadeModellApi.toJson() = MedlemskapYrkesskadeKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell
        .map(LøsningMaskinellMedlemskapYrkesskadeModellApi::toJson),
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningManuellMedlemskapYrkesskadeModellApi, KvalitetssikringMedlemskapYrkesskadeModellApi>::toJsonMedlemskapYrkesskade),
)

private fun Paragraf_8_48ModellApi.toJson() = Paragraf_8_48KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(SykepengedagerModellApi::toJson),
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>::toJson_22_13),
)

private fun Paragraf_11_2ModellApi.toJson() = Paragraf_11_2KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(LøsningMaskinellParagraf_11_2ModellApi::toJson),
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_2ModellApi, KvalitetssikringParagraf_11_2ModellApi>::toJson_11_2),
)

private fun Paragraf_11_3ModellApi.toJson() = Paragraf_11_3KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_3ModellApi, KvalitetssikringParagraf_11_3ModellApi>::toJson_11_3),
)

private fun Paragraf_11_4FørsteLeddModellApi.toJson() = Paragraf_11_4FørsteLeddKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_4AndreOgTredjeLeddModellApi.toJson() = Paragraf_11_4AndreOgTredjeLeddKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_4AndreOgTredjeLeddModellApi, KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi>::toJson_11_4AndreOgTredjeLedd),
)

private fun Paragraf_11_5ModellApi.toJson() = Paragraf_11_5KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_5ModellApi, KvalitetssikringParagraf_11_5ModellApi>::toJson_11_5)
)

private fun Paragraf_11_5YrkesskadeModellApi.toJson() = Paragraf_11_5YrkesskadeKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_5YrkesskadeModellApi, KvalitetssikringParagraf_11_5YrkesskadeModellApi>::toJson_11_5Yrkesskade),
)

private fun Paragraf_11_6ModellApi.toJson() = Paragraf_11_6KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(InnstillingParagraf_11_6ModellApi::toJson),
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_6ModellApi, KvalitetssikringParagraf_11_6ModellApi>::toJson_11_6),
)

private fun Paragraf_11_14ModellApi.toJson() = Paragraf_11_14KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_19ModellApi.toJson() = Paragraf_11_19KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_19ModellApi, KvalitetssikringParagraf_11_19ModellApi>::toJson_11_19),
)

private fun Paragraf_11_22ModellApi.toJson() = Paragraf_11_22KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_22ModellApi, KvalitetssikringParagraf_11_22ModellApi>::toJson_11_22),
)

private fun Paragraf_11_27FørsteLeddModellApi.toJson() = Paragraf_11_27FørsteLeddKafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_27_maskinell = løsning_11_27_maskinell.map(LøsningParagraf_11_27_FørsteLedd_ModellApi::toJson),
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>::toJson_22_13),
)

private fun Paragraf_11_29ModellApi.toJson() = Paragraf_11_29KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_11_29ModellApi, KvalitetssikringParagraf_11_29ModellApi>::toJson_11_29),
)

private fun Paragraf_22_13ModellApi.toJson() = Paragraf_22_13KafkaDto(
    vilkårsvurderingsid = vilkårsvurderingsid,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    totrinnskontroller = totrinnskontroller
        .map(TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>::toJson_22_13),
    søknadsdata = søknadsdata.map(Paragraf_22_13ModellApi.SøknadsdataModellApi::toJson),
)

private fun Paragraf_22_13ModellApi.SøknadsdataModellApi.toJson() = SøknadsdataParagraf_22_13KafkaDto(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt,
)

private fun InnstillingParagraf_11_6ModellApi.toJson() = InnstillingParagraf_11_6KafkaDto(
    innstillingId = innstillingId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun LøsningMaskinellMedlemskapYrkesskadeModellApi.toJson() = LøsningMaskinellMedlemskapYrkesskadeKafkaDto(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskadeModellApi.toJson() = LøsningManuellMedlemskapYrkesskadeKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun SykepengedagerModellApi.toJson() = LøsningMaskinellParagraf_8_48KafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    sykepengedager = sykepengedager?.let { sykepengedager ->
        LøsningMaskinellParagraf_8_48KafkaDto.Sykepengedager(
            gjenståendeSykedager = sykepengedager.gjenståendeSykedager,
            foreløpigBeregnetSluttPåSykepenger = sykepengedager.foreløpigBeregnetSluttPåSykepenger,
            kilde = sykepengedager.kilde,
        )
    }
)

private fun LøsningMaskinellParagraf_11_2ModellApi.toJson() = LøsningMaskinellParagraf_11_2KafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_2ModellApi.toJson() = LøsningManuellParagraf_11_2KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3ModellApi.toJson() = LøsningParagraf_11_3KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4AndreOgTredjeLeddModellApi.toJson() = LøsningParagraf_11_4AndreOgTredjeLeddKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_5ModellApi.toJson() = LøsningParagraf_11_5KafkaDto(
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

private fun LøsningParagraf_11_5YrkesskadeModellApi.toJson() = LøsningParagraf_11_5_yrkesskadeKafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6ModellApi.toJson() = LøsningParagraf_11_6KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun LøsningParagraf_11_19ModellApi.toJson() = LøsningParagraf_11_19KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22ModellApi.toJson() = LøsningParagraf_11_22KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningParagraf_11_27_FørsteLedd_ModellApi.toJson() = LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    svangerskapspenger = LøsningMaskinellParagraf_11_27FørsteLeddKafkaDto.Svangerskapspenger(
        fom = svangerskapspenger.fom,
        tom = svangerskapspenger.tom,
        grad = svangerskapspenger.grad,
        vedtaksdato = svangerskapspenger.vedtaksdato,
    )
)

private fun LøsningParagraf_11_29ModellApi.toJson() = LøsningParagraf_11_29KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_22_13ModellApi.toJson() = LøsningParagraf_22_13KafkaDto(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato,
    begrunnelseForAnnet = begrunnelseForAnnet,
)

private fun KvalitetssikringMedlemskapYrkesskadeModellApi.toJson() = KvalitetssikringMedlemskapYrkesskadeKafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_2ModellApi.toJson() = KvalitetssikringParagraf_11_2KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_3ModellApi.toJson() = KvalitetssikringParagraf_11_3KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi.toJson() =
    KvalitetssikringParagraf_11_4AndreOgTredjeLeddKafkaDto(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

private fun KvalitetssikringParagraf_11_5ModellApi.toJson() = KvalitetssikringParagraf_11_5KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    kravOmNedsattArbeidsevneErGodkjent = kravOmNedsattArbeidsevneErGodkjent,
    kravOmNedsattArbeidsevneErGodkjentBegrunnelse = kravOmNedsattArbeidsevneErGodkjentBegrunnelse,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjent = nedsettelseSkyldesSykdomEllerSkadeErGodkjent,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse,
)

private fun KvalitetssikringParagraf_11_5YrkesskadeModellApi.toJson() = KvalitetssikringParagraf_11_5YrkesskadeKafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_6ModellApi.toJson() = KvalitetssikringParagraf_11_6KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_22_13ModellApi.toJson() = KvalitetssikringParagraf_22_13KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_19ModellApi.toJson() = KvalitetssikringParagraf_11_19KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_22ModellApi.toJson() = KvalitetssikringParagraf_11_22KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_29ModellApi.toJson() = KvalitetssikringParagraf_11_29KafkaDto(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)


private fun VedtakModellApi.toJson() = VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toJson(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun InntektsgrunnlagModellApi.toJson() = InntektsgrunnlagKafkaDto(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(InntekterForBeregningModellApi::toJson),
    yrkesskade = yrkesskade?.toJson(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregningModellApi.toJson() = InntekterForBeregningKafkaDto(
    inntekter = inntekter.map(InntektModellApi::toJson),
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toJson(),
)

private fun InntektModellApi.toJson() = InntektKafkaDto(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅrModellApi.toJson() = InntektsgrunnlagForÅrKafkaDto(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun YrkesskadeModellApi.toJson() = YrkesskadeKafkaDto(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toJson(),
)

private fun TotrinnskontrollModellApi<LøsningManuellMedlemskapYrkesskadeModellApi, KvalitetssikringMedlemskapYrkesskadeModellApi>.toJsonMedlemskapYrkesskade() =
    TotrinnskontrollMedlemskapYrkesskadeKafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_2ModellApi, KvalitetssikringParagraf_11_2ModellApi>.toJson_11_2() =
    Totrinnskontroll_11_2KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_3ModellApi, KvalitetssikringParagraf_11_3ModellApi>.toJson_11_3() =
    Totrinnskontroll_11_3KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_4AndreOgTredjeLeddModellApi, KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi>.toJson_11_4AndreOgTredjeLedd() =
    Totrinnskontroll_11_4AndreOgTredjeLeddKafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_5ModellApi, KvalitetssikringParagraf_11_5ModellApi>.toJson_11_5() =
    Totrinnskontroll_11_5KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_5YrkesskadeModellApi, KvalitetssikringParagraf_11_5YrkesskadeModellApi>.toJson_11_5Yrkesskade() =
    Totrinnskontroll_11_5YrkesskadeKafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_6ModellApi, KvalitetssikringParagraf_11_6ModellApi>.toJson_11_6() =
    Totrinnskontroll_11_6KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_19ModellApi, KvalitetssikringParagraf_11_19ModellApi>.toJson_11_19() =
    Totrinnskontroll_11_19KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_22ModellApi, KvalitetssikringParagraf_11_22ModellApi>.toJson_11_22() =
    Totrinnskontroll_11_22KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_11_29ModellApi, KvalitetssikringParagraf_11_29ModellApi>.toJson_11_29() =
    Totrinnskontroll_11_29KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )

private fun TotrinnskontrollModellApi<LøsningParagraf_22_13ModellApi, KvalitetssikringParagraf_22_13ModellApi>.toJson_22_13() =
    Totrinnskontroll_22_13KafkaDto(
        løsning = løsning.toJson(),
        kvalitetssikring = kvalitetssikring?.toJson()
    )
