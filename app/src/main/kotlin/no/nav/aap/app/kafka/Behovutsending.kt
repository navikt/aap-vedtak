package no.nav.aap.app.kafka

import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.avro.medlem.v1.Medlem
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Lytter
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.mapValues
import no.nav.aap.kafka.streams.named
import no.nav.aap.kafka.streams.produce
import org.apache.kafka.streams.kstream.Branched
import org.apache.kafka.streams.kstream.BranchedKStream
import org.apache.kafka.streams.kstream.KStream
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*

internal fun KStream<String, DtoBehov>.sendBehov(name: String, topics: Topics) {
    split(named("$name-split-behov"))
        .branch(topics.medlem, "$name-medlem", DtoBehov::erMedlem, ::ToAvroMedlem)
        .branch(topics.inntekter, "$name-inntekter", DtoBehov::erInntekter, ::ToInntekterKafkaDto)
}

private fun <JSON : Any, MAPPER> BranchedKStream<String, DtoBehov>.branch(
    topic: Topic<JSON>,
    branchName: String,
    predicate: (DtoBehov) -> Boolean,
    getMapper: () -> MAPPER
) where MAPPER : ToKafka<JSON>, MAPPER : Lytter =
    branch({ _, value -> predicate(value) }, Branched.withConsumer<String?, DtoBehov?> { chain ->
        chain
            .mapValues("branch-$branchName-map-behov") { value -> getMapper().also(value::accept).toJson() }
            .produce(topic, "branch-$branchName-produced-behov")
    }.withName("-branch-$branchName"))

private interface ToKafka<out JSON> {
    fun toJson(): JSON
}

private class ToAvroMedlem : Lytter, ToKafka<Medlem> {
    private lateinit var ident: String

    override fun medlem(ident: String) {
        this.ident = ident
    }

    override fun toJson(): Medlem = Medlem.newBuilder()
        .setId(UUID.randomUUID().toString()) // TraceId
        .setPersonident(ident)
        .setRequestBuilder(
            no.nav.aap.avro.medlem.v1.Request.newBuilder()
                .setArbeidetUtenlands(false)
                .setMottattDato(LocalDate.now())
                .setYtelse("AAP")
        ).build()
}

private class ToInntekterKafkaDto : Lytter, ToKafka<InntekterKafkaDto> {
    private lateinit var ident: String
    private lateinit var fom: YearMonth
    private lateinit var tom: YearMonth

    override fun behovInntekter(ident: String, fom: Year, tom: Year) {
        this.ident = ident
        this.fom = fom.atMonth(1)
        this.tom = tom.atMonth(12)
    }

    override fun toJson(): InntekterKafkaDto = InntekterKafkaDto(
        personident = ident,
        request = InntekterKafkaDto.Request(fom, tom),
        response = null,
    )
}
