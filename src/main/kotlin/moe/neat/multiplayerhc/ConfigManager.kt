package moe.neat.multiplayerhc

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

/**
 * Manages the plugin's config.properties file.
 * Uses .properties format for simplicity.
 *
 * Can be used in two modes:
 * - Standalone (from bootstrapper): constructed with an explicit config path
 * - Plugin mode: constructed with the plugin's data folder
 */
class ConfigManager(private val configDir: Path) {

    private val configFile: Path = configDir.resolve(CONFIG_FILE_NAME)
    private val props = Properties()

    init {
        if (!Files.exists(configDir)) {
            Files.createDirectories(configDir)
        }

        if (!Files.exists(configFile)) {
            val defaults = ConfigManager::class.java.classLoader.getResourceAsStream(CONFIG_FILE_NAME)
            if (defaults != null) {
                defaults.use { Files.copy(it, configFile) }
            } else {
                Files.createFile(configFile)
            }
        }

        Files.newInputStream(configFile).use { props.load(it) }
    }

    fun read(key: String): String? = props.getProperty(key)

    @Throws(IOException::class)
    fun save(key: String, value: String) {
        props.setProperty(key, value)
        Files.newOutputStream(configFile).use { props.store(it, null) }
    }

    companion object {
        private const val CONFIG_FILE_NAME = "config.properties"
    }
}
