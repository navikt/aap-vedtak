package no.nav.aap.app

import com.nimbusds.jwt.SignedJWT
import no.nav.aap.kafka.KafkaMock
import no.nav.security.mock.oauth2.MockOAuth2Server

class Mocks : AutoCloseable {
    val azureAdProvider = AzureMock().apply { start() }
    val kafka = KafkaMock()

    override fun close() {
        azureAdProvider.close()
    }
}

class AzureMock(private val server: MockOAuth2Server = MockOAuth2Server()) {
    fun wellKnownUrl(): String = server.wellKnownUrl("azure").toString()
    fun issueAzureToken(): SignedJWT = server.issueToken(issuerId = "azure", audience = "vedtak")
    fun start() = server.start()
    fun close() = server.shutdown()
}
