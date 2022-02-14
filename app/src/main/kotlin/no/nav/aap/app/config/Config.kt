package no.nav.aap.app.config

import no.nav.aap.app.security.IssuerConfig

data class Config(
    val oauth: OAuthConfig,
    val kafka: KafkaConfig,
)

data class OAuthConfig(
    val azure: IssuerConfig,
)

data class KafkaConfig(
    val brokers: String,
    val groupId: String,
    val clientId: String,
    val security: Boolean,
    val truststorePath: String,
    val keystorePath: String,
    val credstorePsw: String,
    val schemaRegistryUrl: String,
    val schemaRegistryUser: String,
    val schemaRegistryPwd: String,
)