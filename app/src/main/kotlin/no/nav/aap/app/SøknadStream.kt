package no.nav.aap.app

import no.nav.aap.app.kafka.*
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toAvro
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.Søknad
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import java.time.LocalDate
import java.util.*
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.medlem.v1.Request as AvroMedlemRequest
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker

fun StreamsBuilder.søknadStream(søkere: KTable<String, AvroSøker>, topics: Topics) {
    val søknadAndSøkerStream =
        stream(topics.søknad.name, topics.søknad.consumed("soknad-mottatt"))
            .peek { k, v -> log.info("consumed [aap.aap-soknad-sendt.v1] [$k] [$v]") }
            .leftJoin(søkere, SøknadAndSøker::join, topics.søknad.joined(topics.søkere))
            .filter({ _, (_, søker) -> søker == null }, named("skip-eksisterende-soker"))

    søknadAndSøkerStream
        .mapValues(::medlemBehov, named("opprett-medlem-behov"))
        .peek { k, v -> log.info("produced [aap.medlem.v1] [$k] [$v]") }
        .to("aap.medlem.v1", topics.medlem.produced("produced-behov-medlem"))

    søknadAndSøkerStream
        .mapValues(::opprettSøker, named("opprett-soknad"))
        .peek { k, v -> log.info("produced [aap.sokere.v1] [$k] [$v]") }
        .to("aap.sokere.v1", topics.søkere.produced("produced-ny-soker"))
}

private fun opprettSøker(wrapper: SøknadAndSøker): AvroSøker {
    val søknad = Søknad(Personident(wrapper.søknad.ident.verdi), Fødselsdato(wrapper.søknad.fødselsdato))
    val søker = søknad.opprettSøker().apply {
        håndterSøknad(søknad)
    }

    return søker.toDto().toAvro()
}

private fun medlemBehov(søknadAndSøker: SøknadAndSøker): AvroMedlem =
    AvroMedlem.newBuilder()
        .setId(UUID.randomUUID().toString()) // TraceId
        .setPersonident(søknadAndSøker.søknad.ident.verdi)
        .setRequestBuilder(
            AvroMedlemRequest.newBuilder()
                .setArbeidetUtenlands(false)
                .setMottattDato(LocalDate.now())
                .setYtelse("AAP")
        ).build()

private data class SøknadAndSøker(val søknad: JsonSøknad, val søker: AvroSøker?) {
    companion object {
        fun join(søknad: JsonSøknad, søker: AvroSøker?) = SøknadAndSøker(søknad, søker)
    }
}
