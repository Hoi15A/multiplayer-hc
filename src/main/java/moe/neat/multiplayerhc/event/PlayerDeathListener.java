package moe.neat.multiplayerhc.event;

import moe.neat.multiplayerhc.ConfigManager;
import moe.neat.multiplayerhc.MultiplayerHc;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PlayerDeathListener implements Listener {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final String PLAYER_DIED = "%s has died!";
    private static final String WORLD_ENDING = "As such, the world will now end...";
    private static final String SECONDS_UNTIL_RESET = "The server will close in %s seconds and reset.";
    private static final String VOTESKIP_BTN = "\n[Click to voteskip]\n";
    private static int SHUTDOWN_TIME;
    // TODO: constant texts and delays either up here or config

    public PlayerDeathListener() {
        SHUTDOWN_TIME = Integer.parseInt(ConfigManager.read("shutdownTime"));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws IOException {

        updateConfig(event);
        logDeath(event);
        PlayerJoinListener.setSpectatorOnLogin(true);

        announceDeath(event.getEntity().getName());

        var skipButton = Component.text(VOTESKIP_BTN)
                .clickEvent(ClickEvent.runCommand("/vote-end"))
                .color(NamedTextColor.GREEN);

        event.getEntity().getServer().sendMessage(skipButton);

        MultiplayerHc.getPlugin(MultiplayerHc.class).shutDownServer(SHUTDOWN_TIME);
    }

    /**
     * Sets all relevant config values so that standalone can reset the world and the motd is updated.
     */
    private void updateConfig(PlayerDeathEvent event) throws IOException {
        var now = OffsetDateTime.now();

        ConfigManager.save("resetWorld", "true");
        ConfigManager.save("lastPlayerDeath", event.getEntity().getName());
        ConfigManager.save("lastDeathReason", GsonComponentSerializer.gson().serialize(Objects.requireNonNull(event.deathMessage())));

        var worldCount = Integer.parseInt(ConfigManager.read("worldCount"));
        worldCount++;

        ConfigManager.save("worldCount", Integer.toString(worldCount));
        ConfigManager.save("lastDeathTime", formatter.format(now));
    }


    /**
     * Announces the death to all players
     *
     * @param playerName player name
     */
    private void announceDeath(String playerName) {
        Component title = Component.text(String.format(PLAYER_DIED, playerName), NamedTextColor.DARK_RED);
        Component subtitle = Component.text(WORLD_ENDING, NamedTextColor.GRAY);
        var displayedTitle = Title.title(title, subtitle, Title.Times.of(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofSeconds(1)));

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(Sound.sound(Key.key("entity.wither.spawn"), Sound.Source.PLAYER, 1f, 0.5f));

            player.showTitle(displayedTitle);
            player.setGameMode(GameMode.SPECTATOR);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().sendMessage(Component.text(String.format(SECONDS_UNTIL_RESET, SHUTDOWN_TIME)));
            }
        }.runTaskLater(MultiplayerHc.getPlugin(MultiplayerHc.class), 20L * SHUTDOWN_TIME);
    }

    /**
     * Logs player deaths to a text file
     *
     * @param event
     * @throws IOException
     */
    private void logDeath(PlayerDeathEvent event) throws IOException {
        File dataFolder = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MultiplayerHc")).getDataFolder();

        var logPath = Paths.get(dataFolder.getPath(), "/deaths.log");
        var logFile = logPath.toFile();

        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        String entry = GsonComponentSerializer.gson().serialize(Objects.requireNonNull(event.deathMessage())) + "\n";

        Files.write(logPath, entry.getBytes(), StandardOpenOption.APPEND);
    }
}
