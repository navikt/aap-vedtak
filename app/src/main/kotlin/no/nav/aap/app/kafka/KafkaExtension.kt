package no.nav.aap.app.kafka

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import no.nav.aap.kafka.streams.KStreams
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("app")

fun <V> KStreams.waitForStore(name: String): ReadOnlyKeyValueStore<String, V> = runBlocking {
    log.info("Waiting 10_000 ms for store $name to become available")
    val store = withTimeout(10_000L) {
        flow {
            while (true) {
                runCatching { getStore<V>(name) }.getOrNull()?.let { emit(it) }
                delay(100)
            }
        }.firstOrNull()
    }

    store ?: error("state store not awailable after 10s")
}


internal fun <K, V, VR> KStream<K, V>.mapValues(named: Named, mapper: (V) -> VR) = mapValues(mapper, named)
internal fun <K, V, VR> KStream<K, V>.flatMapValues(named: Named, mapper: (V) -> Iterable<VR>) =
    flatMapValues(mapper, named)
