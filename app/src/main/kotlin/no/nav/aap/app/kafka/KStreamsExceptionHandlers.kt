package no.nav.aap.app.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.streams.errors.DeserializationExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler
import org.apache.kafka.streams.errors.ProductionExceptionHandler.ProductionExceptionHandlerResponse.CONTINUE
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT
import org.apache.kafka.streams.processor.ProcessorContext
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

/**
 * Entry point exception handler (consuming records)
 *
 * Exceptions during deserialization, networks issues etc.
 */
class EntryPointExceptionHandler : DeserializationExceptionHandler {
    override fun configure(configs: MutableMap<String, *>) {}
    override fun handle(context: ProcessorContext, record: ConsumerRecord<ByteArray, ByteArray>, exception: Exception) =
        DeserializationExceptionHandler.DeserializationHandlerResponse.CONTINUE.also {
            secureLog.warn(
                "Exception reading from kafka: taskId: {}, topic: {}, partition: {}, offset: {}",
                context.taskId(), record.topic(), record.partition(), record.offset(), exception
            )
        }
}

/**
 * Processing exception handling (process records in the user code)
 *
 * Exceptions not handled by Kafka Streams
 * Three options:
 *  1. replace thread
 *  2. shutdown indicidual stream instance
 *  3. shutdown all streams instances (with the same application-id
 */
class ProcessingExceptionHandler : StreamsUncaughtExceptionHandler {
    override fun handle(exception: Throwable) = when (exception) {
        is IllegalStateException -> logAndReplaceThread(exception)
        else -> logAndShutdownClient(exception)
    }

    private fun logAndReplaceThread(err: RuntimeException) =
        REPLACE_THREAD.also { secureLog.error("Uventet feil, logger og leser neste record, ${err.message}") }

    private fun logAndShutdownClient(err: Throwable) =
        SHUTDOWN_CLIENT.also { secureLog.error("Uventet feil, logger og avslutter client, ${err.message}") }
}

/**
 * Exit point exception handler (producing records)
 *
 * Exceptions due to serialization, networking etc.
 */
class ExitPointExceptionHandler : ProductionExceptionHandler {
    override fun configure(configs: MutableMap<String, *>) {}
    override fun handle(record: ProducerRecord<ByteArray, ByteArray>, exception: Exception) =
        CONTINUE.also { secureLog.error("Feil i streams, logger og leser neste record", exception) }
}
