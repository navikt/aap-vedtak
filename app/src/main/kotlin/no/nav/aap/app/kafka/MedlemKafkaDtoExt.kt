package no.nav.aap.app.kafka

import no.nav.aap.dto.DtoLøsningMaskinellParagraf_11_2
import no.nav.aap.dto.kafka.MedlemKafkaDto

internal fun MedlemKafkaDto.toDto(): DtoLøsningMaskinellParagraf_11_2 = DtoLøsningMaskinellParagraf_11_2(
    erMedlem = response?.erMedlem?.name ?: error("response fra medlemsskap mangler.")
)
