package no.nav.aap.app

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.streams.Kafka
import no.nav.aap.app.kafka.streams.KafkaStreamsFactory
import no.nav.aap.app.kafka.streams.consumedWithJson
import no.nav.aap.app.kafka.streams.joinedWithJsonOnAvro
import no.nav.aap.app.modell.KafkaSøknad
import no.nav.aap.avro.medlem.v1.Request
import no.nav.aap.avro.vedtak.v1.Løsning_11_2
import no.nav.aap.domene.Søker
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.dto.DtoLøsningParagraf_11_2
import no.nav.aap.dto.DtoSak
import no.nav.aap.dto.DtoSøker
import no.nav.aap.dto.DtoVilkårsvurdering
import no.nav.aap.hendelse.LøsningParagraf_11_2
import no.nav.aap.hendelse.Søknad
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Produced
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.vedtak.v1.Sak as AvroSak
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker
import no.nav.aap.avro.vedtak.v1.Vilkårsvurdering as AvroVilkårsvurdering

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::streamsApp).start(wait = true)
}

private val log = LoggerFactory.getLogger("streamsApp")


fun Application.streamsApp(kafka: Kafka = KafkaStreamsFactory()) {
    val config = loadConfig<Config>()
    val topology = createTopology()
    kafka.createKafkaStream(topology, config.kafka)
    environment.monitor.subscribe(ApplicationStarted) { kafka.start() }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }
}

fun createTopology(): Topology =
    StreamsBuilder().apply {
        val søkere = table<String, AvroSøker>("aap.sokere.v1", Materialized.`as`("ktable-sokere"))

        val søknadAndSøkerStream =
            stream<String, KafkaSøknad>("aap.aap-soknad-sendt.v1", consumedWithJson("soknad-mottatt"))
                .peek { _, _ -> log.info("consumed aap.aap-soknad-sendt.v1") }
                .leftJoin(søkere, SøknadAndSøker::join, joinedWithJsonOnAvro("soknad-leftjoin-sokere"))
                .filter({ _, (_, søker) -> søker == null }, Named.`as`("skip-eksisterende-soker"))

        søknadAndSøkerStream
            .mapValues(::medlemBehov, Named.`as`("opprett-medlem-behov"))
            .peek { _, _ -> log.info("produced aap.medlem.v1") }
            .to("aap.medlem.v1", Produced.`as`("produced-behov-medlem"))

        søknadAndSøkerStream
            .mapValues(::opprettSøker, Named.`as`("opprett-soknad"))
            .peek { _, _ -> log.info("produced aap.sokere.v1") }
            .to("aap.sokere.v1", Produced.`as`("produced-ny-soker"))

        stream<String, AvroMedlem>("aap.medlem.v1", Consumed.`as`("medlem-mottatt"))
            .peek { _, _ -> log.info("consumed aap.medlem.v1") }
            .filter { _, medlem -> medlem.response != null }
            .selectKey({ _, medlem -> medlem.personident }, Named.`as`("keyed_personident"))
            .join(søkere, ::medlemLøsning)
            .peek { _, _ -> log.info("produced aap.sokere.v1") }
            .to("aap.sokere.v1", Produced.`as`("produced-soker-med-medlem"))

    }.build()

private fun opprettSøker(wrapper: SøknadAndSøker): AvroSøker {
    val søknad = Søknad(Personident(wrapper.søknad.ident.verdi), Fødselsdato(wrapper.søknad.fødselsdato))
    val søker = søknad.opprettSøker().apply {
        håndterSøknad(søknad)
    }

    return søker.toDto().toAvro()
}

fun DtoSøker.toAvro(): AvroSøker = AvroSøker.newBuilder()
    .setPersonident(personident)
    .setFødselsdato(fødselsdato)
    .setSaker(
        saker.map { sak ->
            AvroSak.newBuilder()
                .setTilstand(sak.tilstand)
                .setVilkårsvurderinger(
                    sak.vilkårsvurderinger.map { vilkår ->
                        AvroVilkårsvurdering.newBuilder()
                            .setLedd(vilkår.ledd)
                            .setParagraf(vilkår.paragraf)
                            .setTilstand(vilkår.tilstand)
                            .setLøsning112Manuell(vilkår.løsning_11_2_manuell?.let { Løsning_11_2(it.erMedlem) })
                            .setLøsning112Maskinell(vilkår.løsning_11_2_maskinell?.let { Løsning_11_2(it.erMedlem) })
                            .build()
                    }
                )
                .setVurderingsdato(sak.vurderingsdato)
                .build()
        }
    ).build()

private fun medlemBehov(søknadAndSøker: SøknadAndSøker): AvroMedlem {
    return AvroMedlem.newBuilder()
        .setId(UUID.randomUUID().toString()) // TraceId
        .setPersonident(søknadAndSøker.søknad.ident.verdi)
        .setRequestBuilder(
            Request.newBuilder()
                .setArbeidetUtenlands(false)
                .setMottattDato(LocalDate.now())
                .setYtelse("AAP")
        ).build()
}

internal data class SøknadAndSøker(val søknad: KafkaSøknad, val søker: AvroSøker?) {
    companion object {
        fun join(søknad: KafkaSøknad, søker: AvroSøker?) = SøknadAndSøker(søknad, søker)
    }
}

private fun medlemLøsning(avroMedlem: AvroMedlem, avroSøker: AvroSøker): AvroSøker {
    val dtoSøker = avroSøker.toDto()
    val søker = Søker.create(dtoSøker).apply {
        val medlem = avroMedlem.toDto()
        håndterLøsning(medlem)
    }

    return søker.toDto().toAvro()
}

fun AvroMedlem.toDto(): LøsningParagraf_11_2 = LøsningParagraf_11_2(
    erMedlem = LøsningParagraf_11_2.ErMedlem.valueOf(response.erMedlem.name)
)

fun AvroSøker.toDto(): DtoSøker = DtoSøker(
    personident = personident,
    fødselsdato = fødselsdato,
    saker = saker.map(AvroSak::toDto),
)

fun AvroSak.toDto(): DtoSak = DtoSak(
    tilstand = tilstand,
    vurderingsdato = vurderingsdato,
    vilkårsvurderinger = vilkårsvurderinger.map(AvroVilkårsvurdering::toDto)
)

fun AvroVilkårsvurdering.toDto(): DtoVilkårsvurdering = DtoVilkårsvurdering(
    paragraf = paragraf,
    ledd = ledd,
    tilstand = tilstand,
    løsning_11_2_manuell = løsning112Manuell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
    løsning_11_2_maskinell = løsning112Maskinell?.let { DtoLøsningParagraf_11_2(it.erMedlem) },
)














