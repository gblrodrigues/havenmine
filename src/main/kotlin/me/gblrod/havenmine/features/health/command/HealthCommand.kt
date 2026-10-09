package me.gblrod.havenmine.features.health.command

import me.gblrod.havenmine.core.command.CommandsName
import me.gblrod.havenmine.core.command.CooldownCommand
import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.health.keys.HealthMessageKeys
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class HealthCommand(
    private val playerFeedbackService: PlayerFeedbackService,
    cooldownSeconds: Long
) : CooldownCommand(
    cooldownId = CommandsName.HEALTH_COMMAND,
    cooldownSeconds = cooldownSeconds,
    playerFeedbackService = playerFeedbackService
) {
    override fun shouldStartCooldown(player: Player): Boolean {
        return player.health < getMaxHealth(player = player)
    }

    override fun execute(player: Player, args: Array<out String>) {
        val maxHealth = getMaxHealth(player)

        if (player.health < maxHealth) {
            player.health = maxHealth

            playerFeedbackService.send(
                sender = player,
                messageKey = HealthMessageKeys.ENABLED,
                sound = FeedbackSounds.SUCCESS
            )
        } else {
            playerFeedbackService.send(
                sender = player,
                messageKey = HealthMessageKeys.ERROR,
                sound = FeedbackSounds.ERROR
            )
        }
    }
}

private fun getMaxHealth(player: Player): Double {
    return player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
}