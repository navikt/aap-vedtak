package vedtak.kafka

import no.nav.aap.dto.kafka.*
import no.nav.aap.modellapi.*

internal fun Løsning_11_2_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_11_3_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_11_4_ledd2_ledd3_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_11_5_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_11_6_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_22_13_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_11_19_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Løsning_11_29_manuellKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun Løsning_11_2_manuellKafkaDto.toModellApi() = LøsningParagraf_11_2ModellApi(vurdertAv, tidspunktForVurdering, erMedlem)
private fun Løsning_11_3_manuellKafkaDto.toModellApi() = LøsningParagraf_11_3ModellApi(vurdertAv, tidspunktForVurdering, erOppfylt)

private fun Løsning_11_4_ledd2_ledd3_manuellKafkaDto.toModellApi() = LøsningParagraf_11_4AndreOgTredjeLeddModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    erOppfylt = erOppfylt,
)

private fun Løsning_11_5_manuellKafkaDto.toModellApi() = LøsningParagraf_11_5ModellApi(
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

private fun Løsning_11_6_manuellKafkaDto.toModellApi() = LøsningParagraf_11_6ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    harBehovForBehandling = harBehovForBehandling,
    harBehovForTiltak = harBehovForTiltak,
    harMulighetForÅKommeIArbeid = harMulighetForÅKommeIArbeid,
    individuellBegrunnelse = individuellBegrunnelse,
)

private fun Løsning_22_13_manuellKafkaDto.toModellApi() = LøsningParagraf_22_13ModellApi(
    vurdertAv = vurdertAv,
    tidspunktForVurdering = tidspunktForVurdering,
    bestemmesAv = bestemmesAv,
    unntak = unntak,
    unntaksbegrunnelse = unntaksbegrunnelse,
    manueltSattVirkningsdato = manueltSattVirkningsdato,
    begrunnelseForAnnet = begrunnelseForAnnet,
)

private fun Løsning_11_19_manuellKafkaDto.toModellApi() = LøsningParagraf_11_19ModellApi(vurdertAv, tidspunktForVurdering, beregningsdato)
private fun Løsning_11_29_manuellKafkaDto.toModellApi() = LøsningParagraf_11_29ModellApi(vurdertAv, tidspunktForVurdering, erOppfylt)
