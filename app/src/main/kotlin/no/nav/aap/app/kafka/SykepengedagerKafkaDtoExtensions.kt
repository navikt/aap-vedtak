package no.nav.aap.app.kafka

import no.nav.aap.domene.Søker
import no.nav.aap.modellapi.SykepengedagerModellApi
import no.nav.aap.dto.kafka.SykepengedagerKafkaDto

internal fun SykepengedagerKafkaDto.Response.håndter(søker: Søker) = toModellApi().håndter(søker)

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
