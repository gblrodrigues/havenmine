package me.gblrod.havenmine.core.command

import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class CooldownCommand(
    private val cooldownId: String,
    private val cooldownSeconds: Long
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
            sender.sendMessage("Este comando só pode ser usado por um jogador.")
            return true
        }

        val remaining = CommandCooldownManager.getRemaining(
            playerId = sender.uniqueId,
            command = cooldownId
        )

        if (remaining > 0 && !canExecuteDuringCooldown(player = sender)) {
            val time = CommandCooldownManager.formatTime(seconds = remaining)

            sender.sendMessage("§c§lERRO! §cAguarde mais $time para utilizar este comando!")
            sender.playSound(sender.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return true
        }

        val startCooldown = shouldStartCooldown(player = sender)
        execute(player = sender, args = args)

        if (startCooldown) {
            CommandCooldownManager.startCooldown(
                playerId =  sender.uniqueId,
                command = cooldownId,
                durationSeconds = cooldownSeconds
            )
        }

        return true
    }
}