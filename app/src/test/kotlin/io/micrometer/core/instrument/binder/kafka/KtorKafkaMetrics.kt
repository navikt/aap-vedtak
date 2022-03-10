package io.micrometer.core.instrument.binder.kafka

import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.common.Metric
import org.apache.kafka.common.MetricName


/**
 * KafkaMetrics is package private.
 * KafkaStreams and TestTopologyDriver does not share interface,
 * this is a workaround for testing kafka streams metrics
 */
class KtorKafkaMetrics(
    registry: MeterRegistry,
    metrics: () -> Map<MetricName, Metric>,
) {
    init {
        val binder = KafkaMetrics(metrics)
        binder.bindTo(registry)
    }
}
