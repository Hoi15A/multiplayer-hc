package moe.neat.multiplayerhc.event;

import moe.neat.multiplayerhc.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    /**
     * Shuts down the server if the world is scheduled to reset and nobody else is online
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if ("true".equals(ConfigManager.read("resetWorld"))) {
            System.out.println(Bukkit.getServer().getOnlinePlayers().size());
            if (Bukkit.getServer().getOnlinePlayers().size() < 2) {
                Bukkit.getServer().shutdown();
            }
        }
    }
}
