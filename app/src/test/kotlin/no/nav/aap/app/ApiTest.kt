package no.nav.aap.app

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.aap.app.modell.Oppgave
import no.nav.aap.app.modell.Oppgaver
import no.nav.aap.app.modell.Personident
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
            with(handleRequest(HttpMethod.Get, "/api/oppgaver") {
                val token = mocks.azureAdProvider.issueAzureToken()
                addHeader("Authorization", "Bearer ${token.serialize()}")
            }) {
                val expected = Oppgaver(
                    listOf(
                        Oppgave(1, Personident("11111111111"), 68),
                        Oppgave(2, Personident("12345678910"), 58),
                        Oppgave(3, Personident("01987654321"), 17),
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
}

fun <R> withTestApp(test: TestApplicationEngine.(mocks: Mocks) -> R): R =
    Mocks().use { mocks ->
        withTestApplication(
            moduleFunction = { server("/") },
            test = { test(mocks) },
        )
    }
