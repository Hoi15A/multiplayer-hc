package moe.neat.multiplayerhc.commands

import io.papermc.paper.command.brigadier.CommandSourceStack
import moe.neat.multiplayerhc.MultiplayerHc
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UnstableApiUsage")
class VoteEnd(private val plugin: MultiplayerHc) {

    private val votes = mutableSetOf<String>()

    fun register(manager: PaperCommandManager<CommandSourceStack>) {
        manager.command(
            manager.commandBuilder("vote-end")
                .handler { ctx ->
                    val sender = ctx.sender().sender
                    if (sender !is Player) return@handler

                    val playerName = sender.name

                    if (plugin.configManager.read("resetWorld") == "true") {
                        if (votes.add(playerName)) {
                            evaluateVotes(sender)
                        } else {
                            sender.sendMessage(Component.text("You have already voted to skip the wait time."))
                        }
                    } else {
                        sender.sendMessage(
                            Component.text("The world is currently still ongoing!", NamedTextColor.RED)
                        )
                    }
                }
        )
    }

    private fun evaluateVotes(sender: Player) {
        val requiredCount = sender.server.onlinePlayers.size
        val voteCount = votes.size
        val percentage = voteCount.toFloat() / requiredCount

        sender.server.sendMessage(
            Component.text("[$voteCount/$requiredCount] votes to restart now!", NamedTextColor.BLUE)
        )

        if (percentage >= 0.5f) {
            sender.server.sendMessage(Component.text("Threshold reached, restarting in 5 seconds!"))
            plugin.skipShutdownCountdown()
        }
    }
}
