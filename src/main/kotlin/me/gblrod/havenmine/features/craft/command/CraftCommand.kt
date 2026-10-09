package me.gblrod.havenmine.features.craft.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType

class CraftCommand : CommandExecutor {
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

        sender.openInventory(MenuType.CRAFTING.create(sender))
        return true
    }
}