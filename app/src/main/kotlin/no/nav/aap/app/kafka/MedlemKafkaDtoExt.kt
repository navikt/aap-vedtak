package no.nav.aap.app.kafka

import no.nav.aap.dto.LøsningMaskinellParagraf_11_2ModellApi
import no.nav.aap.dto.kafka.MedlemKafkaDto

internal fun MedlemKafkaDto.toDto(): LøsningMaskinellParagraf_11_2ModellApi = LøsningMaskinellParagraf_11_2ModellApi(
    erMedlem = response?.erMedlem?.name ?: error("response fra medlemsskap mangler.")
)
