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
import vedtak.kafka.Tables
import vedtak.kafka.Topics
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KStreams
import no.nav.aap.kafka.streams.v2.KafkaStreams
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.kafka.streams.v2.config.StreamsConfig
import no.nav.aap.kafka.streams.v2.processor.state.GaugeStoreEntriesStateScheduleProcessor
import no.nav.aap.kafka.streams.v2.processor.state.MigrateStateInitProcessor
import no.nav.aap.kafka.streams.v2.topology
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import vedtak.stream.*
import vedtak.stream.andreFolketrygdytelserStream
import vedtak.stream.inntekterStream
import vedtak.stream.medlemStream
import vedtak.stream.søknadStream
import kotlin.time.Duration.Companion.minutes

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

internal fun Application.server(kafka: KStreams = KafkaStreams()) {
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

    søknadStream(søkerKTable, lesSøknader, prometheus)
    medlemStream(søkerKTable)
    inntekterStream(søkerKTable)
    andreFolketrygdytelserStream(søkerKTable)
    iverksettelseAvVedtakStream(søkerKTable)
    sykepengedagerStream(søkerKTable)
    manuellInnstillingStream(søkerKTable)
    manuellLøsningStream(søkerKTable)
    manuellKvalitetssikringStream(søkerKTable)
    endredePersonidenterStream(søkerKTable)
}
