package no.nav.aap.app

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toAvro
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Lytter
import no.nav.aap.hendelse.Søknad
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.*
import java.time.LocalDate
import java.util.*
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.medlem.v1.Request as AvroMedlemRequest
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker

fun StreamsBuilder.søknadStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søknadAndSøkerStream =
        stream(topics.søknad.name, topics.søknad.consumed("soknad-mottatt"))
            .peek { k: String, v -> log.info("consumed [aap.aap-soknad-sendt.v1] [$k] [$v]") }
            .leftJoin(søkere, SøknadAndSøker::join, topics.søknad.joined(topics.søkere))
            .filter(named("skip-eksisterende-soker")) { _, (_, søker) -> søker == null }

    val søkerOgBehov = søknadAndSøkerStream
        .mapValues(::opprettSøker, named("opprett-soknad"))

    søkerOgBehov
        .mapValues(named("hent-ut-soker")) { (søker) -> søker }
        .peek { k: String, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to("aap.sokere.v1", topics.søkere.produced("produced-ny-soker"))


    søkerOgBehov
        .flatMapValues(named("hent-ut-behov")) { (_, dtoBehov) -> dtoBehov }
        .split(named("split-behov"))
        .branch(topics.medlem, "medlem", DtoBehov::erMedlem, ::ToAvroMedlem)
}

private fun <AVROVALUE : Any, MAPPER> BranchedKStream<String, DtoBehov>.branch(
    topic: Topic<String, AVROVALUE>,
    branchName: String,
    predicate: (DtoBehov) -> Boolean,
    getMapper: () -> MAPPER
) where MAPPER : ToAvro<AVROVALUE>, MAPPER : Lytter =
    branch({ _, value -> predicate(value) }, Branched.withConsumer<String?, DtoBehov?> { chain ->
        chain
            .mapValues(named("branch-$branchName-map-behov")) { value -> getMapper().also(value::accept).toAvro() }
            .peek { k: String, v -> log.info("produced [${topic.name}] [$k] [$v]") }
            .to(topic.name, topic.produced("branch-$branchName-produced-behov"))
    }.withName("-branch-$branchName"))

private fun <K, V> KStream<K, V>.filter(named: Named, predicate: (K, V) -> Boolean) = filter(predicate, named)
private fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (V) -> VR) = mapValues(mapper, named)
private fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (K, V) -> VR) = mapValues(mapper, named)
private fun <K, V, VR> KStream<K, V>.flatMapValues(named: Named, mapper: (V) -> Iterable<VR>) =
    flatMapValues(mapper, named)


private interface ToAvro<out AVROVALUE> {
    fun toAvro(): AVROVALUE
}

private class ToAvroMedlem : Lytter, ToAvro<AvroMedlem> {
    private lateinit var ident: String

    override fun medlem(ident: String) {
        this.ident = ident
    }

    override fun toAvro(): AvroMedlem = AvroMedlem.newBuilder()
        .setId(UUID.randomUUID().toString()) // TraceId
        .setPersonident(ident)
        .setRequestBuilder(
            AvroMedlemRequest.newBuilder()
                .setArbeidetUtenlands(false)
                .setMottattDato(LocalDate.now())
                .setYtelse("AAP")
        ).build()
}

private fun opprettSøker(wrapper: SøknadAndSøker): Pair<AvroSøker, List<DtoBehov>> {
    val ident = wrapper.søknad.ident.verdi
    val søknad = Søknad(Personident(ident), Fødselsdato(wrapper.søknad.fødselsdato))
    val søker = søknad.opprettSøker()
    søker.håndterSøknad(søknad)

    return søker.toDto().toAvro() to søknad.behov().map { it.toDto(ident) }
}

private data class SøknadAndSøker(val søknad: JsonSøknad, val søker: AvroSøker?) {
    companion object {
        fun join(søknad: JsonSøknad, søker: AvroSøker?) = SøknadAndSøker(søknad, søker)
    }
}
