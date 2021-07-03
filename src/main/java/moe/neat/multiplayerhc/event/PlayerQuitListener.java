package moe.neat.multiplayerhc.event;

import moe.neat.multiplayerhc.ConfigManager;
import moe.neat.multiplayerhc.MultiplayerHc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PlayerQuitListener implements Listener {

    private static final Logger LOGGER = JavaPlugin.getPlugin(MultiplayerHc.class).getLogger();

    /**
     * Shuts down the server if the world is scheduled to reset and nobody else is online
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if ("true".equals(ConfigManager.read("resetWorld"))) {
            LOGGER.info("Shutting down early due to all players having logged out.");
            if (Bukkit.getServer().getOnlinePlayers().size() < 2) {
                Bukkit.getServer().shutdown();
            }
        }
    }
}
