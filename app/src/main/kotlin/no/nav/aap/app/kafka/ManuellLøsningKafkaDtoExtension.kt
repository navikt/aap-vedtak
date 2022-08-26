package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.*
import no.nav.aap.dto.kafka.*

internal fun Løsning_11_2_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_3_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_4_ledd2_ledd3_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_5_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_6_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_12_ledd1_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_19_manuell.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Løsning_11_29_manuell.håndter(søker: Søker) = toDto().håndter(søker)

private fun Løsning_11_2_manuell.toDto() = DtoLøsningParagraf_11_2(vurdertAv, tidspunktForVurdering, erMedlem)
private fun Løsning_11_3_manuell.toDto() = DtoLøsningParagraf_11_3(vurdertAv, tidspunktForVurdering, erOppfylt)

private fun Løsning_11_4_ledd2_ledd3_manuell.toDto() = DtoLøsningParagraf_11_4AndreOgTredjeLedd(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
)

private fun Løsning_11_5_manuell.toDto() = DtoLøsningParagraf_11_5(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade
)

private fun Løsning_11_6_manuell.toDto() = DtoLøsningParagraf_11_6(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun Løsning_11_12_ledd1_manuell.toDto() = DtoLøsningParagraf_11_12FørsteLedd(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun Løsning_11_19_manuell.toDto() = DtoLøsningParagraf_11_19(vurdertAv, tidspunktForVurdering, beregningsdato)
private fun Løsning_11_29_manuell.toDto() = DtoLøsningParagraf_11_29(vurdertAv, tidspunktForVurdering, erOppfylt)

