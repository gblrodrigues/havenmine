package me.gblrod.havenmine.features.light.command

import me.gblrod.havenmine.core.command.CommandsName
import me.gblrod.havenmine.core.command.CooldownCommand
import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.light.keys.LightMessageKeys
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class LightCommand(
    private val playerFeedbackService: PlayerFeedbackService,
    cooldownSeconds: Long
) : CooldownCommand(
    cooldownId = CommandsName.LIGHT_COMMAND,
    cooldownSeconds = cooldownSeconds,
    requireSurvivalOrAdventure = false,
    playerFeedbackService = playerFeedbackService
) {
    private val effect = PotionEffectType.NIGHT_VISION

    override fun canExecuteDuringCooldown(player: Player): Boolean {
        return player.hasPotionEffect(effect)
    }

    override fun shouldStartCooldown(player: Player): Boolean {
        return !player.hasPotionEffect(effect)
    }

    override fun execute(
        player: Player,
        args: Array<out String>
    ) {
        if (player.hasPotionEffect(effect)) {
            player.removePotionEffect(effect)

            playerFeedbackService.send(
                sender = player,
                messageKey = LightMessageKeys.DISABLED,
                sound = FeedbackSounds.DISABLED
            )
        } else {
            player.addPotionEffect(PotionEffect(effect, PotionEffect.INFINITE_DURATION, 1))

            playerFeedbackService.send(
                sender = player,
                messageKey = LightMessageKeys.ENABLED,
                sound = FeedbackSounds.SUCCESS
            )
        }
    }
}