package moe.neat.multiplayerhc.event

import moe.neat.multiplayerhc.MultiplayerHc
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class PlayerDeathListener(private val plugin: MultiplayerHc) : Listener {

    private val config = plugin.configManager
    private val shutdownTime = config.read("shutdownTime")?.toIntOrNull() ?: 120
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm")

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        updateConfig(event)
        logDeath(event)
        PlayerJoinListener.spectatorOnLogin = true

        announceDeath(event.entity.name)

        val skipButton = Component.text("\n[Click to voteskip]\n")
            .clickEvent(ClickEvent.runCommand("/vote-end"))
            .color(NamedTextColor.GREEN)
        Bukkit.getServer().sendMessage(skipButton)

        plugin.shutDownServer(shutdownTime)
    }

    private fun updateConfig(event: PlayerDeathEvent) {
        val now = OffsetDateTime.now()
        config.save("resetWorld", "true")
        config.save("lastPlayerDeath", event.entity.name)

        val deathMessage = event.deathMessage()
        if (deathMessage != null) {
            config.save("lastDeathReason", GsonComponentSerializer.gson().serialize(deathMessage))
        }

        val worldCount = (config.read("worldCount")?.toIntOrNull() ?: 0) + 1
        config.save("worldCount", worldCount.toString())
        config.save("lastDeathTime", dateFormatter.format(now))
    }

    private fun announceDeath(playerName: String) {
        val title = Component.text("$playerName has died!", NamedTextColor.DARK_RED)
        val subtitle = Component.text("As such, the world will now end...", NamedTextColor.GRAY)
        val displayedTitle = Title.title(
            title, subtitle,
            Title.Times.times(
                java.time.Duration.ofSeconds(1),
                java.time.Duration.ofSeconds(15),
                java.time.Duration.ofSeconds(1)
            )
        )

        Bukkit.getOnlinePlayers().forEach { player ->
            player.playSound(Sound.sound(Key.key("entity.wither.spawn"), Sound.Source.PLAYER, 1f, 0.5f))
            player.showTitle(displayedTitle)
            player.gameMode = GameMode.SPECTATOR
        }

        Bukkit.getServer().sendMessage(
            Component.text("The server will close in $shutdownTime seconds and reset.")
        )
    }

    private fun logDeath(event: PlayerDeathEvent) {
        val logPath = plugin.dataFolder.toPath().resolve("deaths.log")

        if (!Files.exists(logPath)) {
            Files.createFile(logPath)
        }

        val deathMessage = event.deathMessage()
        if (deathMessage != null) {
            val entry = GsonComponentSerializer.gson().serialize(deathMessage) + "\n"
            Files.write(logPath, entry.toByteArray(), StandardOpenOption.APPEND)
        }
    }
}
