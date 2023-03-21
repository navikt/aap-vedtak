package vedtak.kafka

import no.nav.aap.dto.kafka.AndreFolketrygdytelserKafkaDto
import no.nav.aap.dto.kafka.InntekterKafkaDto
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.dto.kafka.MedlemKafkaDto
import no.nav.aap.kafka.streams.v2.behov.Behov
import no.nav.aap.kafka.streams.v2.behov.BehovExtractor
import no.nav.aap.kafka.streams.v2.stream.MappedStream
import no.nav.aap.modellapi.BehovModellApi
import no.nav.aap.modellapi.LytterModellApi
import no.nav.aap.modellapi.VedtakModellApi
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*

internal fun MappedStream<BehovModellApiWrapper>.sendBehov() {
    this
        .branch(BehovModellApiWrapper::erMedlem) { stream ->
            stream
                .map { value -> ToMedlemKafkaDto().also(value::accept).toJson() }
                .produce(Topics.medlem)
        }
        .branch(BehovModellApiWrapper::erInntekter) { stream ->
            stream
                .map { value ->
                    ToInntekterKafkaDto().also(value::accept).toJson()
                }
                .produce(Topics.inntekter)
        }
        .branch(BehovModellApiWrapper::erAnderFolketrygdytelser) { stream ->
            stream
                .map { value -> ToAndreFolketrygdytelserKafkaDto().also(value::accept).toJson() }
                .produce(Topics.andreFolketrygdsytelser)
        }
        .branch(BehovModellApiWrapper::erIverksettVedtak) { stream ->
            stream
                .map { value -> ToIverksettVedtakKafkaDto().also(value::accept).toJson() }
                .produce(Topics.vedtak)
        }
        .branch(BehovModellApiWrapper::erSykepengedager) { stream ->
            stream
                .map { _ -> "".toByteArray() }
                .produce(Topics.subscribeSykepengedager)
        }
}

internal class BehovModellApiWrapper(private val behovModellApi: BehovModellApi) : Behov<LytterModellApi> {
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

internal class ToMedlemKafkaDto : LytterModellApi, BehovExtractor<MedlemKafkaDto> {
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

internal class ToInntekterKafkaDto : LytterModellApi, BehovExtractor<InntekterKafkaDto> {
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

internal class ToAndreFolketrygdytelserKafkaDto : LytterModellApi, BehovExtractor<AndreFolketrygdytelserKafkaDto> {
    override fun toJson() = AndreFolketrygdytelserKafkaDto(response = null)
}

internal class ToIverksettVedtakKafkaDto : LytterModellApi, BehovExtractor<IverksettVedtakKafkaDto> {
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
