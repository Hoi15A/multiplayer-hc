package moe.neat.multiplayerhc.commands;

import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;

public interface BaseCommand {
    void register(PaperCommandManager<CommandSender> manager);
}
