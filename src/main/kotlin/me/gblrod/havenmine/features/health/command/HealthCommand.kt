package me.gblrod.havenmine.features.health.command

import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HealthCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Este comando só pode ser usado por um jogador.")
            return true
        }

        if (sender.health < 20.0) {
            sender.health = 20.0
            sender.sendMessage("§a§lSUCESSO! §aSua vida foi restaurada!")
            sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 0.5f);
        } else {
            sender.sendMessage("§c§lERRO! §cSua vida já está cheia!")
            sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }

        return true
    }
}