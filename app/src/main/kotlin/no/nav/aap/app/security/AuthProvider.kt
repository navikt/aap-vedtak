package no.nav.aap.app.security

import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.ktor.RequiredClaims

/**
 * This interface is common for Azure AD, Maskinporten, etc. or for generating tokens as a client.
 * to easier integration of several auth-providers when needed.
 */
interface AuthProvider {
    fun authIdentity(claims: JwtTokenClaims): AapAuth.AuthIdentity

    val issuer: String
    val config: IssuerConfig
    val realms: List<Realm>

    class Realm(
        val name: String,
        val requiredClaims: RequiredClaims? = null,
        val additionalValidation: ((TokenValidationContext) -> Boolean)? = null
    )
}
