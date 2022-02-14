package no.nav.aap.app

import no.nav.aap.app.kafka.consumedWithJson
import no.nav.aap.app.kafka.joinedWithJsonOnAvro
import no.nav.aap.app.log
import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.app.modell.toAvro
import no.nav.aap.domene.entitet.Fødselsdato
import no.nav.aap.domene.entitet.Personident
import no.nav.aap.hendelse.Søknad
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.kstream.Produced
import java.time.LocalDate
import java.util.*
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.medlem.v1.Request as AvroMedlemRequest
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker

fun StreamsBuilder.søknadStream(søkere: KTable<String, AvroSøker>) {
    val søknadAndSøkerStream =
        stream<String, JsonSøknad>("aap.aap-soknad-sendt.v1", consumedWithJson("soknad-mottatt"))
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
