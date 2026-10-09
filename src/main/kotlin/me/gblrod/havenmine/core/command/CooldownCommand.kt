package me.gblrod.havenmine.core.command

import me.gblrod.havenmine.core.command.keys.CommandMessageKeys
import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class CooldownCommand(
    private val cooldownId: String,
    private val cooldownSeconds: Long,
    private val requireSurvivalOrAdventure: Boolean = true,
    private val playerFeedbackService: PlayerFeedbackService
) : CommandExecutor {
    protected open fun canExecuteDuringCooldown(player: Player) = false
    protected open fun shouldStartCooldown(player: Player) = true

    protected abstract fun execute(
        player: Player,
        args: Array<out String>
    )

    final override fun onCommand(
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

        if (!isValidGameMode(
                player = sender,
                requireSurvivalOrAdventure = requireSurvivalOrAdventure,
                playerFeedbackService = playerFeedbackService
            )
        ) {
            return true
        }

        val remaining = CommandCooldownManager.getRemaining(
            playerId = sender.uniqueId,
            command = cooldownId
        )

        if (remaining > 0 && !canExecuteDuringCooldown(player = sender)) {
            val time = CommandCooldownManager.formatTime(seconds = remaining)

            playerFeedbackService.send(
                sender = sender,
                messageKey = CommandMessageKeys.COOLDOWN_ACTIVE,
                sound = FeedbackSounds.ERROR,
                placeholders = mapOf("time" to time)
            )
            return true
        }

        val startCooldown = shouldStartCooldown(player = sender)
        execute(player = sender, args = args)

        if (startCooldown) {
            CommandCooldownManager.startCooldown(
                playerId = sender.uniqueId,
                command = cooldownId,
                durationSeconds = cooldownSeconds
            )
        }

        return true
    }
}