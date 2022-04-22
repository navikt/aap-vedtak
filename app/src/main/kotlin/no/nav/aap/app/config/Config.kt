package no.nav.aap.app.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.MapPropertySource
import com.sksamuel.hoplite.yaml.YamlParser
import io.ktor.server.application.*
import io.ktor.server.config.*
import no.nav.aap.app.kafka.KafkaConfig

data class Config(
    val kafka: KafkaConfig
)

inline fun <reified T : Any> Application.loadConfig(vararg resources: String = arrayOf("/application.yml")): T =
    ConfigLoader.builder()
        .addFileExtensionMapping("yml", YamlParser())
        .addKtorConfig(environment.config)
        .build()
        .loadConfigOrThrow(*resources)

inline fun <reified T : Any> loadConfig(resource: String = "/application.yml"): T =
    ConfigLoader.builder()
        .addFileExtensionMapping("yml", YamlParser())
        .build()
        .loadConfigOrThrow(resource)

/**
 * Add Ktors MapApplicationConfig as PropertySource,
 * this allows the MapApplicationConfig to override config values in tests
 */
fun ConfigLoaderBuilder.addKtorConfig(config: ApplicationConfig) = apply {
    if (config is MapApplicationConfig) {
        // get access to the protected property 'map'
        @Suppress("UNCHECKED_CAST")
        val map = config.javaClass.getDeclaredField("map").let {
            it.isAccessible = true
            it.get(config) as Map<String, String>
        }

        addPropertySource(MapPropertySource(map))
    }
}