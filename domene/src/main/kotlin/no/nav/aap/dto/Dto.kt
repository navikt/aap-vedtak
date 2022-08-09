package no.nav.aap.dto

import no.nav.aap.domene.Søker
import no.nav.aap.domene.beregning.Arbeidsgiver
import no.nav.aap.domene.beregning.Beløp.Companion.beløp
import no.nav.aap.domene.beregning.Inntekt
import no.nav.aap.hendelse.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.YearMonth
import java.util.*

enum class Utfall {
    OPPFYLT, IKKE_OPPFYLT, IKKE_VURDERT, IKKE_RELEVANT
}

data class DtoSøker(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<DtoSak>
)

data class DtoSak(
    val saksid: UUID,
    val tilstand: String,
    val sakstyper: List<DtoSakstype>,
    val vurderingsdato: LocalDate,
    val søknadstidspunkt: LocalDateTime,
    val vedtak: DtoVedtak?
)

data class DtoSakstype(
    val type: String,
    val aktiv: Boolean,
    val vilkårsvurderinger: List<DtoVilkårsvurdering>
)

data class DtoVilkårsvurdering(
    val vilkårsvurderingsid: UUID,
    val vurdertAv: String?,
    val kvalitetssikretAv: String?,
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val utfall: Utfall,
    val løsning_medlemskap_yrkesskade_maskinell: List<DtoLøsningMaskinellMedlemskapYrkesskade>? = null,
    val løsning_medlemskap_yrkesskade_manuell: List<DtoLøsningManuellMedlemskapYrkesskade>? = null,
    val løsning_11_2_maskinell: List<DtoLøsningMaskinellParagraf_11_2>? = null,
    val løsning_11_2_manuell: List<DtoLøsningParagraf_11_2>? = null,
    val løsning_11_3_manuell: List<DtoLøsningParagraf_11_3>? = null,
    val løsning_11_4_ledd2_ledd3_manuell: List<DtoLøsningParagraf_11_4AndreOgTredjeLedd>? = null,
    val løsning_11_5_manuell: List<DtoLøsningParagraf_11_5>? = null,
    val løsning_11_5_yrkesskade_manuell: List<DtoLøsningParagraf_11_5Yrkesskade>? = null,
    val løsning_11_6_manuell: List<DtoLøsningParagraf_11_6>? = null,
    val løsning_11_12_ledd1_manuell: List<DtoLøsningParagraf_11_12FørsteLedd>? = null,
    val løsning_11_19_manuell: List<DtoLøsningParagraf_11_19>? = null,
    val løsning_11_22_manuell: List<DtoLøsningParagraf_11_22>? = null,
    val løsning_11_29_manuell: List<DtoLøsningParagraf_11_29>? = null,
    val kvalitetssikringer_medlemskap_yrkesskade: List<DtoKvalitetssikringMedlemskapYrkesskade>? = null,
    val kvalitetssikringer_11_2: List<DtoKvalitetssikringParagraf_11_2>? = null,
    val kvalitetssikringer_11_3: List<DtoKvalitetssikringParagraf_11_3>? = null,
    val kvalitetssikringer_11_4_ledd2_ledd3: List<DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd>? = null,
    val kvalitetssikringer_11_5: List<DtoKvalitetssikringParagraf_11_5>? = null,
    val kvalitetssikringer_11_5_yrkesskade: List<DtoKvalitetssikringParagraf_11_5Yrkesskade>? = null,
    val kvalitetssikringer_11_6: List<DtoKvalitetssikringParagraf_11_6>? = null,
    val kvalitetssikringer_11_12_ledd1: List<DtoKvalitetssikringParagraf_11_12FørsteLedd>? = null,
    val kvalitetssikringer_11_19: List<DtoKvalitetssikringParagraf_11_19>? = null,
    val kvalitetssikringer_11_22: List<DtoKvalitetssikringParagraf_11_22>? = null,
    val kvalitetssikringer_11_29: List<DtoKvalitetssikringParagraf_11_29>? = null,
)

data class DtoLøsningMaskinellMedlemskapYrkesskade(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningMaskinellMedlemskapYrkesskade(enumValueOf(erMedlem.uppercase()))
}

data class DtoLøsningManuellMedlemskapYrkesskade(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() =
        LøsningManuellMedlemskapYrkesskade(vurdertAv, tidspunktForVurdering, enumValueOf(erMedlem.uppercase()))
}

data class DtoKvalitetssikringMedlemskapYrkesskade(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringMedlemskapYrkesskade(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_2(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erMedlem: String
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningManuellParagraf_11_2(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erMedlem = if (erMedlem.lowercase() in listOf("true", "ja"))
            LøsningManuellParagraf_11_2.ErMedlem.JA else LøsningManuellParagraf_11_2.ErMedlem.NEI
    )
}

data class DtoKvalitetssikringParagraf_11_2(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_2(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningMaskinellParagraf_11_2(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningMaskinellParagraf_11_2(enumValueOf(erMedlem.uppercase()))
}

data class DtoLøsningParagraf_11_3(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_3(vurdertAv, tidspunktForVurdering, erOppfylt)
}

data class DtoKvalitetssikringParagraf_11_3(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_3(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_4AndreOgTredjeLedd(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_4AndreOgTredjeLedd(vurdertAv, tidspunktForVurdering, erOppfylt)
}

data class DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_5(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val kravOmNedsattArbeidsevneErOppfylt: Boolean,
    val nedsettelseSkyldesSykdomEllerSkade: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_5(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    )
}

data class DtoKvalitetssikringParagraf_11_5(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_5(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_5Yrkesskade(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_5Yrkesskade(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}

data class DtoKvalitetssikringParagraf_11_5Yrkesskade(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_5Yrkesskade(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_6(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val harBehovForBehandling: Boolean,
    val harBehovForTiltak: Boolean,
    val harMulighetForÅKommeIArbeid: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_6(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )
}

data class DtoKvalitetssikringParagraf_11_6(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_6(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_12FørsteLedd(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val bestemmesAv: String,
    val unntak: String,
    val unntaksbegrunnelse: String,
    val manueltSattVirkningsdato: LocalDate
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_12FørsteLedd(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = when {
            bestemmesAv == "soknadstidspunkt" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.soknadstidspunkt
            bestemmesAv == "maksdatoSykepenger" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.maksdatoSykepenger
            bestemmesAv == "ermiraSays" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.ermiraSays
            bestemmesAv == "unntaksvurdering" && unntak == "forhindret" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.unntaksvurderingForhindret
            bestemmesAv == "unntaksvurdering" && unntak == "mangelfull" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.unntaksvurderingMangelfull
            bestemmesAv == "etterSisteLoenn" -> LøsningParagraf_11_12FørsteLedd.BestemmesAv.etterSisteLoenn
            else -> error("Ukjent bestemmesAv: $bestemmesAv og unntak: $unntak")
        },
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

data class DtoKvalitetssikringParagraf_11_12FørsteLedd(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_12FørsteLedd(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_22(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean,
    val andelNedsattArbeidsevne: Int,
    val år: Year,
    val antattÅrligArbeidsinntekt: Double
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_22(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.beløp
    )
}

data class DtoKvalitetssikringParagraf_11_22(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_22(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoLøsningParagraf_11_29(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val erOppfylt: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_29(vurdertAv, tidspunktForVurdering, erOppfylt)
}

data class DtoKvalitetssikringParagraf_11_29(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_29(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoVurderingAvBeregningsdato(
    val tilstand: String,
    val løsningVurderingAvBeregningsdato: List<DtoLøsningParagraf_11_19>?
)

data class DtoLøsningParagraf_11_19(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val beregningsdato: LocalDate
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_19(vurdertAv, tidspunktForVurdering, beregningsdato)
}

data class DtoKvalitetssikringParagraf_11_19(
    val kvalitetssikretAv: String,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    fun håndter(søker: Søker) {
        søker.håndterKvalitetssikring(toKvalitetssikring())
    }

    private fun toKvalitetssikring() = KvalitetssikringParagraf_11_19(
        kvalitetssikretAv = kvalitetssikretAv,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )
}

data class DtoVedtak(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val inntektsgrunnlag: DtoInntektsgrunnlag,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate
)

data class DtoInntektsgrunnlag(
    val beregningsdato: LocalDate,
    val inntekterSiste3Kalenderår: List<DtoInntekterForBeregning>,
    val yrkesskade: DtoYrkesskade?,
    val fødselsdato: LocalDate,
    val sisteKalenderår: Year,
    val grunnlagsfaktor: Double
)

data class DtoInntekterForBeregning(
    val inntekter: List<DtoInntekt>,
    val inntektsgrunnlagForÅr: DtoInntektsgrunnlagForÅr
)

data class DtoInntektsgrunnlagForÅr(
    val år: Year,
    val beløpFørJustering: Double,
    val beløpJustertFor6G: Double,
    val erBeløpJustertFor6G: Boolean,
    val grunnlagsfaktor: Double
)

data class DtoYrkesskade(
    val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
    val inntektsgrunnlag: DtoInntektsgrunnlagForÅr
)

data class DtoInntekt(
    val arbeidsgiver: String,
    val inntekstmåned: YearMonth,
    val beløp: Double
)

data class DtoInntekter(
    val inntekter: List<DtoInntekt>
) {
    fun håndter(søker: Søker) {
        søker.håndterLøsning(LøsningInntekter(inntekter.map {
            Inntekt(
                arbeidsgiver = Arbeidsgiver(it.arbeidsgiver),
                inntekstmåned = it.inntekstmåned,
                beløp = it.beløp.beløp
            )
        }))
    }
}
