package no.nav.aap.app

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.KStreams
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.modell.JsonPersonident
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.avro.medlem.v1.ErMedlem
import no.nav.aap.avro.medlem.v1.Response
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import kotlin.random.Random
import kotlin.random.nextLong
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem

fun main() {
    embeddedServer(Netty, port = 8081) {
        val config = loadConfig<Config>()
        val kafka = KStreams(config.kafka)
        val topics = Topics(config.kafka)
        val søknadProducer = kafka.createProducer(topics.søknad)
        val medlemProducer = kafka.createProducer(topics.medlem)

        launch {
            while (isActive) {
                val ident = randomIdent

                søknadProducer.produce(topics.søknad.name, ident) {
                    JsonSøknad(JsonPersonident("FNR", ident), randomDate)
                }
                delay(5_000L)

                medlemProducer.produce(topics.medlem.name, ident) {
                    AvroMedlem.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setPersonident(ident)
                        .setResponseBuilder(Response.newBuilder().setErMedlem(ErMedlem.JA))
                        .build()
                }
                delay(5_000L)
            }
        }
    }.start(true)
}

private val randomIdent: String get() = Random.nextLong(10_000_000_000 until 99_999_999_999).toString()
private val randomDate: LocalDate get() = LocalDate.now().minusYears(Random.nextLong(0..100L))

inline fun <reified V : Any> Producer<String, V>.produce(topic: String, key: String, value: () -> V) {
    val record = ProducerRecord(topic, key, value())
    send(record).get().also {
        LoggerFactory.getLogger("app").info("produced: ${it.topic()} $record")
    }
}