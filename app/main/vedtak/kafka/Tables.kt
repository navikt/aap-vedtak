package vedtak.kafka

import no.nav.aap.kafka.streams.v2.Table

object Tables {
    val søkere = Table(Topics.søkere, stateStoreName = "soker-state-store-v2")
}
