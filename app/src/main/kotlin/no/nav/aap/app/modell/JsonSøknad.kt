package no.nav.aap.app.modell

import java.time.LocalDate

data class JsonSøknad(
    val ident: JsonPersonident,
    val fødselsdato: LocalDate,
)

data class JsonPersonident(
    val type: String,
    val verdi: String,
)
