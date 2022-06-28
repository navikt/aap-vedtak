package no.nav.aap.app.modell

import no.nav.aap.domene.Søker
import no.nav.aap.dto.*
import java.time.LocalDate
import java.time.LocalDateTime

data class Løsning_11_2_manuell(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime, val erMedlem: String) {
    private fun toDto() = DtoLøsningParagraf_11_2(vurdertAv, tidspunktForVurdering, erMedlem)
    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Løsning_11_3_manuell(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime,  val erOppfylt: Boolean) {
    private fun toDto() = DtoLøsningParagraf_11_3(vurdertAv, tidspunktForVurdering, erOppfylt)
    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Løsning_11_4_ledd2_ledd3_manuell(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime,  val erOppfylt: Boolean) {
    private fun toDto() = DtoLøsningParagraf_11_4_ledd2_ledd3(vurdertAv, tidspunktForVurdering, erOppfylt)
    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Løsning_11_5_manuell(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val kravOmNedsattArbeidsevneErOppfylt: Boolean,
    val nedsettelseSkyldesSykdomEllerSkade: Boolean
) {
    private fun toDto() = DtoLøsningParagraf_11_5(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
        nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Løsning_11_6_manuell(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val harBehovForBehandling: Boolean,
    val harBehovForTiltak: Boolean,
    val harMulighetForÅKommeIArbeid: Boolean
) {
    private fun toDto() = DtoLøsningParagraf_11_6(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        harBehovForBehandling = harBehovForBehandling,
        harBehovForTiltak = harBehovForTiltak,
        harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Løsning_11_12_ledd1_manuell(
    val vurdertAv: String,
    val tidspunktForVurdering: LocalDateTime,
    val bestemmesAv: String,
    val unntak: String,
    val unntaksbegrunnelse: String,
    val manueltSattVirkningsdato: LocalDate
) {
    private fun toDto() = DtoLøsningParagraf_11_12_ledd1(
        vurdertAv = vurdertAv,
        tidspunktForVurdering = tidspunktForVurdering,
        bestemmesAv = bestemmesAv,
        unntak = unntak,
        unntaksbegrunnelse = unntaksbegrunnelse,
        manueltSattVirkningsdato = manueltSattVirkningsdato
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Løsning_11_29_manuell(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime,  val erOppfylt: Boolean) {
    private fun toDto() = DtoLøsningParagraf_11_29(vurdertAv, tidspunktForVurdering, erOppfylt)
    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class LøsningVurderingAvBeregningsdato(val vurdertAv: String, val tidspunktForVurdering: LocalDateTime,  val beregningsdato: LocalDate) {
    private fun toDto() = DtoLøsningParagraf_11_19(vurdertAv, tidspunktForVurdering, beregningsdato)
    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}
