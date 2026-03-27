package moe.neat.multiplayerhc.event

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (spectatorOnLogin) {
            event.player.gameMode = GameMode.SPECTATOR
            event.player.sendMessage(Component.text("This world is currently ending.", NamedTextColor.RED))
        }
    }

    companion object {
        var spectatorOnLogin = false
    }
}
