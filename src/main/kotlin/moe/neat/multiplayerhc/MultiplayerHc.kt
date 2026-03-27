package moe.neat.multiplayerhc

import moe.neat.multiplayerhc.commands.VoteEnd
import moe.neat.multiplayerhc.event.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager

class MultiplayerHc : JavaPlugin() {

    lateinit var configManager: ConfigManager
        private set

    private var shutdownRemaining = 0
    private var shutdownTotal = 0

    override fun onEnable() {
        configManager = ConfigManager(dataFolder.toPath())

        logger.info("Registering event listeners")
        registerListeners()

        logger.info("Setting up commands")
        setupCommands()

        logger.info("MultiplayerHC is ready to go!")
    }

    private fun registerListeners() {
        val pm = server.pluginManager
        pm.registerEvents(PlayerDeathListener(this), this)
        pm.registerEvents(PlayerJoinListener(), this)
        pm.registerEvents(PlayerQuitListener(this), this)
        pm.registerEvents(EntityDamageEventListener(this), this)
        pm.registerEvents(ServerListPingListener(this), this)
    }

    @Suppress("UnstableApiUsage")
    private fun setupCommands() {
        val commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this)

        VoteEnd(this).register(commandManager)
    }

    fun shutDownServer(secondsDelay: Int) {
        val kickMessage = Component.text(configManager.read("lastPlayerDeath") ?: "Someone")
            .color(NamedTextColor.DARK_RED)
            .decorate(TextDecoration.BOLD)
            .append(
                Component.text(" has died and thus the world falls once again!")
                    .color(NamedTextColor.WHITE)
            )

        shutdownTotal = secondsDelay
        shutdownRemaining = secondsDelay

        val bossBar = BossBar.bossBar(
            Component.text("World resetting in ${secondsDelay}s", NamedTextColor.RED),
            1.0f,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
        )
        Bukkit.getOnlinePlayers().forEach { it.showBossBar(bossBar) }

        Bukkit.getScheduler().runTaskTimer(this, { task ->
            shutdownRemaining--
            if (shutdownRemaining <= 0) {
                task.cancel()
                Bukkit.getOnlinePlayers().forEach { it.hideBossBar(bossBar) }
                Bukkit.getOnlinePlayers().forEach { it.kick(kickMessage) }
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-off")
                Bukkit.shutdown()
                return@runTaskTimer
            }
            bossBar.progress(shutdownRemaining.toFloat() / shutdownTotal)
            bossBar.name(Component.text("World resetting in ${shutdownRemaining}s", NamedTextColor.RED))
        }, 20L, 20L)
    }

    fun skipShutdownCountdown() {
        if (shutdownRemaining > 5) {
            shutdownRemaining = 5
            shutdownTotal = 5
        }
    }
}
