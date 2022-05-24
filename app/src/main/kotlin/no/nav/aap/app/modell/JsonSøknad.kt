package no.nav.aap.app.modell

import java.time.LocalDate
import kotlin.random.Random

// se https://github.com/navikt/aap-soknad-api/blob/1b4563e81b418be7e9a39863278b607f685b9cee/src/main/kotlin/no/nav/aap/api/s%C3%B8knad/model/StandardS%C3%B8knad.kt
data class JsonSøknad(
    val fødselsdato: LocalDate = LocalDate.now().minusYears(Random.nextLong(0, 100))
)
