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
    val vurderingAvBeregningsdato: DtoVurderingAvBeregningsdato,
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
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val måVurderesManuelt: Boolean,
    val løsning_medlemskap_yrkesskade_maskinell: DtoLøsningMaskinellMedlemskapYrkesskade? = null,
    val løsning_medlemskap_yrkesskade_manuell: DtoLøsningManuellMedlemskapYrkesskade? = null,
    val løsning_11_2_maskinell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_2_manuell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_3_manuell: DtoLøsningParagraf_11_3? = null,
    val løsning_11_4_ledd2_ledd3_manuell: DtoLøsningParagraf_11_4_ledd2_ledd3? = null,
    val løsning_11_5_manuell: DtoLøsningParagraf_11_5? = null,
    val løsning_11_5_yrkesskade_manuell: DtoLøsningParagraf_11_5_yrkesskade? = null,
    val løsning_11_6_manuell: DtoLøsningParagraf_11_6? = null,
    val løsning_11_12_ledd1_manuell: DtoLøsningParagraf_11_12_ledd1? = null,
    val løsning_11_22_manuell: DtoLøsningParagraf_11_22? = null,
    val løsning_11_29_manuell: DtoLøsningParagraf_11_29? = null,
)

data class DtoManuell(
    val løsning_11_2_manuell: DtoLøsningParagraf_11_2? = null,
    val løsning_11_3_manuell: DtoLøsningParagraf_11_3? = null,
    val løsning_11_4_ledd2_ledd3_manuell: DtoLøsningParagraf_11_4_ledd2_ledd3? = null,
    val løsning_11_5_manuell: DtoLøsningParagraf_11_5? = null,
    val løsning_11_6_manuell: DtoLøsningParagraf_11_6? = null,
    val løsning_11_12_ledd1_manuell: DtoLøsningParagraf_11_12_ledd1? = null,
    val løsning_11_29_manuell: DtoLøsningParagraf_11_29? = null,
    val løsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato? = null,
)

data class DtoLøsningMaskinellMedlemskapYrkesskade(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningMaskinellMedlemskapYrkesskade(enumValueOf(erMedlem.uppercase()))
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningManuellMedlemskapYrkesskade(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningManuellMedlemskapYrkesskade(enumValueOf(erMedlem.uppercase()))
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_2(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_2(enumValueOf(erMedlem.uppercase()))
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_3(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_3(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_4_ledd2_ledd3(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_4AndreOgTredjeLedd(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_5(
    val kravOmNedsattArbeidsevneErOppfylt: Boolean,
    val nedsettelseSkyldesSykdomEllerSkade: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_5(
            LøsningParagraf_11_5.NedsattArbeidsevnegrad(
                kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
                nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
            )
        )
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_5_yrkesskade(
    val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_5_yrkesskade(
            arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
            arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
        )
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_6(
    val harBehovForBehandling: Boolean,
    val harBehovForTiltak: Boolean,
    val harMulighetForÅKommeIArbeid: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_6(
            harBehovForBehandling = harBehovForBehandling,
            harBehovForTiltak = harBehovForTiltak,
            harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
        )
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_12_ledd1(
    val bestemmesAv: String,
    val unntak: String,
    val unntaksbegrunnelse: String,
    val manueltSattVirkningsdato: LocalDate
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_12FørsteLedd(
            bestemmesAv = bestemmesAv,
            unntak = unntak,
            unntaksbegrunnelse = unntaksbegrunnelse,
            manueltSattVirkningsdato = manueltSattVirkningsdato
        )
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_22(
    val erOppfylt: Boolean,
    val andelNedsattArbeidsevne: Int,
    val år: Year,
    val antattÅrligArbeidsinntekt: Double
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_22(
            erOppfylt = erOppfylt,
            andelNedsattArbeidsevne = andelNedsattArbeidsevne,
            år = år,
            antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.beløp
        )
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoLøsningParagraf_11_29(val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningParagraf_11_29(erOppfylt)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
}

data class DtoVurderingAvBeregningsdato(
    val tilstand: String,
    val løsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato?
)

data class DtoLøsningVurderingAvBeregningsdato(
    val beregningsdato: LocalDate
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = LøsningVurderingAvBeregningsdato(beregningsdato)
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }
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
