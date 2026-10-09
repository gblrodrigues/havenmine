package me.gblrod.havenmine.features.food.command

import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class FoodCommand : CommandExecutor {
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

        val needsRestore = sender.foodLevel < 20 || sender.saturation < 20f || sender.exhaustion > 0f

        if (needsRestore && (sender.gameMode == GameMode.SURVIVAL || sender.gameMode == GameMode.ADVENTURE)) {
            sender.foodLevel = 20
            sender.saturation = 20f
            sender.exhaustion = 0f

            sender.sendMessage("§a§lSUCESSO! §aSua fome foi restaurada!")
            sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 0.5f);
        } else {
            sender.sendMessage("§c§lERRO! §cVocê já está saciado!")
            sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
        }

        return true
    }
}