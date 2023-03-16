package vedtak

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
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.Streams
import no.nav.aap.kafka.streams.v2.KafkaStreams
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.kafka.streams.v2.config.StreamsConfig
import no.nav.aap.kafka.streams.v2.processor.state.GaugeStoreEntriesStateScheduleProcessor
import no.nav.aap.kafka.streams.v2.processor.state.MigrateStateInitProcessor
import no.nav.aap.kafka.streams.v2.topology
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import vedtak.kafka.MonadeKafkaDto
import vedtak.kafka.Tables
import vedtak.kafka.Topics
import vedtak.stream.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(
    val toggle: Toggle,
    val kafka: StreamsConfig,
) {
    data class Toggle(
        val lesSøknader: Boolean,
    )
}

internal fun Application.server(kafka: Streams = KafkaStreams()) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()
    log.info("Starter med toggles: ${config.toggle}")
    val søkerProducer = kafka.createProducer(config.kafka, Topics.søkere)

    install(MicrometerMetrics) { registry = prometheus }
    install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) {
        kafka.close()
        søkerProducer.close()
    }

    kafka.connect(
        config = config.kafka,
        registry = prometheus,
        topology = topology(prometheus, søkerProducer, config.toggle.lesSøknader),
    )

    routing {
        actuator(prometheus, kafka)
    }
}

internal fun topology(
    prometheus: MeterRegistry,
    søkerProducer: Producer<String, SøkereKafkaDtoHistorikk>,
    lesSøknader: Boolean
): Topology = topology {

    val søkerKTable = consume(Tables.søkere)

    søkerKTable.schedule(
        GaugeStoreEntriesStateScheduleProcessor(
            ktable = søkerKTable,
            interval = 2.minutes,
            registry = prometheus,
        )
    )

    søkerKTable.init(
        MigrateStateInitProcessor(
            ktable = søkerKTable,
            producer = søkerProducer,
            logValue = true,
        )
    )

    consume(Topics.medlem)
        .filter { medlem -> medlem.response != null }
        .rekey { medlem -> medlem.personident }
        .mapWithMetadata(::MonadeKafkaDto)
        .produce(Topics.monade)

    consume(Topics.inntekter)
        .filter { inntekter -> inntekter.response != null }
        .mapWithMetadata(::MonadeKafkaDto)
        .produce(Topics.monade)

    consume(Topics.andreFolketrygdsytelser)
        .filter { value -> value.response != null }
        .mapWithMetadata(::MonadeKafkaDto)
        .produce(Topics.monade)

    consume(Topics.sykepengedager)
        .filter { kafkaDto -> kafkaDto.response != null }
        .mapWithMetadata(::MonadeKafkaDto)
        .produce(Topics.monade)

    consume(Topics.innstilling_11_6).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_2).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_3).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_4).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_5).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_6).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_22_13).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_19).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.manuell_11_29).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_2).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_3).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_4).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_5).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_6).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_22_13).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_19).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_29).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.endredePersonidenter).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)
    consume(Topics.iverksettelseAvVedtak).mapWithMetadata(::MonadeKafkaDto).produce(Topics.monade)

    consume(Topics.monade)
        .windowed(250.toDuration(DurationUnit.MILLISECONDS), 250.toDuration(DurationUnit.MILLISECONDS))
        .reduce { monade, nextMonade -> monade + nextMonade }
        .map { monade -> monade }

//    søknadStream(søkerKTable, lesSøknader, prometheus)
//    medlemStream(søkerKTable)
//    inntekterStream(søkerKTable)
//    andreFolketrygdytelserStream(søkerKTable)
//    iverksettelseAvVedtakStream(søkerKTable)
//    sykepengedagerStream(søkerKTable)
//    manuellInnstillingStream(søkerKTable)
//    manuellLøsningStream(søkerKTable)
//    manuellKvalitetssikringStream(søkerKTable)
//    endredePersonidenterStream(søkerKTable)
}
