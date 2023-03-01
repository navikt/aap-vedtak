package no.nav.aap.modellapi

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class SakModellApi(
    val saksid: UUID,
    val tilstand: String,
    val sakstyper: List<SakstypeModellApi>,
    val vurderingsdato: LocalDate,
    val søknadstidspunkt: LocalDateTime,
    val vedtak: VedtakModellApi?
)