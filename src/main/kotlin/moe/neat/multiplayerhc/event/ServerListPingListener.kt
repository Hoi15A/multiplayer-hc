package moe.neat.multiplayerhc.event

import com.google.gson.JsonParseException
import moe.neat.multiplayerhc.MultiplayerHc
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerListPingListener(private val plugin: MultiplayerHc) : Listener {

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        val config = plugin.configManager
        val separator = Component.text(" | ", NamedTextColor.GRAY)

        val deathMsg = try {
            val raw = config.read("lastDeathReason")
            if (!raw.isNullOrBlank()) {
                GsonComponentSerializer.gson().deserialize(raw).color(NamedTextColor.DARK_RED)
            } else {
                Component.text("None", NamedTextColor.WHITE)
            }
        } catch (e: JsonParseException) {
            Component.text("None", NamedTextColor.WHITE)
        }

        val motd = Component.text("World: ${config.read("worldCount") ?: "1"}", NamedTextColor.BLUE)
            .append(separator)
            .append(Component.text("Last reset: ${config.read("lastDeathTime") ?: "Never"}", NamedTextColor.GREEN))
            .append(Component.text("\nLast death: ", NamedTextColor.RED))
            .append(deathMsg)

        event.motd(motd)
    }
}
