package no.nav.aap.app.kafka

import no.nav.aap.modellapi.LøsningMaskinellParagraf_11_2ModellApi
import no.nav.aap.dto.kafka.MedlemKafkaDto

internal fun MedlemKafkaDto.toModellApi(): LøsningMaskinellParagraf_11_2ModellApi = LøsningMaskinellParagraf_11_2ModellApi(
    erMedlem = response?.erMedlem?.name ?: error("response fra medlemsskap mangler.")
)
