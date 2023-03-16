package vedtak.kafka

import no.nav.aap.dto.kafka.*
import no.nav.aap.kafka.streams.v2.processor.ProcessorMetadata

internal data class Content<T>(
    val data: T,
    val metadata: ProcessorMetadata
)

internal data class MonadeKafkaDto(
    val endredePersonidenter: List<Content<String>> = emptyList(),
    val iverksettelse: List<Content<IverksettelseAvVedtakKafkaDto>> = emptyList(),
    val medlem: List<Content<MedlemKafkaDto>> = emptyList(),
    val inntekter: List<Content<InntekterKafkaDto>> = emptyList(),
    val andreYtelser: List<Content<AndreFolketrygdytelserKafkaDto>> = emptyList(),
    val sykepengedager: List<Content<SykepengedagerKafkaDto>> = emptyList(),
    val instilling_11_6: List<Content<Innstilling_11_6KafkaDto>> = emptyList(),
    val manuell_11_2: List<Content<Løsning_11_2_manuellKafkaDto>> = emptyList(),
    val manuell_11_3: List<Content<Løsning_11_3_manuellKafkaDto>> = emptyList(),
    val manuell_11_4: List<Content<Løsning_11_4_ledd2_ledd3_manuellKafkaDto>> = emptyList(),
    val manuell_11_5: List<Content<Løsning_11_5_manuellKafkaDto>> = emptyList(),
    val manuell_11_6: List<Content<Løsning_11_6_manuellKafkaDto>> = emptyList(),
    val manuell_11_19: List<Content<Løsning_11_19_manuellKafkaDto>> = emptyList(),
    val manuell_11_29: List<Content<Løsning_11_29_manuellKafkaDto>> = emptyList(),
    val manuell_22_13: List<Content<Løsning_22_13_manuellKafkaDto>> = emptyList(),
    val kvalitetssikring_11_2: List<Content<Kvalitetssikring_11_2KafkaDto>> = emptyList(),
    val kvalitetssikring_11_3: List<Content<Kvalitetssikring_11_3KafkaDto>> = emptyList(),
    val kvalitetssikring_11_4: List<Content<Kvalitetssikring_11_4_ledd2_ledd3KafkaDto>> = emptyList(),
    val kvalitetssikring_11_5: List<Content<Kvalitetssikring_11_5KafkaDto>> = emptyList(),
    val kvalitetssikring_11_6: List<Content<Kvalitetssikring_11_6KafkaDto>> = emptyList(),
    val kvalitetssikring_11_19: List<Content<Kvalitetssikring_11_19KafkaDto>> = emptyList(),
    val kvalitetssikring_11_29: List<Content<Kvalitetssikring_11_29KafkaDto>> = emptyList(),
    val kvalitetssikring_22_13: List<Content<Kvalitetssikring_22_13KafkaDto>> = emptyList(),
) {

    operator fun plus(other: MonadeKafkaDto): MonadeKafkaDto = copy(
        endredePersonidenter = endredePersonidenter + other.endredePersonidenter,
        iverksettelse = iverksettelse + other.iverksettelse,
        medlem = medlem + other.medlem,
        inntekter = inntekter + other.inntekter,
        andreYtelser = andreYtelser + other.andreYtelser,
        sykepengedager = sykepengedager + other.sykepengedager,
        instilling_11_6 = instilling_11_6 + other.instilling_11_6,
        manuell_11_2 = manuell_11_2 + other.manuell_11_2,
        manuell_11_3 = manuell_11_3 + other.manuell_11_3,
        manuell_11_4 = manuell_11_4 + other.manuell_11_4,
        manuell_11_5 = manuell_11_5 + other.manuell_11_5,
        manuell_11_6 = manuell_11_6 + other.manuell_11_6,
        manuell_11_19 = manuell_11_19 + other.manuell_11_19,
        manuell_11_29 = manuell_11_29 + other.manuell_11_29,
        manuell_22_13 = manuell_22_13 + other.manuell_22_13,
        kvalitetssikring_11_2 = kvalitetssikring_11_2 + other.kvalitetssikring_11_2,
        kvalitetssikring_11_3 = kvalitetssikring_11_3 + other.kvalitetssikring_11_3,
        kvalitetssikring_11_4 = kvalitetssikring_11_4 + other.kvalitetssikring_11_4,
        kvalitetssikring_11_5 = kvalitetssikring_11_5 + other.kvalitetssikring_11_5,
        kvalitetssikring_11_6 = kvalitetssikring_11_6 + other.kvalitetssikring_11_6,
        kvalitetssikring_11_19 = kvalitetssikring_11_19 + other.kvalitetssikring_11_19,
        kvalitetssikring_11_29 = kvalitetssikring_11_29 + other.kvalitetssikring_11_29,
        kvalitetssikring_22_13 = kvalitetssikring_22_13 + other.kvalitetssikring_22_13,
    )

    internal constructor(endredePersonidenter: String, metadata: ProcessorMetadata) : this(
        endredePersonidenter = listOf(Content(endredePersonidenter, metadata))
    )
    internal constructor(iverksettelse: IverksettelseAvVedtakKafkaDto, metadata: ProcessorMetadata) : this(
        iverksettelse = listOf(Content(iverksettelse, metadata))
    )

    internal constructor(medlem: MedlemKafkaDto, metadata: ProcessorMetadata) : this(
        medlem = listOf(Content(medlem, metadata))
    )

    internal constructor(inntekter: InntekterKafkaDto, metadata: ProcessorMetadata) : this(
        inntekter = listOf(Content(inntekter, metadata))
    )

    internal constructor(andreYtelser: AndreFolketrygdytelserKafkaDto, metadata: ProcessorMetadata) : this(
        andreYtelser = listOf(Content(andreYtelser, metadata))
    )

    internal constructor(sykepengedager: SykepengedagerKafkaDto, metadata: ProcessorMetadata) : this(
        sykepengedager = listOf(Content(sykepengedager, metadata))
    )

    internal constructor(instilling_11_6: Innstilling_11_6KafkaDto, metadata: ProcessorMetadata) : this(
        instilling_11_6 = listOf(Content(instilling_11_6, metadata))
    )

    internal constructor(manuell_11_2: Løsning_11_2_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_2 = listOf(Content(manuell_11_2, metadata))
    )

    internal constructor(manuell_11_3: Løsning_11_3_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_3 = listOf(Content(manuell_11_3, metadata))
    )

    internal constructor(manuell_11_4: Løsning_11_4_ledd2_ledd3_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_4 = listOf(Content(manuell_11_4, metadata))
    )

    internal constructor(manuell_11_5: Løsning_11_5_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_5 = listOf(Content(manuell_11_5, metadata))
    )

    internal constructor(manuell_11_6: Løsning_11_6_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_6 = listOf(Content(manuell_11_6, metadata))
    )

    internal constructor(manuell_11_19: Løsning_11_19_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_19 = listOf(Content(manuell_11_19, metadata))
    )

    internal constructor(manuell_11_29: Løsning_11_29_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_11_29 = listOf(Content(manuell_11_29, metadata))
    )

    internal constructor(manuell_22_13: Løsning_22_13_manuellKafkaDto, metadata: ProcessorMetadata) : this(
        manuell_22_13 = listOf(Content(manuell_22_13, metadata))
    )

    internal constructor(kvalitetssikring_11_2: Kvalitetssikring_11_2KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_11_2 = listOf(Content(kvalitetssikring_11_2, metadata))
    )

    internal constructor(kvalitetssikring_11_3: Kvalitetssikring_11_3KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_11_3 = listOf(Content(kvalitetssikring_11_3, metadata))
    )

    internal constructor(
        kvalitetssikring_11_4: Kvalitetssikring_11_4_ledd2_ledd3KafkaDto,
        metadata: ProcessorMetadata
    ) : this(
        kvalitetssikring_11_4 = listOf(Content(kvalitetssikring_11_4, metadata))
    )

    internal constructor(kvalitetssikring_11_5: Kvalitetssikring_11_5KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_11_5 = listOf(Content(kvalitetssikring_11_5, metadata))
    )

    internal constructor(kvalitetssikring_11_6: Kvalitetssikring_11_6KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_11_6 = listOf(Content(kvalitetssikring_11_6, metadata))
    )

    internal constructor(kvalitetssikring_11_19: Kvalitetssikring_11_19KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_11_19 = listOf(Content(kvalitetssikring_11_19, metadata))
    )

    internal constructor(kvalitetssikring_11_29: Kvalitetssikring_11_29KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_11_29 = listOf(Content(kvalitetssikring_11_29, metadata))
    )

    internal constructor(kvalitetssikring_22_13: Kvalitetssikring_22_13KafkaDto, metadata: ProcessorMetadata) : this(
        kvalitetssikring_22_13 = listOf(Content(kvalitetssikring_22_13, metadata))
    )
}
