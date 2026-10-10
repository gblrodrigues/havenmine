package me.gblrod.havenmine.features.social.command

import me.gblrod.havenmine.core.command.keys.CommandMessageKeys
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SocialProfileCommand(
    private val playerFeedbackService: PlayerFeedbackService,
    private val messageKey: String
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
            messageKey = messageKey,
            sound = null
        )

        return true
    }
}