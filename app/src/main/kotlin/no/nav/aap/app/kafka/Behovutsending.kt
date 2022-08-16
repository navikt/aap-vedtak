package no.nav.aap.app.kafka

import no.nav.aap.app.modell.InntekterKafkaDto
import no.nav.aap.app.modell.MedlemKafkaDto
import no.nav.aap.hendelse.DtoBehov
import no.nav.aap.hendelse.Lytter
import no.nav.aap.kafka.streams.Behov
import no.nav.aap.kafka.streams.BehovExtractor
import no.nav.aap.kafka.streams.branch
import no.nav.aap.kafka.streams.extension.mapValues
import no.nav.aap.kafka.streams.sendBehov
import org.apache.kafka.streams.kstream.KStream
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*

internal fun KStream<String, DtoBehov>.sendBehov(name: String) {
    mapValues("$name-wrap-behov") { value -> DtoBehovWrapper(value) }
        .sendBehov(name) {
            branch(Topics.medlem, "$name-medlem", DtoBehovWrapper::erMedlem, ::ToMedlemKafkaDto)
            branch(Topics.inntekter, "$name-inntekter", DtoBehovWrapper::erInntekter, ::ToInntekterKafkaDto)
        }
}

private class DtoBehovWrapper(
    private val dtoBehov: DtoBehov
) : Behov<Lytter> {
    fun erMedlem() = Sjekk.ErMedlem().apply(this::accept).er()
    fun erInntekter() = Sjekk.ErInntekter().apply(this::accept).er()
    override fun accept(visitor: Lytter) {
        dtoBehov.accept(visitor)
    }
}

private sealed class Sjekk : Lytter {
    protected var er = false
    fun er() = er

    class ErMedlem : Sjekk() {
        override fun medlem(ident: String) {
            er = true
        }
    }

    class ErInntekter : Sjekk() {
        override fun behovInntekter(ident: String, fom: Year, tom: Year) {
            er = true
        }
    }
}

private class ToMedlemKafkaDto : Lytter, BehovExtractor<MedlemKafkaDto> {
    private lateinit var ident: String

    override fun medlem(ident: String) {
        this.ident = ident
    }

    override fun toJson(): MedlemKafkaDto =
        MedlemKafkaDto(
            personident = ident,
            id = UUID.randomUUID(), // trace-id
            response = null,
            request = MedlemKafkaDto.Request(
                mottattDato = LocalDate.now(),
                ytelse = "AAP",
                arbeidetUtenlands = false
            ),
        )
}

private class ToInntekterKafkaDto : Lytter, BehovExtractor<InntekterKafkaDto> {
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
