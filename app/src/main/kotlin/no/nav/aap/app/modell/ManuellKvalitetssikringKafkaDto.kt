package no.nav.aap.app.modell

import no.nav.aap.domene.Søker
import no.nav.aap.dto.*
import java.time.LocalDateTime
import java.util.*

data class Kvalitetssikring_11_2(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_2(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_3(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_3(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_4_ledd2_ledd3(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_5(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_5(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_6(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_6(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_12_ledd1(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_12FørsteLedd(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_19(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_19(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_29(
    val løsningId: UUID,
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_29(
        løsningId = løsningId,
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}
