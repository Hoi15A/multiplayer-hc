package moe.neat.multiplayerhc.event;

import com.google.gson.JsonParseException;
import moe.neat.multiplayerhc.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        Component separator = Component.text(" | ", NamedTextColor.GRAY);
        Component deathMsg;
        try {
            deathMsg = GsonComponentSerializer.gson().deserialize(ConfigManager.read("lastDeathReason")).color(NamedTextColor.DARK_RED);
        } catch (JsonParseException e) {
            deathMsg = Component.text("None", NamedTextColor.WHITE);
        }

        Component motd = Component.text("World: " + ConfigManager.read("worldCount"), NamedTextColor.BLUE)
                .append(separator)
                .append(Component.text("Last reset: " + ConfigManager.read("lastDeathTime"), NamedTextColor.GREEN))
                .append(Component.text("\nLast death: ", NamedTextColor.RED))
                .append(deathMsg);

        event.motd(motd);
    }
}
