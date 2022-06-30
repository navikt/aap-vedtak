package no.nav.aap.app.route

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.kafka.streams.Topic
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.YearMonth

private val secureLog: Logger = LoggerFactory.getLogger("secureLog")

internal fun Routing.devTools(
    søknadProducer: Producer<String, JsonSøknad>,
    søkerProducer: Producer<String, SøkereKafkaDto>,
    inntekterProducer: Producer<String, InntekterKafkaDto>
) {
    get("/delete/{personident}") {
        val personident = call.parameters.getOrFail("personident")

        søknadProducer.tombstone(Topics.søknad, personident)
        søkerProducer.tombstone(Topics.søkere, personident)

        call.respondText("Søknad og søker med ident $personident slettes!")
    }

    get("/søknad/{personident}") {
        val personident = call.parameters.getOrFail("personident")
        val søknad = JsonSøknad()

        søknadProducer.produce(Topics.søknad, personident, søknad)

        call.respondText("Søknad $søknad mottatt!")
    }

    get("/inntekter/{personident}") {
        val personident = call.parameters.getOrFail("personident")
        val inntekter = InntekterKafkaDto(
            personident = personident,
            request = InntekterKafkaDto.Request(
                fom = YearMonth.of(2020, 1),
                tom = YearMonth.of(2021, 1)
            ),
            response = null
        )

        inntekterProducer.produce(Topics.inntekter, personident, inntekter)

        call.respondText("Sender inntektsbehov")
    }
}

private fun <V> Producer<String, V>.produce(topic: Topic<V>, key: String, value: V) {
    send(ProducerRecord(topic.name, key, value)) { meta, error ->
        if (error != null) {
            secureLog.error("Produserer til Topic feilet", error)
        } else {
            secureLog.trace(
                "Produserer til Topic",
                kv("key", key),
                kv("topic", topic.name),
                kv("partition", meta.partition()),
                kv("offset", meta.offset()),
            )
        }
    }
}

private fun <V> Producer<String, V>.tombstone(topic: Topic<V>, key: String) {
    send(ProducerRecord(topic.name, key, null)) { meta, error ->
        if (error != null) {
            secureLog.error("Tombstoner Topic feilet", error)
        } else {
            secureLog.trace(
                "Tombstoner Topic",
                kv("key", key),
                kv("topic", topic.name),
                kv("partition", meta.partition()),
                kv("offset", meta.offset()),
            )
        }
    }
}
