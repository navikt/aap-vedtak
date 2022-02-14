package no.nav.aap.app.kafka.json

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import kotlin.reflect.KClass

class JsonSerde<V : Any>(private val kclass: KClass<V>) : Serde<V> {
    override fun serializer(): Serializer<V> = JsonSerializer()
    override fun deserializer(): Deserializer<V> = JsonDeserializer(kclass)
}

class JsonSerializer<T : Any> : Serializer<T> {
    private companion object {
        private val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }

    override fun serialize(topic: String?, data: T?): ByteArray? = data?.let {
        objectMapper.writeValueAsBytes(it)
    }
}

class JsonDeserializer<T : Any>(private val kclass: KClass<T>) : Deserializer<T> {
    private companion object {
        private val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? = objectMapper.readValue(data, kclass.java)
}
