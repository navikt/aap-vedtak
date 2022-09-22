package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.SykepengedagerKafkaDto
import no.nav.aap.modellapi.SykepengedagerModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal fun SykepengedagerKafkaDto.Response.håndter(søker: SøkerModellApi) = toModellApi().håndter(søker)

private fun SykepengedagerKafkaDto.Response.toModellApi(): SykepengedagerModellApi {
    return if(sykepengedager == null) {
        SykepengedagerModellApi(sykepengedager = null)
    } else {
        SykepengedagerModellApi(
            sykepengedager = SykepengedagerModellApi.Sykepengedager(
                gjenståendeSykedager = sykepengedager!!.gjenståendeSykedager,
                foreløpigBeregnetSluttPåSykepenger = sykepengedager!!.foreløpigBeregnetSluttPåSykepenger,
                kilde = sykepengedager!!.kilde.name
            )
        )
    }
}
