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
import no.nav.aap.avro.vedtak.v1.Soker as AvroSøker
import no.nav.aap.avro.vedtak.v1.Losning as AvroLøsning
import no.nav.aap.avro.vedtak.v1.VurderingAvBeregningsdato as AvroVurderingAvBeregningsdato
import no.nav.aap.avro.vedtak.v1.Inntekter as AvroInntekter

data class Topic<K, V>(
    val name: String,
    val keySerde: Serde<K>,
    val valueSerde: Serde<V>,
)

class Topics(private val config: KafkaConfig) {
    val søknad = Topic("aap.aap-soknad-sendt.v1", Serdes.StringSerde(), jsonSerde<JsonSøknad>())
    val søkere = Topic("aap.sokere.v1", Serdes.StringSerde(), avroSerde<AvroSøker>())
    val medlem = Topic("aap.medlem.v1", Serdes.StringSerde(), avroSerde<AvroMedlem>())
    val løsning = Topic("aap.losning.v1", Serdes.StringSerde(), avroSerde<AvroLøsning>())
    val vurderingAvBeregningsdato = Topic("aap.vurdering-av-beregningsdato.v1", Serdes.StringSerde(), avroSerde<AvroVurderingAvBeregningsdato>())
    val inntekter = Topic("aap.inntekter.v1", Serdes.StringSerde(), avroSerde<AvroInntekter>())

    private inline fun <reified V : Any> jsonSerde(): Serde<V> = JsonSerde(V::class)

    private fun <T : SpecificRecord> avroSerde(): SpecificAvroSerde<T> = SpecificAvroSerde<T>().apply {
        val conf = (config.ssl + config.schemaRegistry).map { it.key.toString() to it.value.toString() }
        configure(conf.toMap(), false)
    }
}

fun <V : Any> Topic<String, V>.consumed(named: String): Consumed<String, V> =
    Consumed.with(keySerde, valueSerde).withName(named)

fun <V : Any> Topic<String, V>.produced(named: String): Produced<String, V> =
    Produced.with(keySerde, valueSerde).withName(named)

fun <L : Any, R : Any> Topic<String, L>.joined(right: Topic<String, R>): Joined<String, L, R> =
    Joined.with(keySerde, valueSerde, right.valueSerde, "$name-joined-${right.name}")
