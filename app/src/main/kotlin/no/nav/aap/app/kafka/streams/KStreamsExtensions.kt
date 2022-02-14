package no.nav.aap.app.kafka.streams

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.aap.app.kafka.json.JsonSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.KeyValueStore

inline fun <reified V : Any> materializedAsJson(name: String): Materialized<String, V, KeyValueStore<Bytes, ByteArray>> =
    Materialized.`as`<String, V, KeyValueStore<Bytes, ByteArray>>(name)
        .withKeySerde(Serdes.StringSerde())
        .withValueSerde(JsonSerde(V::class))

inline fun <reified V : Any> consumedWithJson(named: String): Consumed<String, V> =
    Consumed.with(Serdes.StringSerde(), JsonSerde(V::class)).withName(named)

inline fun <reified LEFT : Any, reified RIGHT : Any> joinedWithJson(named: String): Joined<String, LEFT, RIGHT> =
    Joined.with(
        Serdes.StringSerde(),
        JsonSerde(LEFT::class),
        JsonSerde(RIGHT::class),
        named,
    )

inline fun <reified LEFT : Any, reified RIGHT : SpecificRecord> joinedWithJsonOnAvro(named: String): Joined<String, LEFT, RIGHT> =
    Joined.with(
        Serdes.StringSerde(),
        JsonSerde(LEFT::class),
        SpecificAvroSerde(),
        named,
    )

inline fun <reified V : Any> producedWithJson(named: String): Produced<String, V> =
    Produced.with(Serdes.StringSerde(), JsonSerde(V::class)).withName(named)
