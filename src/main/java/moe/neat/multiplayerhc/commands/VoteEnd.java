package moe.neat.multiplayerhc.commands;

import cloud.commandframework.paper.PaperCommandManager;
import moe.neat.multiplayerhc.ConfigManager;
import moe.neat.multiplayerhc.MultiplayerHc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class VoteEnd implements BaseCommand {

    private static final String ALREADY_VOTED = "You have already voted to skip the wait time.";
    private static final String WORLD_STILL_ACTIVE = "The world is currently still ongoing!";
    private static final String N_VOTES_NEEDED = "[%s/%s] votes to restart now!";
    private static final String VOTE_SUCCESS = "Threshold reached, restarting in 10 seconds!";

    private final Set<String> votes = new HashSet<>();

    @Override
    public void register(PaperCommandManager<CommandSender> manager) {
        manager.command(manager.commandBuilder("vote-end")
                .senderType(Player.class)
                .handler(cmdContext -> manager.taskRecipe().begin(cmdContext).synchronous(ctx -> {
                    var sender = ctx.getSender();
                    var playerName = sender.getName();

                    if ("true".equals(ConfigManager.read("resetWorld"))) {
                        if (!votes.contains(playerName)) {
                            votes.add(playerName);
                            evaluateVotes(sender);
                        } else {
                            ctx.getSender().sendMessage(ALREADY_VOTED);
                        }
                    } else {
                        ctx.getSender().sendMessage(
                                Component.text(WORLD_STILL_ACTIVE)
                                .color(NamedTextColor.RED)
                        );
                    }

                }).execute())
        );
    }

    private void evaluateVotes(CommandSender sender) {
        var requiredCount = sender.getServer().getOnlinePlayers().size();
        var voteCount = votes.size();

        float percentage = ((float) voteCount) / requiredCount;

        sender.getServer().sendMessage(Component.text(String.format(N_VOTES_NEEDED, voteCount, requiredCount))
                                                .color(NamedTextColor.BLUE)
        );

        if (percentage >= 0.5) {
            sender.getServer().sendMessage(Component.text(VOTE_SUCCESS));

            JavaPlugin.getPlugin(MultiplayerHc.class).shutDownServer(10);
        }

        sender.getServer();
    }
}
