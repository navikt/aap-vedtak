package no.nav.aap.app

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.aap.app.config.Config
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.KafkaStreamsFactory
import no.nav.aap.app.kafka.createJsonProducer
import no.nav.aap.app.kafka.createProducer
import no.nav.aap.app.modell.JsonPersonident
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.avro.medlem.v1.ErMedlem
import no.nav.aap.avro.medlem.v1.Response
import org.apache.kafka.clients.producer.ProducerRecord
import java.lang.Thread.sleep
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextLong
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem

fun main() {
    embeddedServer(Netty, port = 8081) {
        val config = loadConfig<Config>()
        val kafka = KafkaStreamsFactory()
        val søknadProducer = kafka.createJsonProducer<JsonSøknad>(config.kafka)
        val medlemProducer = kafka.createProducer<AvroMedlem>(config.kafka)
        thread {
            while (true) {
                val ident = randomIdent
                val søknad = JsonSøknad(JsonPersonident("FNR", ident), randomDate)
                søknadProducer.send(ProducerRecord("aap.aap-soknad-sendt.v1", ident, søknad)).also {
                    log.info("Produced ${it.get()}")
                }

                sleep(5_000L)

                val medlem = AvroMedlem.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setPersonident(ident)
                    .setResponseBuilder(Response.newBuilder().setErMedlem(ErMedlem.JA))
                    .build()

                medlemProducer.send(ProducerRecord("aap.medlem.v1", ident, medlem)).also {
                    log.info("Produced ${it.get()}")
                }

                sleep(5_000L)

            }
        }
    }.start(true)
}

private val randomIdent: String get() = Random.nextLong(10_000_000_000 until 99_999_999_999).toString()
private val randomDate: LocalDate get() = LocalDate.now().minusYears(Random.nextLong(0..100L))
