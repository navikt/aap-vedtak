package no.nav.aap.app

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.modell.KafkaPersonident
import no.nav.aap.app.modell.KafkaSøknad
import no.nav.aap.domene.frontendView.FrontendSak
import no.nav.aap.domene.frontendView.FrontendVilkår
import no.nav.aap.domene.frontendView.FrontendVilkårsvurdering
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate

internal class ApiTest {
    companion object {
        private val objectMapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
        }

        inline fun <reified T> TestApplicationResponse.parseBody(): T {
            return content?.let { objectMapper.readValue(it) } ?: error("empty content")
        }
    }

    @Test
    fun `GET oppgaver returns 200 OK`() {
        withTestApp { mocks ->
            mocks.kafka.produce("aap.aap-soknad-sendt.v1", "11111111111") {
                KafkaSøknad(
                    ident = KafkaPersonident("FNR", "11111111111"),
                    fødselsdato = LocalDate.of(1990, 1, 1)
                )
            }

            with(handleRequest(HttpMethod.Get, "/api/saker") {
                val token = mocks.azureAdProvider.issueAzureToken()
                addHeader("Authorization", "Bearer ${token.serialize()}")
            }) {
                val expected = listOf(
                    FrontendSak(
                        personident = "11111111111",
                        fødselsdato = LocalDate.of(1990, 1, 1),
                        vilkårsvurdering = listOf(
                            FrontendVilkårsvurdering(
                                vilkår = FrontendVilkår("PARAGRAF_11_4", "LEDD_1"),
                                tilstand = "OPPFYLT"
                            )
                        )
                    )
                )
                assertEquals(response.status(), HttpStatusCode.OK)
                assertEquals(expected, response.parseBody<List<FrontendSak>>())
            }
        }
    }
}

fun <R> withTestApp(test: TestApplicationEngine.(mocks: Mocks) -> R): R = Mocks().use { mocks ->
    val externalConfig = mapOf(
        "AZURE_OPENID_CONFIG_ISSUER" to "azure",
        "AZURE_APP_WELL_KNOWN_URL" to mocks.azureAdProvider.wellKnownUrl(),
        "AZURE_APP_CLIENT_ID" to "vedtak",
        "KAFKA_BROKERS" to "kafka-mock",
        "KAFKA_TRUSTSTORE_PATH" to "",
        "KAFKA_SECURITY_ENABLED" to "false",
        "KAFKA_KEYSTORE_PATH" to "",
        "KAFKA_CREDSTORE_PASSWORD" to "",
        "KAFKA_CLIENT_ID" to "vedtak",
        "KAFKA_GROUP_ID" to "vedtak-1"
    )

    EnvironmentVariables(externalConfig).execute<R> {
        withTestApplication(
            {
                val config: Config = loadConfig()
                val kafkaConsumer = mocks.kafka.createTestConsumer<KafkaSøknad>(config.kafka)
                server(config, kafkaConsumer)
            },
            { test(mocks) }
        )
    }
}
