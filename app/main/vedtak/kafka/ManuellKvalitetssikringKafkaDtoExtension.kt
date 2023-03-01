package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.*
import no.nav.aap.modellapi.*

internal fun Kvalitetssikring_11_2KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_3KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_4_ledd2_ledd3KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_5KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_6KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_22_13KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_19KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_29KafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun Kvalitetssikring_11_2KafkaDto.toModellApi() = KvalitetssikringParagraf_11_2ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_3KafkaDto.toModellApi() = KvalitetssikringParagraf_11_3ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_4_ledd2_ledd3KafkaDto.toModellApi() =
    KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

private fun Kvalitetssikring_11_5KafkaDto.toModellApi() = KvalitetssikringParagraf_11_5ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    kravOmNedsattArbeidsevneErGodkjent = kravOmNedsattArbeidsevneErGodkjent,
    kravOmNedsattArbeidsevneErGodkjentBegrunnelse = kravOmNedsattArbeidsevneErGodkjentBegrunnelse,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjent = nedsettelseSkyldesSykdomEllerSkadeErGodkjent,
    nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse = nedsettelseSkyldesSykdomEllerSkadeErGodkjentBegrunnelse,
)

private fun Kvalitetssikring_11_6KafkaDto.toModellApi() = KvalitetssikringParagraf_11_6ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_19KafkaDto.toModellApi() = KvalitetssikringParagraf_11_19ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_29KafkaDto.toModellApi() = KvalitetssikringParagraf_11_29ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_22_13KafkaDto.toModellApi() = KvalitetssikringParagraf_22_13ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)
