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
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

internal fun StreamsBuilder.søknadStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søkerOgBehov = stream(topics.søknad.name, topics.søknad.consumed("soknad-mottatt"))
        .selectKey ({ _, value -> value.ident.verdi }, named("soknad_keyed_personident"))
        .peek { k: String, v -> log.info("consumed [aap.aap-soknad-sendt.v1] [$k] [$v]") }
        .leftJoin(søkere, SøknadAndSøker::join, topics.søknad.joined(topics.søkere))
        .filter(named("skip-eksisterende-soker")) { _, (_, søker) -> søker == null }
        .mapValues(::opprettSøker, named("opprett-soknad"))

    søkerOgBehov
        .mapValues(named("soknad-hent-ut-soker")) { (søker) -> søker }
        .peek { k: String, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to(topics.søkere.name, topics.søkere.produced("produced-ny-soker"))

    søkerOgBehov
        .flatMapValues(named("soknad-hent-ut-behov")) { (_, dtoBehov) -> dtoBehov }
        .split(named("soknad-split-behov"))
        .branch(topics.medlem, "soknad-medlem", DtoBehov::erMedlem, ::ToAvroMedlem)
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
