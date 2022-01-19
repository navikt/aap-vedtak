package no.nav.aap.app

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.aap.app.modell.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal class ApiTest {
    companion object {
        private val objectMapper = jacksonObjectMapper()

        inline fun <reified T> TestApplicationResponse.parseBody(): T {
            return content?.let { objectMapper.readValue(it) } ?: error("empty content")
        }
    }

    @Test
    fun `GET oppgaver returns 200 OK`() {
        withTestApp { mocks ->
            val record = produceKafkaTestRecords(mocks)

            with(handleRequest(HttpMethod.Get, "/api/oppgaver") {
                val token = mocks.azureAdProvider.issueAzureToken()
                addHeader("Authorization", "Bearer ${token.serialize()}")
            }) {
                val expected = Oppgaver(
                    listOf(
                        Oppgave(
                            oppgaveId = record.hashCode(),
                            personident = Personident("11111111111"),
                            alder = record.fødselsdato.until(LocalDate.now(), ChronoUnit.YEARS).toInt()
                        )
                    )
                )
                assertEquals(response.status(), HttpStatusCode.OK)
                assertEquals(expected, response.parseBody<Oppgaver>())
            }

        }
    }

    @Test
    fun `POST vurderAlder returns 202 ACCEPTED`() {
        withTestApp { mocks ->
            with(
                handleRequest(HttpMethod.Post, "/api/vurderAlder") {
                    val token = mocks.azureAdProvider.issueAzureToken()
                    addHeader("Authorization", "Bearer ${token.serialize()}")
                    addHeader("Content-Type", "application/json")
                    setBody(resourceFile("/vurder-alder-ok.json"))
                }
            ) {
                assertEquals(response.status(), HttpStatusCode.Accepted)
            }
        }
    }

    private fun produceKafkaTestRecords(mocks: Mocks): KafkaSøknad {
        val record = KafkaSøknad(
            KafkaPersonident("FNR", "11111111111"),
            LocalDate.of(1990, 1, 1)
        )
        mocks.kafka.produce("aap.aap-soknad-sendt.v1", "11111111111", record)
        runCatching { Thread.sleep(10_000L) }
        return record
    }
}

fun <R> withTestApp(test: TestApplicationEngine.(mocks: Mocks) -> R): R = Mocks().use { mocks ->

    // Environment variables populated by nais
    val externalConfig = mapOf(
        "AZURE_OPENID_CONFIG_ISSUER" to "azure",
        "AZURE_APP_WELL_KNOWN_URL" to mocks.azureAdProvider.wellKnownUrl(),
        "AZURE_APP_CLIENT_ID" to "apparat",
        "KAFKA_BROKERS" to mocks.kafka.brokersURL,
        "KAFKA_TRUSTSTORE_PATH" to "",
        "KAFKA_SECURITY_ENABLED" to "false",
        "KAFKA_KEYSTORE_PATH" to "",
        "KAFKA_CREDSTORE_PASSWORD" to "",
        "KAFKA_CLIENT_ID" to "apparat",
        "KAFKA_GROUP_ID" to "apparat-1"
    )

    // Wrap test with external environment variables
    EnvironmentVariables(externalConfig).execute<R> {
        withTestApplication(Application::server) {
            test(mocks)
        }
    }
}
