package no.nav.aap.app.kafka

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore

inline fun <reified V : Any> consumedWithJson(named: String): Consumed<String, V> =
    Consumed.with(Serdes.StringSerde(), JsonSerde(V::class)).withName(named)

inline fun <reified LEFT : Any, reified RIGHT : SpecificRecord> joinedWithJsonOnAvro(named: String): Joined<String, LEFT, RIGHT> =
    Joined.with(Serdes.StringSerde(), JsonSerde(LEFT::class), SpecificAvroSerde(), named)

fun <K, T> ReadOnlyKeyValueStore<K, T>.getAllValues(): List<T> =
    all().use { iterator -> iterator.asSequence().map(KeyValue<K, T>::value).toList() }
