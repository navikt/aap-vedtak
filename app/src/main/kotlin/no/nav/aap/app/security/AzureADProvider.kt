package no.nav.aap.app.security

import no.nav.security.token.support.core.jwt.JwtTokenClaims
import java.net.URL

class AzureADProvider(private val issuerConfig: IssuerConfig) : AuthProvider {
    companion object {
        const val REALM = "AzureAD"
    }

    override fun authIdentity(claims: JwtTokenClaims): AapAuth.AuthIdentity = AzureADIdentity()

    override val issuer: String get() = issuerConfig.name
    override val config: IssuerConfig get() = issuerConfig
    override val realms: List<AuthProvider.Realm> get() = listOf(AuthProvider.Realm(REALM))

    class AzureADIdentity : AapAuth.AuthIdentity
}

data class IssuerConfig(
    val name: String,
    val discoveryUrl: URL,
    val audience: String,
    val optionalClaims: String?
)
