package no.nav.aap.app.kafka

import no.nav.aap.app.modell.JsonSøknad
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.serde.avro.AvroSerde
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.avro.inntekter.v1.Inntekter as AvroInntekter
import no.nav.aap.avro.manuell.v1.Manuell as AvroManuell
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker

class Topics(config: KafkaConfig) {
    val søknad = Topic("aap.aap-soknad-sendt.v1", JsonSerde.jackson<JsonSøknad>())
    val søkere = Topic("aap.sokere.v1", AvroSerde.specific<AvroSøker>(config))
    val medlem = Topic("aap.medlem.v1", AvroSerde.specific<AvroMedlem>(config))
    val manuell = Topic("aap.manuell.v1", AvroSerde.specific<AvroManuell>(config))
    val inntekter = Topic("aap.inntekter.v1", AvroSerde.specific<AvroInntekter>(config))
}
