package vedtak.kafka

import no.nav.aap.dto.kafka.MedlemKafkaDto
import no.nav.aap.modellapi.LøsningMaskinellParagraf_11_2ModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun MedlemKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun MedlemKafkaDto.toModellApi(): LøsningMaskinellParagraf_11_2ModellApi =
    LøsningMaskinellParagraf_11_2ModellApi(
        erMedlem = response?.erMedlem?.name ?: error("response fra medlemsskap mangler.")
    )
