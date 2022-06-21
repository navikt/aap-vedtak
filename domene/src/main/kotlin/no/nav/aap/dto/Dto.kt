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
    val vurdertAv: String?,
    val godkjentAv: String?,
    val paragraf: String,
    val ledd: List<String>,
    val tilstand: String,
    val utfall: Utfall,
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

data class DtoLøsningMaskinellMedlemskapYrkesskade(val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningMaskinellMedlemskapYrkesskade(enumValueOf(erMedlem.uppercase()))
}

data class DtoLøsningManuellMedlemskapYrkesskade(val vurdertAv: String, val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningManuellMedlemskapYrkesskade(vurdertAv, enumValueOf(erMedlem.uppercase()))
}

data class DtoLøsningParagraf_11_2(val vurdertAv: String, val erMedlem: String) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_2(vurdertAv, enumValueOf(erMedlem.uppercase()))
}

data class DtoLøsningParagraf_11_3(val vurdertAv: String, val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_3(vurdertAv, erOppfylt)
}

data class DtoLøsningParagraf_11_4_ledd2_ledd3(val vurdertAv: String, val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_4AndreOgTredjeLedd(vurdertAv, erOppfylt)
}

data class DtoLøsningParagraf_11_5(
    val vurdertAv: String,
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
        nedsattArbeidsevnegrad = LøsningParagraf_11_5.NedsattArbeidsevnegrad(
            kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
            nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade,
        )
    )
}

data class DtoLøsningParagraf_11_5_yrkesskade(
    val vurdertAv: String,
    val arbeidsevneErNedsattMedMinst50Prosent: Boolean,
    val arbeidsevneErNedsattMedMinst30Prosent: Boolean
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_5_yrkesskade(
        vurdertAv = vurdertAv,
        arbeidsevneErNedsattMedMinst50Prosent = arbeidsevneErNedsattMedMinst50Prosent,
        arbeidsevneErNedsattMedMinst30Prosent = arbeidsevneErNedsattMedMinst30Prosent
    )
}

data class DtoLøsningParagraf_11_6(
    val vurdertAv: String,
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
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )
}

data class DtoLøsningParagraf_11_12_ledd1(
    val vurdertAv: String,
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
        bestemmesAv = bestemmesAv,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )
}

data class DtoLøsningParagraf_11_22(
    val vurdertAv: String,
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
        erOppfylt = erOppfylt,
        andelNedsattArbeidsevne = andelNedsattArbeidsevne,
        år = år,
        antattÅrligArbeidsinntekt = antattÅrligArbeidsinntekt.beløp
    )
}

data class DtoLøsningParagraf_11_29(val vurdertAv: String, val erOppfylt: Boolean) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningParagraf_11_29(vurdertAv, erOppfylt)
}

data class DtoVurderingAvBeregningsdato(
    val tilstand: String,
    val løsningVurderingAvBeregningsdato: DtoLøsningVurderingAvBeregningsdato?
)

data class DtoLøsningVurderingAvBeregningsdato(
    val vurdertAv: String,
    val beregningsdato: LocalDate
) {
    fun håndter(søker: Søker): List<Behov> {
        val løsning = toLøsning()
        søker.håndterLøsning(løsning)
        return løsning.behov()
    }

    private fun toLøsning() = LøsningVurderingAvBeregningsdato(vurdertAv, beregningsdato)
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
