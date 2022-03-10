package no.nav.aap.app.stream.mock

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nav.aap.app.kafka.Kafka
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.JsonPersonident
import no.nav.aap.app.modell.JsonSøknad
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

internal fun Application.soknadProducer(kafka: Kafka, topics: Topics) {
    val producer = kafka.createProducer(topics.søknad)
    val logger = LoggerFactory.getLogger("soknadProducer")

    routing {
        get("/load/stop") {
            load = false

            log.info("stopped producing.")
            call.respondText("stopped producing.")
        }

        get("/load/start") {
            load = true

            launch(Dispatchers.Default) {
                produceLoad(topics, producer, logger)
            }

            log.info("started producing.")
            call.respondText("started producing.")
        }
    }
}

private suspend fun produceLoad(
    topics: Topics,
    producer: Producer<String, JsonSøknad>,
    logger: Logger
) {
    val limit = AtomicInteger(0)

    while (load && limit.getAndIncrement() < 1_000_000) {
        val søknad = randomSøknad()
        val record = ProducerRecord(topics.søknad.name, søknad.ident.verdi, søknad)
        producer.send(record)
        if (limit.get() % 10000 == 0) logger.info("Produced ${limit.get()} records.")
    }
}

@Volatile
private var load: Boolean = false

fun randomSøknad(): JsonSøknad {
    val ident = Random.nextLong(10_000_000_000, 99_000_000_000).toString()
    val birth = LocalDate.now().minusYears(Random.nextLong(0, 100))
    return JsonSøknad(JsonPersonident("FNR", ident), birth)
}
