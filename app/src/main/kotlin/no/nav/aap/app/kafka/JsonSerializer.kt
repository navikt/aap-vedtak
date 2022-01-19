package no.nav.aap.app.kafka

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class JsonSerializer<T : Any> : Serializer<T> {
    private companion object {
        private val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule()) // LocalDate
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

class JsonDeserializer<T : Any>(private val clazz: Class<T>) : Deserializer<T> {
    companion object {
        private val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule()) // java LocalDate
            configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? = data?.let {
        when {
            topic != null -> objectMapper.readValue(it, clazz)
            else -> throw IllegalStateException("Missing topic and class information to deserialize data")
        }
    }
}
