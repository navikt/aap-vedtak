package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.*
import no.nav.aap.kafka.streams.Behov
import no.nav.aap.kafka.streams.BehovExtractor
import no.nav.aap.kafka.streams.branch
import no.nav.aap.kafka.streams.extension.mapValues
import no.nav.aap.kafka.streams.sendBehov
import no.nav.aap.modellapi.BehovModellApi
import no.nav.aap.modellapi.LytterModellApi
import no.nav.aap.modellapi.VedtakModellApi
import org.apache.kafka.streams.kstream.KStream
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*

internal fun KStream<String, BehovModellApi>.sendBehov(name: String) {
    this
        .mapValues("$name-wrap-behov", ::BehovModellApiWrapper)
        .sendBehov(name) {
            branch(Topics.medlem, "$name-medlem", BehovModellApiWrapper::erMedlem, ::ToMedlemKafkaDto)
            branch(Topics.inntekter, "$name-inntekter", BehovModellApiWrapper::erInntekter, ::ToInntekterKafkaDto)
            branch(Topics.andreFolketrygdsytelser, "$name-andre-folketrygdytelser", BehovModellApiWrapper::erAnderFolketrygdytelser, ::ToAndreFolketrygdytelserKafkaDto)
            branch(Topics.vedtak, "$name-vedtak", BehovModellApiWrapper::erIverksettVedtak, ::ToIverksettVedtakKafkaDto)
            branch(Topics.sykepengedager, "$name-sykepengedager", BehovModellApiWrapper::erSykepengedager, ::ToSykepengedagerKafkaDto)
        }
}

private class BehovModellApiWrapper(private val behovModellApi: BehovModellApi) : Behov<LytterModellApi> {
    override fun accept(visitor: LytterModellApi) = behovModellApi.accept(visitor)

    fun erMedlem() = Sjekk.ErMedlem().apply(this::accept).er()
    fun erSykepengedager() = Sjekk.ErSykepengedager().apply(this::accept).er()
    fun erInntekter() = Sjekk.ErInntekter().apply(this::accept).er()
    fun erAnderFolketrygdytelser() = Sjekk.ErAndreFolketrygdytelser().apply(this::accept).er()
    fun erIverksettVedtak() = Sjekk.ErIverksettVedtak().apply(this::accept).er()
}

private sealed class Sjekk : LytterModellApi {
    protected var er = false
    fun er() = er

    class ErMedlem : Sjekk() {
        override fun medlem(ident: String) {
            er = true
        }
    }

    class ErSykepengedager : Sjekk() {
        override fun behov_8_48AndreLedd(ident: String) {
            er = true
        }
    }

    class ErInntekter : Sjekk() {
        override fun behovInntekter(ident: String, fom: Year, tom: Year) {
            er = true
        }
    }

    class ErAndreFolketrygdytelser : Sjekk() {
        override fun behov_11_27(ident: String) {
            er = true
        }
    }

    class ErIverksettVedtak : Sjekk() {
        override fun behovIverksettVedtak(vedtakModellApi: VedtakModellApi) {
            er = true
        }
    }
}

private class ToMedlemKafkaDto : LytterModellApi, BehovExtractor<MedlemKafkaDto> {
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

private class ToSykepengedagerKafkaDto : LytterModellApi, BehovExtractor<SykepengedagerKafkaDto> {
    override fun toJson(): SykepengedagerKafkaDto = SykepengedagerKafkaDto(response = null)
}

private class ToInntekterKafkaDto : LytterModellApi, BehovExtractor<InntekterKafkaDto> {
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

private class ToAndreFolketrygdytelserKafkaDto : LytterModellApi, BehovExtractor<AndreFolketrygdytelserKafkaDto> {
    override fun toJson() = AndreFolketrygdytelserKafkaDto(response = null)
}

private class ToIverksettVedtakKafkaDto : LytterModellApi, BehovExtractor<IverksettVedtakKafkaDto> {
    private lateinit var vedtaksid: UUID
    private lateinit var innvilget: Innvilget
    private lateinit var grunnlagsfaktor: Grunnlagsfaktor
    private lateinit var vedtaksdato: LocalDate
    private lateinit var virkningsdato: LocalDate
    private lateinit var fødselsdato: LocalDate

    override fun behovIverksettVedtak(vedtakModellApi: VedtakModellApi) {
        this.vedtaksid = vedtakModellApi.vedtaksid
        this.innvilget = Innvilget(vedtakModellApi.innvilget)
        this.grunnlagsfaktor = Grunnlagsfaktor(vedtakModellApi.inntektsgrunnlag.grunnlagsfaktor)
        this.vedtaksdato = vedtakModellApi.vedtaksdato
        this.virkningsdato = vedtakModellApi.virkningsdato
        this.fødselsdato = vedtakModellApi.inntektsgrunnlag.fødselsdato
    }

    override fun toJson(): IverksettVedtakKafkaDto = IverksettVedtakKafkaDto(
        vedtaksid = vedtaksid,
        innvilget = innvilget.innvilget,
        grunnlagsfaktor = grunnlagsfaktor.grunnlagsfaktor,
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato,
        fødselsdato = fødselsdato
    )

    private data class Innvilget(val innvilget: Boolean)
    private data class Grunnlagsfaktor(val grunnlagsfaktor: Double)
}
