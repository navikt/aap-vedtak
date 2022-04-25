package no.nav.aap.app.kafka

import no.nav.aap.kafka.streams.Table

internal const val SØKERE_STORE_NAME = "soker-state-store-v1"

class Tables(topics: Topics) {
    val søkere = Table("sokere", topics.søkere, false, SØKERE_STORE_NAME)
}