package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.kafka.*
import no.nav.aap.modellapi.*

internal fun Løsning_11_2_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_11_3_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_11_4_ledd2_ledd3_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_11_5_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_11_6_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_22_13_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_11_19_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)
internal fun Løsning_11_29_manuell.håndter(søker: Søker) = toModellApi().håndter(søker)

private fun Løsning_11_2_manuell.toModellApi() = LøsningParagraf_11_2ModellApi(vurdertAv, tidspunktForVurdering, erMedlem)
private fun Løsning_11_3_manuell.toModellApi() = LøsningParagraf_11_3ModellApi(vurdertAv, tidspunktForVurdering, erOppfylt)

private fun Løsning_11_4_ledd2_ledd3_manuell.toModellApi() = LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
)

private fun Løsning_11_5_manuell.toModellApi() = LøsningParagraf_11_5ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    kravOmNedsattArbeidsevneErOppfylt = kravOmNedsattArbeidsevneErOppfylt,
    nedsettelseSkyldesSykdomEllerSkade = nedsettelseSkyldesSykdomEllerSkade
)

private fun Løsning_11_6_manuell.toModellApi() = LøsningParagraf_11_6ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid
)

private fun Løsning_22_13_manuell.toModellApi() = LøsningParagraf_22_13ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato
)

private fun Løsning_11_19_manuell.toModellApi() = LøsningParagraf_11_19ModellApi(vurdertAv, tidspunktForVurdering, beregningsdato)
private fun Løsning_11_29_manuell.toModellApi() = LøsningParagraf_11_29ModellApi(vurdertAv, tidspunktForVurdering, erOppfylt)

