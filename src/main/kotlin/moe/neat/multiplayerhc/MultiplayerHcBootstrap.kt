package moe.neat.multiplayerhc

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import java.nio.file.Files
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Paper bootstrapper that runs before worlds are loaded.
 * If a world reset is pending, backs up and deletes world folders
 * so the server generates fresh worlds on startup.
 */
@Suppress("unused", "UnstableApiUsage")
class MultiplayerHcBootstrap : PluginBootstrap {

    private val logger = Logger.getLogger("MultiplayerHc")

    override fun bootstrap(context: BootstrapContext) {
        val configDir = Path.of("plugins", "MultiplayerHc")
        val config = ConfigManager(configDir)

        if (config.read("resetWorld") == "true") {
            logger.info("World reset flag detected — backing up and deleting worlds")
            val serverRoot = Path.of("").toAbsolutePath()
            val backupDir = serverRoot.resolve(BACKUP_FOLDER)

            val timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(OffsetDateTime.now())
            val backupTarget = backupDir.resolve(timestamp)
            Files.createDirectories(backupTarget)

            for (worldName in WORLD_FOLDERS) {
                val worldPath = serverRoot.resolve(worldName)
                if (Files.exists(worldPath)) {
                    moveDirectory(worldPath, backupTarget.resolve(worldName))
                    logger.info("Backed up $worldName")
                }
            }

            config.save("resetWorld", "false")
            logger.info("World reset complete — fresh worlds will be generated")
        }
    }

    private fun moveDirectory(source: Path, target: Path) {
        try {
            Files.move(source, target)
        } catch (e: Exception) {
            // Files.move can fail across filesystems; fall back to copy+delete
            logger.log(Level.WARNING, "move failed for $source, falling back to copy", e)
            source.toFile().copyRecursively(target.toFile(), overwrite = true)
            source.toFile().deleteRecursively()
        }
    }

    companion object {
        private val WORLD_FOLDERS = listOf("world", "world_nether", "world_the_end")
        private const val BACKUP_FOLDER = "world-backups"
    }
}
