package me.gblrod.havenmine.features.cosmetics.command

import me.gblrod.havenmine.core.command.keys.CommandMessageKeys
import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuService
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CosmeticsCommand(
    private val cosmeticsMenuService: CosmeticsMenuService,
    private val playerFeedbackService: PlayerFeedbackService
) : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            playerFeedbackService.send(
                sender = sender,
                messageKey = CommandMessageKeys.PLAYER_ONLY
            )
            return true
        }

        playerFeedbackService.send(
            sender = sender,
            sound = FeedbackSounds.OPEN_MENU
        )

        cosmeticsMenuService.openMainMenu(player = sender)
        return true
    }
}