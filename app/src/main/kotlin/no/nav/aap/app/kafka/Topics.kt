package no.nav.aap.app.kafka

import no.nav.aap.app.modell.*
import no.nav.aap.kafka.serde.avro.AvroSerde
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem

object Topics {
    val søknad = Topic("aap.soknad-sendt.v1", JsonSerde.jackson<JsonSøknad>())
    val søkere = Topic("aap.sokere.v1", JsonSerde.jackson(SøkereKafkaDto.VERSION, ForrigeSøkereKafkaDto::toDto))
    val medlem = Topic("aap.medlem.v1", AvroSerde.specific<AvroMedlem>())
    val inntekter = Topic("aap.inntekter.v1", JsonSerde.jackson<InntekterKafkaDto>())
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<IverksettVedtakKafkaDto>())
    val manuell_11_2 = Topic("aap.manuell.11-2.v1", JsonSerde.jackson<Løsning_11_2_manuell>())
    val manuell_11_3 = Topic("aap.manuell.11-3.v1", JsonSerde.jackson<Løsning_11_3_manuell>())
    val manuell_11_4 = Topic("aap.manuell.11-4.v1", JsonSerde.jackson<Løsning_11_4_ledd2_ledd3_manuell>())
    val manuell_11_5 = Topic("aap.manuell.11-5.v1", JsonSerde.jackson<Løsning_11_5_manuell>())
    val manuell_11_6 = Topic("aap.manuell.11-6.v1", JsonSerde.jackson<Løsning_11_6_manuell>())
    val manuell_11_12 = Topic("aap.manuell.11-12.v1", JsonSerde.jackson<Løsning_11_12_ledd1_manuell>())
    val manuell_11_19 = Topic("aap.manuell.11-19.v1", JsonSerde.jackson<Løsning_11_19_manuell>())
    val manuell_11_29 = Topic("aap.manuell.11-29.v1", JsonSerde.jackson<Løsning_11_29_manuell>())

    val kvalitetssikring_11_2 = Topic("aap.kvalitetssikring.11-2.v1", JsonSerde.jackson<Kvalitetssikring_11_2>())
    val kvalitetssikring_11_3 = Topic("aap.kvalitetssikring.11-3.v1", JsonSerde.jackson<Kvalitetssikring_11_3>())
    val kvalitetssikring_11_4 = Topic("aap.kvalitetssikring.11-4.v1", JsonSerde.jackson<Kvalitetssikring_11_4_ledd2_ledd3>())
    val kvalitetssikring_11_5 = Topic("aap.kvalitetssikring.11-5.v1", JsonSerde.jackson<Kvalitetssikring_11_5>())
    val kvalitetssikring_11_6 = Topic("aap.kvalitetssikring.11-6.v1", JsonSerde.jackson<Kvalitetssikring_11_6>())
    val kvalitetssikring_11_12 = Topic("aap.kvalitetssikring.11-12.v1", JsonSerde.jackson<Kvalitetssikring_11_12_ledd1>())
    val kvalitetssikring_11_19 = Topic("aap.kvalitetssikring.11-19.v1", JsonSerde.jackson<Kvalitetssikring_11_19>())
    val kvalitetssikring_11_29 = Topic("aap.kvalitetssikring.11-29.v1", JsonSerde.jackson<Kvalitetssikring_11_29>())
}
