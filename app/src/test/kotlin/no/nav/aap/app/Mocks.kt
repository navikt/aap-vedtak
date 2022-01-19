package no.nav.aap.app

import com.nimbusds.jwt.SignedJWT
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

class Mocks : AutoCloseable {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    val azureAdProvider = AzureMock().also { log.info("Azure OAuth provider: ${it.wellKnownUrl()}") }

    override fun close() {
        azureAdProvider.close()
    }
}

class AzureMock : AutoCloseable {
    private val server = MockOAuth2Server().apply { start(8082) }
    fun wellKnownUrl() = server.wellKnownUrl("azure")
    fun issueAzureToken(): SignedJWT = server.issueToken(issuerId = "azure", audience = "apparat")
    override fun close() = server.shutdown()
}
