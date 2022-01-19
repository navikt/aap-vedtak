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
import java.util.*

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

    override fun serialize(topic: String?, data: T?): ByteArray? =
        data?.let {
            objectMapper.writeValueAsBytes(it).also {
                // Add the topic and the class to the deserialization topic
                if (topic != null) JsonDeserializer.addTopicMappingIfAbsent(topic, data::class.java)
            }
        }
}

class JsonDeserializer<T : Any>(private val clazz: Class<T>? = null) : Deserializer<T> {
    companion object {
        private val objectMapper: ObjectMapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule()) // java LocalDate
            configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        private val topicMapping = Properties()

        fun addTopicMappingIfAbsent(topic: String, clazz: Class<*>): Any? = topicMapping.putIfAbsent(topic, clazz.name)

        fun getTopicMapping(topic: String?): Class<*>? = topicMapping[topic]?.let {
            Thread.currentThread().contextClassLoader.loadClass(it as String?)
        }
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? = data?.let {
        when {
            clazz != null -> objectMapper.readValue(it, clazz)
            topic != null -> deserializeValueFromTopic(topic, it)
            else -> throw IllegalStateException("Missing topic and class information to deserialize data")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun deserializeValueFromTopic(topic: String?, data: ByteArray?): T? =
        when (val clazz = getTopicMapping(topic)) {
            null -> error("No mapping found for topic: $topic - could not deserialize")
            else -> objectMapper.readValue(data, clazz) as T?
        }
}