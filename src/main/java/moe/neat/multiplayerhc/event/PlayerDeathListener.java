package moe.neat.multiplayerhc.event;

import moe.neat.multiplayerhc.ConfigManager;
import moe.neat.multiplayerhc.MultiplayerHc;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class PlayerDeathListener implements Listener {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws IOException {

        updateConfig(event);
        logDeath(event);
        PlayerJoinListener.setSpectatorOnLogin(true);

        event.getEntity().sendMessage("You died, get fucked");

        announceDeath(event.getEntity().getName());

        Component kickMessage = Component.text(event.getEntity().getName())
                .color(NamedTextColor.DARK_RED)
                .decorate(TextDecoration.BOLD)
                .append(Component.text(" has died and thus the world falls once again!").color(NamedTextColor.WHITE));

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.kick(kickMessage);
                });

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-off");
                Bukkit.getServer().getWorlds().forEach(world -> {
                    Arrays.stream(world.getLoadedChunks()).forEach(Chunk::unload);
                });

                Bukkit.shutdown();
            }
        }.runTaskLater(MultiplayerHc.getPlugin(MultiplayerHc.class), 20*60*6);


    }

    /**
     * Sets all relevant config values so that standalone can reset the world and the motd is updated.
     *
     * @param event
     * @throws IOException
     */
    private void updateConfig(PlayerDeathEvent event) throws IOException {
        OffsetDateTime now = OffsetDateTime.now();

        ConfigManager.save("resetWorld", "true");
        ConfigManager.save("lastPlayerDeath", event.getEntity().getName());
        ConfigManager.save("lastDeathReason", GsonComponentSerializer.gson().serialize(event.deathMessage()));

        int worldCount = Integer.parseInt(ConfigManager.read("worldCount"));
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
        Component title = Component.text(playerName + " has died!", NamedTextColor.DARK_RED);
        Component subtitle = Component.text("As such, the world will now end...", NamedTextColor.GRAY);
        Title displayedTitle = Title.title(title, subtitle, Title.Times.of(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofSeconds(1)));


        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(Sound.sound(Key.key("entity.wither.spawn"), Sound.Source.PLAYER, 1f, 0.5f));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*20, 10, true, false, false));
            player.showTitle(displayedTitle);
            player.setGameMode(GameMode.SPECTATOR);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().sendMessage(Component.text("You have 5 minutes until the world ends, enjoy."));
            }
        }.runTaskLater(MultiplayerHc.getPlugin(MultiplayerHc.class), 20*30);
    }

    /**
     * Logs player deaths to a text file
     *
     * @param event
     * @throws IOException
     */
    private void logDeath(PlayerDeathEvent event) throws IOException {
        File dataFolder = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("MultiplayerHc")).getDataFolder();

        Path logPath = Paths.get(dataFolder.getPath(), "/deaths.log");
        File logFile = logPath.toFile();

        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        String entry = GsonComponentSerializer.gson().serialize(event.deathMessage()) + "\n";

        Files.write(logPath, entry.getBytes(), StandardOpenOption.APPEND);
    }
}
