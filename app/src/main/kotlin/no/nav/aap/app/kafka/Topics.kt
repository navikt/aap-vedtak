package no.nav.aap.app.kafka

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.aap.app.modell.JsonSøknad
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.Produced
import no.nav.aap.avro.medlem.v1.Medlem as AvroMedlem
import no.nav.aap.avro.vedtak.v1.Søker as AvroSøker

data class Topic<K, V>(
    val name: String,
    val keySerde: Serde<K>,
    val valueSerde: Serde<V>,
)

class Topics(private val config: KafkaConfig) {
    val søknad = Topic("aap.aap-soknad-sendt.v1", Serdes.StringSerde(), jsonSerde<JsonSøknad>())
    val søkere = Topic("aap.sokere.v1", Serdes.StringSerde(), avroSerde<AvroSøker>())
    val medlem = Topic("aap.medlem.v1", Serdes.StringSerde(), avroSerde<AvroMedlem>())

    private inline fun <reified V : Any> jsonSerde(): Serde<V> = JsonSerde(V::class)

    private fun <T : SpecificRecord> avroSerde(): SpecificAvroSerde<T> = SpecificAvroSerde<T>().apply {
        configure(serdeConfig, false)
    }

    private val serdeConfig: Map<String, String>
        get() = mapOf(
            AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to config.schemaRegistryUrl,
            SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE to "USER_INFO",
            SchemaRegistryClientConfig.USER_INFO_CONFIG to "${config.schemaRegistryUser}:${config.schemaRegistryPwd}",
        )
}

fun <V : Any> Topic<String, V>.consumed(named: String): Consumed<String, V> =
    Consumed.with(keySerde, valueSerde).withName(named)

fun <V : Any> Topic<String, V>.produced(named: String): Produced<String, V> =
    Produced.with(keySerde, valueSerde).withName(named)

fun <L : Any, R : Any> Topic<String, L>.joined(right: Topic<String, R>): Joined<String, L, R> =
    Joined.with(keySerde, valueSerde, right.valueSerde, "$name-joined-${right.name}")
