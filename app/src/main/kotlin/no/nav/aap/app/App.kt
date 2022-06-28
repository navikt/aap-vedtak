package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.migrateStateStore
import no.nav.aap.app.modell.SøkereKafkaDto
import no.nav.aap.app.route.actuator
import no.nav.aap.app.route.devTools
import no.nav.aap.app.stream.inntekterStream
import no.nav.aap.app.stream.manuell.manuellStream
import no.nav.aap.app.stream.medlemStream
import no.nav.aap.app.stream.søknadStream
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.streams.KStreams
import no.nav.aap.kafka.streams.KafkaStreams
import no.nav.aap.kafka.streams.consume
import no.nav.aap.kafka.streams.produce
import no.nav.aap.kafka.streams.store.scheduleMetrics
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import kotlin.time.Duration.Companion.minutes

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(val kafka: KafkaConfig)

internal fun Application.server(kafka: KStreams = KafkaStreams) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }
    install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    val søknadProducer = kafka.createProducer(config.kafka, Topics.søknad)
    val søkerProducer = kafka.createProducer(config.kafka, Topics.søkere)

    kafka.connect(
        config = config.kafka,
        registry = prometheus,
        topology = topology(prometheus, søkerProducer),
    )

    routing {
        devTools(søknadProducer, søkerProducer)
        actuator(prometheus, kafka)
    }
}

internal fun topology(registry: MeterRegistry, migrationProducer: Producer<String, SøkereKafkaDto>): Topology {
    val streams = StreamsBuilder()
    val søkerKTable = streams
        .consume(Topics.søkere)
        .produce(Tables.søkere)

    søkerKTable.scheduleMetrics(Tables.søkere, 2.minutes, registry)
    søkerKTable.migrateStateStore(Tables.søkere, migrationProducer)

    streams.søknadStream(søkerKTable)
    streams.medlemStream(søkerKTable)
    streams.inntekterStream(søkerKTable)
    streams.manuellStream(søkerKTable)

    return streams.build()
}
