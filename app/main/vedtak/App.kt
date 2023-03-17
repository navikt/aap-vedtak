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
import no.nav.aap.kafka.streams.v2.KafkaStreams
import no.nav.aap.kafka.streams.v2.Streams
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.kafka.streams.v2.config.StreamsConfig
import no.nav.aap.kafka.streams.v2.processor.state.GaugeStoreEntriesStateScheduleProcessor
import no.nav.aap.kafka.streams.v2.processor.state.MigrateStateInitProcessor
import no.nav.aap.kafka.streams.v2.topology
import no.nav.aap.ktor.config.loadConfig
import no.nav.aap.modellapi.BehovModellApi
import org.apache.kafka.clients.producer.Producer
import vedtak.kafka.*
import vedtak.stream.endredePersonidenterStream
import vedtak.stream.søknadStream
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

    søknadStream(søkerKTable, lesSøknader, prometheus)
    endredePersonidenterStream(søkerKTable) // TODO: finn beste måte å håndter endrede personidenter på

    // TODO: bytt ut alle disse til pub-sub for å slippe å sjekke etter null responses
    consume(Topics.medlem)
        .filter { medlem -> medlem.response != null }
        .rekey { medlem -> medlem.personident }
        .mapWithMetadata{ dto, metadata -> MonadeKafkaDto(medlem = listOf(MedlemMonoide(metadata, dto)))}
        .produce(Topics.monade)

    consume(Topics.inntekter)
        .filter { inntekter -> inntekter.response != null }
        .mapWithMetadata{ dto, metadata -> MonadeKafkaDto(inntekter = listOf(InntekterMonoide(metadata, dto)))}
        .produce(Topics.monade)

    consume(Topics.andreFolketrygdsytelser)
        .filter { value -> value.response != null }
        .mapWithMetadata{ dto, metadata -> MonadeKafkaDto(andreYtelser = listOf(AndreFolketrygdytelserMonoide(metadata, dto)))}
        .produce(Topics.monade)

    consume(Topics.sykepengedager)
        .filter { kafkaDto -> kafkaDto.response != null }
        .mapWithMetadata{ dto, metadata -> MonadeKafkaDto(sykepengedager = listOf(SykepengedagerMonoide(metadata, dto)))}
        .produce(Topics.monade)

    consume(Topics.iverksettelseAvVedtak).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(iverksettelse = listOf(IverksettelseAvVedtakMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.innstilling_11_6).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(innstilling_11_6 = listOf(Innstilling_11_6Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_2).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_2 = listOf(Løsning_11_2_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_3).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_3 = listOf(Løsning_11_3_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_4).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_4 = listOf(Løsning_11_4_ledd2_ledd3_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_5).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_5 = listOf(Løsning_11_5_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_6).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_6 = listOf(Løsning_11_6_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_19).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_19 = listOf(Løsning_11_19_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_11_29).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_11_29 = listOf(Løsning_11_29_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.manuell_22_13).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(manuell_22_13 = listOf(Løsning_22_13_manuellMonoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_2).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_2 = listOf(Kvalitetssikring_11_2Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_3).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_3 = listOf(Kvalitetssikring_11_3Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_4).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_4 = listOf(Kvalitetssikring_11_4_ledd2_ledd3Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_5).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_5 = listOf(Kvalitetssikring_11_5Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_6).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_6 = listOf(Kvalitetssikring_11_6Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_19).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_19 = listOf(Kvalitetssikring_11_19Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_11_29).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_11_29 = listOf(Kvalitetssikring_11_29Monoide(metadata, dto)))}.produce(Topics.monade)
    consume(Topics.kvalitetssikring_22_13).mapWithMetadata{ dto, metadata -> MonadeKafkaDto(kvalitetssikring_22_13 = listOf(Kvalitetssikring_22_13Monoide(metadata, dto)))}.produce(Topics.monade)

    val søkerOgBehov = consume(Topics.monade)
        .windowed(5_000.toDuration(DurationUnit.MILLISECONDS), 500.toDuration(DurationUnit.MILLISECONDS))
        .reduce { monade, nextMonade -> monade + nextMonade }
        .joinWith(søkerKTable)
        .map { monade, søkerKafkaDto ->

            val hendelser = monade.sorted()
            val (søker, alleBehov) = hendelser.fold(søkerKafkaDto.søkereKafkaDto.toModellApi() to emptyList<BehovModellApi>()) { (søker, akkumulerteBehov), hendelse ->
                val (oppdatertSøker, nyeBehov) = hendelse.håndter(søker)
                oppdatertSøker to akkumulerteBehov + nyeBehov
            }

            søker.toSøkereKafkaDtoHistorikk(søkerKafkaDto.sekvensnummer) to alleBehov
        }

    søkerOgBehov
        .map(Pair<SøkereKafkaDtoHistorikk, List<BehovModellApi>>::first)
        .produce(Topics.søkere, søkerKTable.buffer) { it }

    søkerOgBehov
        .flatMap { (_, behov) -> behov.map(::BehovModellApiWrapper) }
        .sendBehov()
}
