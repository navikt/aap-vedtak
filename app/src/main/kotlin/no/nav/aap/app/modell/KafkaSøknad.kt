package no.nav.aap.app.modell

import java.time.LocalDate

data class KafkaSøknad(
    val ident: KafkaPersonident,
    val fødselsdato: LocalDate,
)

data class KafkaPersonident(
    val type: String,
    val verdi: String,
)
