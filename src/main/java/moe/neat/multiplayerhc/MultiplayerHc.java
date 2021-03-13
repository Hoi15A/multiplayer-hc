package moe.neat.multiplayerhc;

import moe.neat.multiplayerhc.event.PlayerDeathListener;
import moe.neat.multiplayerhc.event.PlayerJoinListener;
import moe.neat.multiplayerhc.event.PlayerQuitListener;
import moe.neat.multiplayerhc.event.ServerListPingListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entrypoint for Multiplayer Hardcore
 */
public final class MultiplayerHc extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new ServerListPingListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
