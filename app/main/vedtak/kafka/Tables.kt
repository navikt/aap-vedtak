package no.nav.aap.app.kafka

import no.nav.aap.kafka.streams.Table

internal const val SØKERE_STORE_NAME = "soker-state-store-v2"

object Tables {
    val søkere = Table("sokere", Topics.søkere, false, SØKERE_STORE_NAME)
}
