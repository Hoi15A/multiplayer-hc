package moe.neat.multiplayerhc.event

import moe.neat.multiplayerhc.MultiplayerHc
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(private val plugin: MultiplayerHc) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (plugin.configManager.read("resetWorld") == "true") {
            if (Bukkit.getOnlinePlayers().size < 2) {
                plugin.logger.info("Shutting down early due to all players having logged out.")
                Bukkit.shutdown()
            }
        }
    }
}
