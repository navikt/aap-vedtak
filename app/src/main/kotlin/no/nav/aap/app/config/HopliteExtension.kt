package no.nav.aap.app.config

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.MapPropertySource
import com.sksamuel.hoplite.yaml.YamlParser
import io.ktor.application.*
import io.ktor.config.*

inline fun <reified T : Any> Application.loadConfig(vararg resources: String = arrayOf("/application.yml")) =
    ConfigLoader.Builder()
        .addFileExtensionMapping("yml", YamlParser())
        .addKtorConfig(environment.config)
        .build()
        .loadConfigOrThrow<T>(*resources)

/**
 * Add Ktors MapApplicationConfig as PropertySource,
 * this allows the MapApplicationConfig to override config values in tests
 */
fun ConfigLoader.Builder.addKtorConfig(config: ApplicationConfig) = apply {
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