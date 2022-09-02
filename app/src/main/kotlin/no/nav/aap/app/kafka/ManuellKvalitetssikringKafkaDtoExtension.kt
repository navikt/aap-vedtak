package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.*
import no.nav.aap.dto.kafka.*
import no.nav.aap.modellapi.*

internal fun Kvalitetssikring_11_2.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_3.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_4_ledd2_ledd3.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_5.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_6.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_12_ledd1.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_19.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_29.håndter(søker: Søker) = toDto().håndter(søker)

private fun Kvalitetssikring_11_2.toDto() = KvalitetssikringParagraf_11_2ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_3.toDto() = KvalitetssikringParagraf_11_3ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_4_ledd2_ledd3.toDto() = KvalitetssikringParagraf_11_4AndreOgTredjeLeddModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_5.toDto() = KvalitetssikringParagraf_11_5ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_6.toDto() = KvalitetssikringParagraf_11_6ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_12_ledd1.toDto() = KvalitetssikringParagraf_11_12FørsteLeddModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_19.toDto() = KvalitetssikringParagraf_11_19ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_29.toDto() = KvalitetssikringParagraf_11_29ModellApi(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)
