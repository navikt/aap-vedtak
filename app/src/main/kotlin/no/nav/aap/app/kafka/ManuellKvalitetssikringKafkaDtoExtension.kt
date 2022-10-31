package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.*
import no.nav.aap.modellapi.*

internal fun Kvalitetssikring_11_2.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_3.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_4_ledd2_ledd3.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_5.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_6.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_22_13.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_19.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)
internal fun Kvalitetssikring_11_29.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun Kvalitetssikring_11_2.toModellApi() = KvalitetssikringParagraf_11_2ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_3.toModellApi() = KvalitetssikringParagraf_11_3ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_4_ledd2_ledd3.toModellApi() = KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_5.toModellApi() = KvalitetssikringParagraf_11_5ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_6.toModellApi() = KvalitetssikringParagraf_11_6ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_19.toModellApi() = KvalitetssikringParagraf_11_19ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_29.toModellApi() = KvalitetssikringParagraf_11_29ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_22_13.toModellApi() = KvalitetssikringParagraf_22_13ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)
