package vedtak.kafka

import no.nav.aap.dto.kafka.SykepengedagerKafkaDto
import no.nav.aap.modellapi.SykepengedagerModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun SykepengedagerKafkaDto.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun SykepengedagerKafkaDto.toModellApi(): SykepengedagerModellApi {
    val response = requireNotNull(response) { "når sykepengedager sin respons er null skal vi ikke være her" }
    return if (response.sykepengedager == null) {
        SykepengedagerModellApi(sykepengedager = null)
    } else {
        SykepengedagerModellApi(
            sykepengedager = SykepengedagerModellApi.Sykepengedager(
                gjenståendeSykedager = response.sykepengedager!!.gjenståendeSykedager,
                foreløpigBeregnetSluttPåSykepenger = response.sykepengedager!!.foreløpigBeregnetSluttPåSykepenger,
                kilde = response.sykepengedager!!.kilde.name
            )
        )
    }
}
