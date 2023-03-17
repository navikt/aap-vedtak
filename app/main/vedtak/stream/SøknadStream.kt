package vedtak.stream

import io.micrometer.core.instrument.MeterRegistry
import vedtak.kafka.toSøkereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.SøkereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.modellapi.BehovModellApi
import no.nav.aap.modellapi.SøknadModellApi
import org.slf4j.LoggerFactory
import vedtak.kafka.*

private val secureLog = LoggerFactory.getLogger("secureLog")

internal fun Topology.søknadStream(
    søkere: KTable<SøkereKafkaDtoHistorikk>,
    lesSøknader: Boolean,
    registry: MeterRegistry
) {
    val søkerOgBehov = consume(Topics.søknad)
        .filter {
            if (!lesSøknader) secureLog.info("leser ikke søknad da toggle for lesing er skrudd av")
            lesSøknader
        }
        .processor(KafkaCounter(registry, "soknad_mottatt"))
        .leftJoinWith(søkere, søkere.buffer)
        .filter { (_, søkereKafkaDto) ->
            if (søkereKafkaDto != null) secureLog.warn("oppretter ikke ny søker pga eksisterende: $søkereKafkaDto")
            søkereKafkaDto == null
        }
        .processor(KafkaCounter(registry, "forstegangssoker"))
        .map { personident, søknadKafkaDto, _ ->
            val søknad = SøknadModellApi(personident, søknadKafkaDto.fødselsdato, søknadKafkaDto.innsendingTidspunkt)
            val (endretSøker, dtoBehov) = søknad.håndter()
            endretSøker.toSøkereKafkaDtoHistorikk(0) to dtoBehov
        }

    søkerOgBehov
        .map(Pair<SøkereKafkaDtoHistorikk, List<BehovModellApi>>::first)
        .produce(Topics.søkere, søkere.buffer) { it }

    søkerOgBehov
        .flatMap { (_, behov) -> behov.map(::BehovModellApiWrapper) }
        .sendBehov()
}
