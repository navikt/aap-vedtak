package vedtak.kafka

import no.nav.aap.dto.kafka.*
import no.nav.aap.kafka.streams.v2.processor.ProcessorMetadata
import no.nav.aap.modellapi.BehovModellApi
import no.nav.aap.modellapi.SøkerModellApi

internal data class MonadeKafkaDto(
    val iverksettelse: List<IverksettelseAvVedtakMonoide> = emptyList(),
    val medlem: List<MedlemMonoide> = emptyList(),
    val inntekter: List<InntekterMonoide> = emptyList(),
    val andreYtelser: List<AndreFolketrygdytelserMonoide> = emptyList(),
    val sykepengedager: List<SykepengedagerMonoide> = emptyList(),
    val innstilling_11_6: List<Innstilling_11_6Monoide> = emptyList(),
    val manuell_11_2: List<Løsning_11_2_manuellMonoide> = emptyList(),
    val manuell_11_3: List<Løsning_11_3_manuellMonoide> = emptyList(),
    val manuell_11_4: List<Løsning_11_4_ledd2_ledd3_manuellMonoide> = emptyList(),
    val manuell_11_5: List<Løsning_11_5_manuellMonoide> = emptyList(),
    val manuell_11_6: List<Løsning_11_6_manuellMonoide> = emptyList(),
    val manuell_11_19: List<Løsning_11_19_manuellMonoide> = emptyList(),
    val manuell_11_29: List<Løsning_11_29_manuellMonoide> = emptyList(),
    val manuell_22_13: List<Løsning_22_13_manuellMonoide> = emptyList(),
    val kvalitetssikring_11_2: List<Kvalitetssikring_11_2Monoide> = emptyList(),
    val kvalitetssikring_11_3: List<Kvalitetssikring_11_3Monoide> = emptyList(),
    val kvalitetssikring_11_4: List<Kvalitetssikring_11_4_ledd2_ledd3Monoide> = emptyList(),
    val kvalitetssikring_11_5: List<Kvalitetssikring_11_5Monoide> = emptyList(),
    val kvalitetssikring_11_6: List<Kvalitetssikring_11_6Monoide> = emptyList(),
    val kvalitetssikring_11_19: List<Kvalitetssikring_11_19Monoide> = emptyList(),
    val kvalitetssikring_11_29: List<Kvalitetssikring_11_29Monoide> = emptyList(),
    val kvalitetssikring_22_13: List<Kvalitetssikring_22_13Monoide> = emptyList(),
) {

    fun sorted(): List<Håndterbar> = (
            iverksettelse +
                    medlem +
                    inntekter +
                    andreYtelser +
                    sykepengedager +
                    innstilling_11_6 +
                    manuell_11_2 +
                    manuell_11_3 +
                    manuell_11_4 +
                    manuell_11_5 +
                    manuell_11_6 +
                    manuell_11_19 +
                    manuell_11_29 +
                    manuell_22_13 +
                    kvalitetssikring_11_2 +
                    kvalitetssikring_11_3 +
                    kvalitetssikring_11_4 +
                    kvalitetssikring_11_5 +
                    kvalitetssikring_11_6 +
                    kvalitetssikring_11_19 +
                    kvalitetssikring_11_29 +
                    kvalitetssikring_22_13
            ).sortedBy { it.metadata.timestamp }

    operator fun plus(other: MonadeKafkaDto): MonadeKafkaDto = copy(
        iverksettelse = iverksettelse + other.iverksettelse,
        medlem = medlem + other.medlem,
        inntekter = inntekter + other.inntekter,
        andreYtelser = andreYtelser + other.andreYtelser,
        sykepengedager = sykepengedager + other.sykepengedager,
        innstilling_11_6 = innstilling_11_6 + other.innstilling_11_6,
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
}

interface Håndterbar {
    val metadata: ProcessorMetadata
    fun håndter(søker: SøkerModellApi): Pair<SøkerModellApi, List<BehovModellApi>>
}

internal class IverksettelseAvVedtakMonoide(
    override val metadata: ProcessorMetadata,
    val dto: IverksettelseAvVedtakKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class MedlemMonoide(
    override val metadata: ProcessorMetadata,
    val dto: MedlemKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class InntekterMonoide(
    override val metadata: ProcessorMetadata,
    val dto: InntekterKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class AndreFolketrygdytelserMonoide(
    override val metadata: ProcessorMetadata,
    val dto: AndreFolketrygdytelserKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class SykepengedagerMonoide(
    override val metadata: ProcessorMetadata,
    val dto: SykepengedagerKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Innstilling_11_6Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Innstilling_11_6KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_2_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_2_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_3_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_3_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_4_ledd2_ledd3_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_4_ledd2_ledd3_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_5_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_5_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_6_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_6_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_19_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_19_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_11_29_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_11_29_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Løsning_22_13_manuellMonoide(
    override val metadata: ProcessorMetadata,
    val dto: Løsning_22_13_manuellKafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_2Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_2KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_3Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_3KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_4_ledd2_ledd3Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_4_ledd2_ledd3KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_5Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_5KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_6Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_6KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_19Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_19KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_11_29Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_11_29KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}

internal class Kvalitetssikring_22_13Monoide(
    override val metadata: ProcessorMetadata,
    val dto: Kvalitetssikring_22_13KafkaDto
) : Håndterbar {
    override fun håndter(søker: SøkerModellApi) = dto.håndter(søker)
}
