package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.dto.*
import no.nav.aap.dto.kafka.*

internal fun Kvalitetssikring_11_2.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_3.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_4_ledd2_ledd3.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_5.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_6.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_12_ledd1.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_19.håndter(søker: Søker) = toDto().håndter(søker)
internal fun Kvalitetssikring_11_29.håndter(søker: Søker) = toDto().håndter(søker)

private fun Kvalitetssikring_11_2.toDto() = DtoKvalitetssikringParagraf_11_2(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_3.toDto() = DtoKvalitetssikringParagraf_11_3(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_4_ledd2_ledd3.toDto() = DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_5.toDto() = DtoKvalitetssikringParagraf_11_5(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_6.toDto() = DtoKvalitetssikringParagraf_11_6(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_12_ledd1.toDto() = DtoKvalitetssikringParagraf_11_12FørsteLedd(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_19.toDto() = DtoKvalitetssikringParagraf_11_19(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)

private fun Kvalitetssikring_11_29.toDto() = DtoKvalitetssikringParagraf_11_29(
    løsningId = løsningId,
    kvalitetssikretAv = kvalitetssikretAv,
    tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
    erGodkjent = erGodkjent,
    begrunnelse = begrunnelse
)
