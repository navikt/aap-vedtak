package vedtak.kafka

import no.nav.aap.app.kafka.toDto
import no.nav.aap.dto.kafka.*
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topic
import no.nav.aap.kafka.streams.v2.concurrency.RaceConditionBuffer
import no.nav.aap.kafka.streams.v2.serde.ByteArraySerde
import no.nav.aap.kafka.streams.v2.serde.JsonSerde
import no.nav.aap.kafka.streams.v2.serde.StringSerde

internal object Topics {
    val søknad = Topic("aap.soknad-sendt.v1", JsonSerde.jackson<SøknadKafkaDto>())
    val søkere = Topic(
        name = "aap.sokere.v1",
        valueSerde = JsonSerde.jackson(
            dtoVersion = SøkereKafkaDto.VERSION,
            migrate = ForrigeSøkereKafkaDtoHistorikk::toDto
        ) { json -> json.get("søkereKafkaDto")?.get("version")?.takeIf { it.isNumber }?.intValue() },
    )
    val monade = Topic("aap.monade.v1", JsonSerde.jackson<MonadeKafkaDto>())

    val medlem = Topic("aap.medlem.v1", JsonSerde.jackson<MedlemKafkaDto>())
    val inntekter = Topic("aap.inntekter.v1", JsonSerde.jackson<InntekterKafkaDto>())
    val andreFolketrygdsytelser = Topic("aap.andre-folketrygdytelser.v1", JsonSerde.jackson<AndreFolketrygdytelserKafkaDto>())
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<IverksettVedtakKafkaDto>())
    val sykepengedager = Topic("aap.sykepengedager.v1", JsonSerde.jackson<SykepengedagerKafkaDto>())
    val subscribeSykepengedager = Topic("aap.subscribe-sykepengedager.v1", ByteArraySerde)

    val iverksettelseAvVedtak = Topic("aap.iverksettelse-av-vedtak.v1", JsonSerde.jackson<IverksettelseAvVedtakKafkaDto>())
    val endredePersonidenter = Topic("aap.endrede-personidenter.v1", StringSerde)

    val innstilling_11_6 = Topic("aap.innstilling.11-6.v1", JsonSerde.jackson<Innstilling_11_6KafkaDto>())

    val manuell_11_2 = Topic("aap.manuell.11-2.v1", JsonSerde.jackson<Løsning_11_2_manuellKafkaDto>())
    val manuell_11_3 = Topic("aap.manuell.11-3.v1", JsonSerde.jackson<Løsning_11_3_manuellKafkaDto>())
    val manuell_11_4 = Topic("aap.manuell.11-4.v1", JsonSerde.jackson<Løsning_11_4_ledd2_ledd3_manuellKafkaDto>())
    val manuell_11_5 = Topic("aap.manuell.11-5.v1", JsonSerde.jackson<Løsning_11_5_manuellKafkaDto>())
    val manuell_11_6 = Topic("aap.manuell.11-6.v1", JsonSerde.jackson<Løsning_11_6_manuellKafkaDto>())
    val manuell_11_19 = Topic("aap.manuell.11-19.v1", JsonSerde.jackson<Løsning_11_19_manuellKafkaDto>())
    val manuell_11_29 = Topic("aap.manuell.11-29.v1", JsonSerde.jackson<Løsning_11_29_manuellKafkaDto>())
    val manuell_22_13 = Topic("aap.manuell.22-13.v1", JsonSerde.jackson<Løsning_22_13_manuellKafkaDto>())

    val kvalitetssikring_11_2 = Topic("aap.kvalitetssikring.11-2.v1", JsonSerde.jackson<Kvalitetssikring_11_2KafkaDto>())
    val kvalitetssikring_11_3 = Topic("aap.kvalitetssikring.11-3.v1", JsonSerde.jackson<Kvalitetssikring_11_3KafkaDto>())
    val kvalitetssikring_11_4 = Topic("aap.kvalitetssikring.11-4.v1", JsonSerde.jackson<Kvalitetssikring_11_4_ledd2_ledd3KafkaDto>())
    val kvalitetssikring_11_5 = Topic("aap.kvalitetssikring.11-5.v1", JsonSerde.jackson<Kvalitetssikring_11_5KafkaDto>())
    val kvalitetssikring_11_6 = Topic("aap.kvalitetssikring.11-6.v1", JsonSerde.jackson<Kvalitetssikring_11_6KafkaDto>())
    val kvalitetssikring_11_19 = Topic("aap.kvalitetssikring.11-19.v1", JsonSerde.jackson<Kvalitetssikring_11_19KafkaDto>())
    val kvalitetssikring_11_29 = Topic("aap.kvalitetssikring.11-29.v1", JsonSerde.jackson<Kvalitetssikring_11_29KafkaDto>())
    val kvalitetssikring_22_13 = Topic("aap.kvalitetssikring.22-13.v1", JsonSerde.jackson<Kvalitetssikring_22_13KafkaDto>())
}

internal val KTable<SøkereKafkaDtoHistorikk>.buffer by lazy {
    RaceConditionBuffer<SøkereKafkaDtoHistorikk>(logRecordValues = true)
}
