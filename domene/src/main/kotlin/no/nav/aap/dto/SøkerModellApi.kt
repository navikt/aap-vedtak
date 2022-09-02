package no.nav.aap.dto

import java.time.LocalDate

data class SøkerModellApi(
    val personident: String,
    val fødselsdato: LocalDate,
    val saker: List<SakModellApi>
)