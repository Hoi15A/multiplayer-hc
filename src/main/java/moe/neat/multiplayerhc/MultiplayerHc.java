package moe.neat.multiplayerhc;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.services.types.ConsumerService;
import moe.neat.multiplayerhc.commands.BaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.util.function.Function;


/**
 * Plugin entrypoint for Multiplayer Hardcore
 */
public final class MultiplayerHc extends JavaPlugin {

    private PaperCommandManager<CommandSender> commandManager;

    @Override
    public void onEnable() {
        setupEventListeners();
        setupCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setupEventListeners() {
        var reflections = new Reflections("moe.neat.multiplayerhc.event");
        var listeners = reflections.getSubTypesOf(Listener.class);

        listeners.forEach(listener -> {
            try {
                var instance = listener.getConstructor().newInstance();
                getServer().getPluginManager().registerEvents(instance, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupCommands() {
        try {
            commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            this.getLogger().severe("Failed to initialize the command manager");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        commandManager.registerCommandPreProcessor(preprocessingContext -> {
            var sender = preprocessingContext.getCommandContext().getSender();
            if (sender instanceof Player && !sender.hasPermission("multiplayerhc.commands")) {
                sender.sendMessage(Component.text("You dont have permission to use this!").color(NamedTextColor.RED));
                ConsumerService.interrupt();
            }
        });

        commandManager.registerAsynchronousCompletions();
        commandManager.registerBrigadier();

        var reflections = new Reflections("moe.neat.multiplayerhc.commands");
        var commands = reflections.getSubTypesOf(BaseCommand.class);

        commands.forEach(command -> {
            try {
                var instance = command.getConstructor().newInstance();
                instance.register(commandManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void shutDownServer(int secondsDelay) {
        Component kickMessage = Component.text(ConfigManager.read("lastPlayerDeath"))
                .color(NamedTextColor.DARK_RED)
                .decorate(TextDecoration.BOLD)
                .append(Component.text(" has died and thus the world falls once again!").color(NamedTextColor.WHITE));

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> player.kick(kickMessage));

                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "save-off");

                Bukkit.shutdown();
            }
        }.runTaskLater(MultiplayerHc.getPlugin(MultiplayerHc.class), 20L * secondsDelay);
    }
}
