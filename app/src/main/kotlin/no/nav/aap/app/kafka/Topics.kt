package no.nav.aap.app.kafka

import no.nav.aap.app.modell.*
import no.nav.aap.kafka.serde.avro.AvroSerde
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem

object Topics {
    val søknad = Topic("aap.soknad-sendt.v1", JsonSerde.jackson<JsonSøknad>())
    val søkere = Topic("aap.sokere.v1", JsonSerde.jackson(1, ForrigeSøkereKafkaDto::toDto))
    val medlem = Topic("aap.medlem.v1", AvroSerde.specific<AvroMedlem>())
    val manuell = Topic("aap.manuell.v1", JsonSerde.jackson<ManuellKafkaDto>())
    val inntekter = Topic("aap.inntekter.v1", JsonSerde.jackson<InntekterKafkaDto>())
}
