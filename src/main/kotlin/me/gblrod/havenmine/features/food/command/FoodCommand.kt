package me.gblrod.havenmine.features.food.command

import me.gblrod.havenmine.core.command.CommandsName
import me.gblrod.havenmine.core.command.CooldownCommand
import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.food.keys.FoodMessageKeys
import org.bukkit.entity.Player

class FoodCommand(
    private val playerFeedbackService: PlayerFeedbackService,
    cooldownSeconds: Long
) : CooldownCommand(
    cooldownId = CommandsName.FOOD_COMMAND,
    cooldownSeconds = cooldownSeconds,
    playerFeedbackService = playerFeedbackService
) {
    override fun shouldStartCooldown(player: Player): Boolean {
        return needsFoodRestore(player = player)
    }

    override fun execute(player: Player, args: Array<out String>) {
        if (!needsFoodRestore(player = player)) {
            playerFeedbackService.send(
                sender = player,
                messageKey = FoodMessageKeys.ERROR,
                sound = FeedbackSounds.ERROR
            )
            return
        }

        player.foodLevel = 20
        player.saturation = 20f
        player.exhaustion = 0f

        playerFeedbackService.send(
            sender = player,
            messageKey = FoodMessageKeys.ENABLED,
            sound = FeedbackSounds.SUCCESS
        )
    }

    private fun needsFoodRestore(player: Player): Boolean {
        return player.foodLevel < 20
    }
}