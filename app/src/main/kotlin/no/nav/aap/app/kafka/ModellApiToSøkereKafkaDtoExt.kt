package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.SøkereKafkaDto
import no.nav.aap.dto.kafka.SøkereKafkaDto.*
import no.nav.aap.modellapi.*

internal fun SøkerModellApi.toJson(gammelSekvensnummer: Long) = SøkereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    sekvensnummer = gammelSekvensnummer + 1,
    saker = saker.map(SakModellApi::toJson),
)

private fun SakModellApi.toJson() = Sak(
    saksid = saksid,
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    sakstyper = sakstyper.map(SakstypeModellApi::toJson),
    søknadstidspunkt = søknadstidspunkt,
    vedtak = vedtak?.toJson()
)

private fun SakstypeModellApi.toJson(): Sakstype {
    val visitor = object : VilkårsvurderingModellApiVisitor {
        var medlemskapYrkesskade: MedlemskapYrkesskade? = null
        var paragraf_8_48: Paragraf_8_48? = null
        var paragraf_11_2: Paragraf_11_2? = null
        var paragraf_11_3: Paragraf_11_3? = null
        var paragraf_11_4FørsteLedd: Paragraf_11_4FørsteLedd? = null
        var paragraf_11_4AndreOgTredjeLedd: Paragraf_11_4AndreOgTredjeLedd? = null
        var paragraf_11_5: Paragraf_11_5? = null
        var paragraf_11_5Yrkesskade: Paragraf_11_5Yrkesskade? = null
        var paragraf_11_6: Paragraf_11_6? = null
        var paragraf_11_14: Paragraf_11_14? = null
        var paragraf_11_19: Paragraf_11_19? = null
        var paragraf_11_22: Paragraf_11_22? = null
        var paragraf_11_27FørsteLedd: Paragraf_11_27FørsteLedd? = null
        var paragraf_11_29: Paragraf_11_29? = null
        var paragraf_22_13: Paragraf_22_13? = null

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
            paragraf_11_5 = modellApi.toJson()
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

    return Sakstype(
        type = type,
        aktiv = aktiv,
        medlemskapYrkesskade = visitor.medlemskapYrkesskade,
        paragraf_8_48 = visitor.paragraf_8_48,
        paragraf_11_2 = visitor.paragraf_11_2,
        paragraf_11_3 = visitor.paragraf_11_3,
        paragraf_11_4FørsteLedd = visitor.paragraf_11_4FørsteLedd,
        paragraf_11_4AndreOgTredjeLedd = visitor.paragraf_11_4AndreOgTredjeLedd,
        paragraf_11_5 = visitor.paragraf_11_5,
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

private fun MedlemskapYrkesskadeModellApi.toJson() = MedlemskapYrkesskade(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_medlemskap_yrkesskade_maskinell = løsning_medlemskap_yrkesskade_maskinell
        .map(LøsningMaskinellMedlemskapYrkesskadeModellApi::toJson),
    løsning_medlemskap_yrkesskade_manuell = løsning_medlemskap_yrkesskade_manuell
        .map(LøsningManuellMedlemskapYrkesskadeModellApi::toJson),
    kvalitetssikringer_medlemskap_yrkesskade = kvalitetssikringer_medlemskap_yrkesskade
        .map(KvalitetssikringMedlemskapYrkesskadeModellApi::toJson),
)

private fun Paragraf_8_48ModellApi.toJson() = Paragraf_8_48(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_8_48_maskinell = løsning_8_48_maskinell.map(SykepengedagerModellApi::toJson),
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13ModellApi::toJson),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13ModellApi::toJson),
)

private fun Paragraf_11_2ModellApi.toJson() = Paragraf_11_2(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_2_maskinell = løsning_11_2_maskinell.map(LøsningMaskinellParagraf_11_2ModellApi::toJson),
    løsning_11_2_manuell = løsning_11_2_manuell.map(LøsningParagraf_11_2ModellApi::toJson),
    kvalitetssikringer_11_2 = kvalitetssikringer_11_2.map(KvalitetssikringParagraf_11_2ModellApi::toJson),
)

private fun Paragraf_11_3ModellApi.toJson() = Paragraf_11_3(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_3_manuell = løsning_11_3_manuell.map(LøsningParagraf_11_3ModellApi::toJson),
    kvalitetssikringer_11_3 = kvalitetssikringer_11_3.map(KvalitetssikringParagraf_11_3ModellApi::toJson),
)

private fun Paragraf_11_4FørsteLeddModellApi.toJson() = Paragraf_11_4FørsteLedd(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_4AndreOgTredjeLeddModellApi.toJson() = Paragraf_11_4AndreOgTredjeLedd(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_4_ledd2_ledd3_manuell = løsning_11_4_ledd2_ledd3_manuell
        .map(LøsningParagraf_11_4AndreOgTredjeLeddModellApi::toJson),
    kvalitetssikringer_11_4_ledd2_ledd3 = kvalitetssikringer_11_4_ledd2_ledd3
        .map(KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi::toJson),
)

private fun Paragraf_11_5ModellApi.toJson() = Paragraf_11_5(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_5_manuell = løsning_11_5_manuell.map(LøsningParagraf_11_5ModellApi::toJson),
    kvalitetssikringer_11_5 = kvalitetssikringer_11_5.map(KvalitetssikringParagraf_11_5ModellApi::toJson),
)

private fun Paragraf_11_5YrkesskadeModellApi.toJson() = Paragraf_11_5Yrkesskade(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_5_yrkesskade_manuell = løsning_11_5_yrkesskade_manuell
        .map(LøsningParagraf_11_5YrkesskadeModellApi::toJson),
    kvalitetssikringer_11_5_yrkesskade = kvalitetssikringer_11_5_yrkesskade
        .map(KvalitetssikringParagraf_11_5YrkesskadeModellApi::toJson),
)

private fun Paragraf_11_6ModellApi.toJson() = Paragraf_11_6(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    innstillinger_11_6 = innstillinger_11_6.map(InnstillingParagraf_11_6ModellApi::toJson),
    løsning_11_6_manuell = løsning_11_6_manuell.map(LøsningParagraf_11_6ModellApi::toJson),
    kvalitetssikringer_11_6 = kvalitetssikringer_11_6.map(KvalitetssikringParagraf_11_6ModellApi::toJson),
)

private fun Paragraf_11_14ModellApi.toJson() = Paragraf_11_14(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
)

private fun Paragraf_11_19ModellApi.toJson() = Paragraf_11_19(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_19_manuell = løsning_11_19_manuell.map(LøsningParagraf_11_19ModellApi::toJson),
    kvalitetssikringer_11_19 = kvalitetssikringer_11_19.map(KvalitetssikringParagraf_11_19ModellApi::toJson),
)

private fun Paragraf_11_22ModellApi.toJson() = Paragraf_11_22(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_22_manuell = løsning_11_22_manuell.map(LøsningParagraf_11_22ModellApi::toJson),
    kvalitetssikringer_11_22 = kvalitetssikringer_11_22.map(KvalitetssikringParagraf_11_22ModellApi::toJson),
)

private fun Paragraf_11_27FørsteLeddModellApi.toJson() = Paragraf_11_27FørsteLedd(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_27_maskinell = løsning_11_27_maskinell.map(LøsningParagraf_11_27_FørsteLedd_ModellApi::toJson),
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13ModellApi::toJson),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13ModellApi::toJson),
)

private fun Paragraf_11_29ModellApi.toJson() = Paragraf_11_29(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_11_29_manuell = løsning_11_29_manuell.map(LøsningParagraf_11_29ModellApi::toJson),
    kvalitetssikringer_11_29 = kvalitetssikringer_11_29.map(KvalitetssikringParagraf_11_29ModellApi::toJson),
)

private fun Paragraf_22_13ModellApi.toJson() = Paragraf_22_13(
    vilkårsvurderingsid = vilkårsvurderingsid,
    vurdertAv = vurdertAv,
    kvalitetssikretAv = kvalitetssikretAv,
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    utfall = utfall.name,
    vurdertMaskinelt = vurdertMaskinelt,
    løsning_22_13_manuell = løsning_22_13_manuell.map(LøsningParagraf_22_13ModellApi::toJson),
    kvalitetssikringer_22_13 = kvalitetssikringer_22_13.map(KvalitetssikringParagraf_22_13ModellApi::toJson),
    søknadsdata = søknadsdata.map(Paragraf_22_13ModellApi.SøknadsdataModellApi::toJson),
)

private fun Paragraf_22_13ModellApi.SøknadsdataModellApi.toJson() = SøknadsdataParagraf_22_13(
    søknadId = søknadId,
    søknadstidspunkt = søknadstidspunkt,
)

private fun InnstillingParagraf_11_6ModellApi.toJson() = InnstillingParagraf_11_6(
    innstillingId = innstillingId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun LøsningMaskinellMedlemskapYrkesskadeModellApi.toJson() = LøsningMaskinellMedlemskapYrkesskade(
    løsningId = løsningId,
    erMedlem = erMedlem
)

private fun LøsningManuellMedlemskapYrkesskadeModellApi.toJson() = LøsningManuellMedlemskapYrkesskade(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun SykepengedagerModellApi.toJson() = LøsningMaskinellParagraf_8_48(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    sykepengedager = sykepengedager?.let { sykepengedager ->
        LøsningMaskinellParagraf_8_48.Sykepengedager(
            gjenståendeSykedager = sykepengedager.gjenståendeSykedager,
            foreløpigBeregnetSluttPåSykepenger = sykepengedager.foreløpigBeregnetSluttPåSykepenger,
            kilde = sykepengedager.kilde,
        )
    }
)

private fun LøsningMaskinellParagraf_11_2ModellApi.toJson() = LøsningMaskinellParagraf_11_2(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_2ModellApi.toJson() = LøsningManuellParagraf_11_2(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erMedlem = erMedlem
)

private fun LøsningParagraf_11_3ModellApi.toJson() = LøsningParagraf_11_3(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_4AndreOgTredjeLeddModellApi.toJson() = LøsningParagraf_11_4AndreOgTredjeLedd(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_11_5ModellApi.toJson() = LøsningParagraf_11_5(
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

private fun LøsningParagraf_11_5YrkesskadeModellApi.toJson() = LøsningParagraf_11_5_yrkesskade(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
    arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent,
)

private fun LøsningParagraf_11_6ModellApi.toJson() = LøsningParagraf_11_6(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun LøsningParagraf_11_19ModellApi.toJson() = LøsningParagraf_11_19(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    beregningsdato = beregningsdato
)

private fun LøsningParagraf_11_22ModellApi.toJson() = LøsningParagraf_11_22(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
    andelNedsattArbeidsevne = andelNedsattArbeidsevne,
    år = år,
    antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt,
)

private fun LøsningParagraf_11_27_FørsteLedd_ModellApi.toJson() = LøsningMaskinellParagraf_11_27FørsteLedd(
    løsningId = løsningId,
    tidspunktForVurdering = tidspunktForVurdering,
    svangerskapspenger = LøsningMaskinellParagraf_11_27FørsteLedd.Svangerskapspenger(
        fom = svangerskapspenger.fom,
        tom = svangerskapspenger.tom,
        grad = svangerskapspenger.grad,
        vedtaksdato = svangerskapspenger.vedtaksdato,
    )
)

private fun LøsningParagraf_11_29ModellApi.toJson() = LøsningParagraf_11_29(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt
)

private fun LøsningParagraf_22_13ModellApi.toJson() = LøsningParagraf_22_13(
    løsningId = løsningId,
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun KvalitetssikringMedlemskapYrkesskadeModellApi.toJson() = KvalitetssikringMedlemskapYrkesskade(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_2ModellApi.toJson() = KvalitetssikringParagraf_11_2(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_3ModellApi.toJson() = KvalitetssikringParagraf_11_3(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi.toJson() =
    KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        kvalitetssikringId = kvalitetssikringId,
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

private fun KvalitetssikringParagraf_11_5ModellApi.toJson() = KvalitetssikringParagraf_11_5(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_5YrkesskadeModellApi.toJson() = KvalitetssikringParagraf_11_5Yrkesskade(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_6ModellApi.toJson() = KvalitetssikringParagraf_11_6(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_22_13ModellApi.toJson() = KvalitetssikringParagraf_22_13(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_19ModellApi.toJson() = KvalitetssikringParagraf_11_19(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_22ModellApi.toJson() = KvalitetssikringParagraf_11_22(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun KvalitetssikringParagraf_11_29ModellApi.toJson() = KvalitetssikringParagraf_11_29(
    kvalitetssikringId = kvalitetssikringId,
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)


private fun VedtakModellApi.toJson() = Vedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    inntektsgrunnlag = inntektsgrunnlag.toJson(),
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
)

private fun InntektsgrunnlagModellApi.toJson() = Inntektsgrunnlag(
    beregningsdato = beregningsdato,
    inntekterSiste3Kalenderår = inntekterSiste3Kalenderår.map(InntekterForBeregningModellApi::toJson),
    yrkesskade = yrkesskade?.toJson(),
    fødselsdato = fødselsdato,
    sisteKalenderår = sisteKalenderår,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun InntekterForBeregningModellApi.toJson() = InntekterForBeregning(
    inntekter = inntekter.map(InntektModellApi::toJson),
    inntektsgrunnlagForÅr = inntektsgrunnlagForÅr.toJson(),
)

private fun InntektModellApi.toJson() = Inntekt(
    arbeidsgiver = arbeidsgiver,
    inntekstmåned = inntekstmåned,
    beløp = beløp,
)

private fun InntektsgrunnlagForÅrModellApi.toJson() = InntektsgrunnlagForÅr(
    år = år,
    beløpFørJustering = beløpFørJustering,
    beløpJustertFor6G = beløpJustertFor6G,
    erBeløpJustertFor6G = erBeløpJustertFor6G,
    grunnlagsfaktor = grunnlagsfaktor,
)

private fun YrkesskadeModellApi.toJson() = Yrkesskade(
    gradAvNedsattArbeidsevneKnyttetTilYrkesskade = gradAvNedsattArbeidsevneKnyttetTilYrkesskade,
    inntektsgrunnlag = inntektsgrunnlag.toJson(),
)
