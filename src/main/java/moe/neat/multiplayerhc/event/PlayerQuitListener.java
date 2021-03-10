package moe.neat.multiplayerhc.event;

import moe.neat.multiplayerhc.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

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
