package no.nav.aap.app.kafka

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.aap.app.modell.JsonSøknad
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.Produced
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.inntekter.v1.Inntekter as AvroInntekter
import no.nav.aap.avro.sokere.v1.Soker as AvroSøker
import no.nav.aap.avro.manuell.v1.Manuell as AvroManuell

data class Topic<K, V>(
    val name: String,
    val keySerde: Serde<K>,
    val valueSerde: Serde<V>,
) {
    fun consumed(named: String): Consumed<K, V> =
        Consumed.with(keySerde, valueSerde).withName(named)

    fun produced(named: String): Produced<K, V> =
        Produced.with(keySerde, valueSerde).withName(named)

    fun <R : Any> joined(right: Topic<K, R>): Joined<K, V, R> =
        Joined.with(keySerde, valueSerde, right.valueSerde, "$name-joined-${right.name}")
}

class Topics(private val config: KafkaConfig) {
    val søknad = Topic("aap.aap-soknad-sendt.v1", Serdes.StringSerde(), jsonSerde<JsonSøknad>())
    val søkere = Topic("aap.sokere.v1", Serdes.StringSerde(), avroSerde<AvroSøker>())
    val medlem = Topic("aap.medlem.v1", Serdes.StringSerde(), avroSerde<AvroMedlem>())
    val manuell = Topic("aap.manuell.v1", Serdes.StringSerde(), avroSerde<AvroManuell>())
    val inntekter = Topic("aap.inntekter.v1", Serdes.StringSerde(), avroSerde<AvroInntekter>())

    private inline fun <reified V : Any> jsonSerde(): Serde<V> = JsonSerde(V::class)

    private fun <T : SpecificRecord> avroSerde(): SpecificAvroSerde<T> = SpecificAvroSerde<T>().apply {
        val conf = (config.ssl + config.schemaRegistry).map { it.key.toString() to it.value.toString() }
        configure(conf.toMap(), false)
    }
}
