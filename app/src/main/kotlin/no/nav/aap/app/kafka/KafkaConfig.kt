package no.nav.aap.app.kafka

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
    @Deprecated("fjernes i kafka-streams implementasjonen")
    val topic: String,
)
