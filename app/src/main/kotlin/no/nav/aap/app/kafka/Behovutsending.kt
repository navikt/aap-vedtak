package no.nav.aap.app.kafka

import no.nav.aap.avro.inntekter.v1.Inntekter
import no.nav.aap.avro.inntekter.v1.Request
import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Lytter
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.named
import no.nav.aap.kafka.streams.produce
import org.apache.kafka.streams.kstream.Branched
import org.apache.kafka.streams.kstream.BranchedKStream
import org.apache.kafka.streams.kstream.KStream
import java.time.LocalDate
import java.time.Year
import java.util.*

internal fun KStream<String, DtoBehov>.sendBehov(name: String, topics: Topics) {
    split(named("$name-split-behov"))
        .branch(topics.medlem, "$name-medlem", DtoBehov::erMedlem, ::ToAvroMedlem)
        .branch(topics.inntekter, "$name-inntekter", DtoBehov::erInntekter, ::ToAvroInntekter)
}

private fun <AVROVALUE : Any, MAPPER> BranchedKStream<String, DtoBehov>.branch(
    topic: Topic<AVROVALUE>,
    branchName: String,
    predicate: (DtoBehov) -> Boolean,
    getMapper: () -> MAPPER
) where MAPPER : ToAvro<AVROVALUE>, MAPPER : Lytter =
    branch({ _, value -> predicate(value) }, Branched.withConsumer<String?, DtoBehov?> { chain ->
        chain
            .mapValues(named("branch-$branchName-map-behov")) { value -> getMapper().also(value::accept).toAvro() }
            .produce(topic) { "branch-$branchName-produced-behov" }
    }.withName("-branch-$branchName"))

private interface ToAvro<out AVROVALUE> {
    fun toAvro(): AVROVALUE
}

private class ToAvroMedlem : Lytter, ToAvro<Medlem> {
    private lateinit var ident: String

    override fun medlem(ident: String) {
        this.ident = ident
    }

    override fun toAvro(): Medlem = Medlem.newBuilder()
        .setId(UUID.randomUUID().toString()) // TraceId
        .setPersonident(ident)
        .setRequestBuilder(
            no.nav.aap.avro.medlem.v1.Request.newBuilder()
                .setArbeidetUtenlands(false)
                .setMottattDato(LocalDate.now())
                .setYtelse("AAP")
        ).build()
}

private class ToAvroInntekter : Lytter, ToAvro<Inntekter> {
    private lateinit var ident: String
    private lateinit var fom: Year
    private lateinit var tom: Year

    override fun behovInntekter(ident: String, fom: Year, tom: Year) {
        this.ident = ident
        this.fom = fom
        this.tom = tom
    }

    override fun toAvro(): Inntekter = Inntekter.newBuilder()
        .setPersonident(ident)
        .setRequestBuilder(
            Request.newBuilder()
                .setFom(fom.atDay(1))
                .setTom(tom.atMonth(12).atEndOfMonth())
        ).build()
}
