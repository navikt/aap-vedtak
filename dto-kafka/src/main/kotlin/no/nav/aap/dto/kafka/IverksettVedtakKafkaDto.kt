package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.util.*

data class IverksettVedtakKafkaDto(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)
