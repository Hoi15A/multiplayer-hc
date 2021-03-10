package moe.neat.multiplayerhc.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private static boolean spectatorOnLogin = false;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (spectatorOnLogin) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().sendMessage(Component.text("This world is currently ending.", NamedTextColor.RED));
        }
    }

    public static void setSpectatorOnLogin(boolean spectatorOnLogin) {
        PlayerJoinListener.spectatorOnLogin = spectatorOnLogin;
    }
}
