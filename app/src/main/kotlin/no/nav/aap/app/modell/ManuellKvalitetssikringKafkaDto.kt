package no.nav.aap.app.modell

import no.nav.aap.domene.Søker
import no.nav.aap.dto.*
import java.time.LocalDateTime
import java.util.*

data class Kvalitetssikring_11_2(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_2(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_3(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_3(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_4_ledd2_ledd3(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_4AndreOgTredjeLedd(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_5(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_5(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_6(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_6(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_12_ledd1(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_12FørsteLedd(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_19(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_19(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}

data class Kvalitetssikring_11_29(
    val kvalitetssikretAv: String,
    val tidspunktForKvalitetssikring: LocalDateTime,
    val erGodkjent: Boolean,
    val begrunnelse: String
) {
    private fun toDto() = DtoKvalitetssikringParagraf_11_29(
        kvalitetssikretAv = kvalitetssikretAv,
        tidspunktForKvalitetssikring = tidspunktForKvalitetssikring,
        erGodkjent = erGodkjent,
        begrunnelse = begrunnelse
    )

    internal fun håndter(søker: Søker) = toDto().håndter(søker)
}
