package me.gblrod.havenmine.features.light.command

import me.gblrod.havenmine.core.command.CooldownCommand
import me.gblrod.havenmine.core.command.CommandsConfig
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class LightCommand : CooldownCommand(
    cooldownId = CommandsConfig.LIGHT_COMMAND,
    cooldownSeconds = CommandsConfig.COOLDOWN_COMMAND_SECONDS
) {
    private val effect = PotionEffectType.NIGHT_VISION

    override fun canExecuteDuringCooldown(player: Player): Boolean {
        return player.hasPotionEffect(effect)
    }

    override fun shouldStartCooldown(player: Player): Boolean {
        return player.hasPotionEffect(effect)
    }

    override fun execute(
        player: Player,
        args: Array<out String>
    ) {
        if (player.hasPotionEffect(effect)) {
            player.removePotionEffect(effect)

            player.sendMessage("§cLuz desabilitada!")
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
        } else {
            player.addPotionEffect(PotionEffect(effect, PotionEffect.INFINITE_DURATION, 1))

            player.sendMessage("§aLuz ativada!")
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 0.5f)
        }
    }
}