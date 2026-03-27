package moe.neat.multiplayerhc.event

import moe.neat.multiplayerhc.MultiplayerHc
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamageEventListener(private val plugin: MultiplayerHc) : Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player && plugin.configManager.read("resetWorld") == "true") {
            event.isCancelled = true
        }
    }
}
