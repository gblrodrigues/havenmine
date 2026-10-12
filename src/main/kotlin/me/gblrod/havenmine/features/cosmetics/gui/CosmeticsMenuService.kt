package me.gblrod.havenmine.features.cosmetics.gui

import me.gblrod.havenmine.features.cosmetics.config.InventoryConfig
import me.gblrod.havenmine.features.cosmetics.model.CosmeticsMenuType
import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

class CosmeticsMenuService(
    private val plugin: JavaPlugin,
    private val cosmeticsMenuContentFactory: CosmeticsMenuContentFactory
) {
    fun openMainMenu(player: Player) {
        val holder = CosmeticsMenuHolder(
            plugin = plugin,
            type = CosmeticsMenuType.MAIN,
            title = cosmeticsMenuContentFactory.mainMenuTitle()
        )

        holder.menu.setItem(
            InventoryConfig.MOVEMENT_TRAILS_SLOT,
            cosmeticsMenuContentFactory.movementTrailsItem(player)
        )

        holder.menu.setItem(
            InventoryConfig.CLOSE_MENU_SLOT,
            cosmeticsMenuContentFactory.closeMenuItem()
        )

        player.openInventory(holder.menu)
    }

    fun openMovementTrails(player: Player) {
        val holder = CosmeticsMenuHolder(
            plugin = plugin,
            type = CosmeticsMenuType.MOVEMENT_TRAILS,
            title = cosmeticsMenuContentFactory.movementTrailsTitle()
        )

        MovementTrailType.entries.forEach {
            holder.menu.setItem(
                it.menuSlot,
                cosmeticsMenuContentFactory.movementTrailItem(
                    player = player,
                    trail = it
                )
            )
        }

        holder.menu.setItem(
            InventoryConfig.CLOSE_MENU_SLOT,
            cosmeticsMenuContentFactory.closeMenuItem()
        )

        player.openInventory(holder.menu)
    }

    fun closeMenu(player: Player) {
        player.closeInventory()
    }

    fun refreshMovementTrailItems(inventory: Inventory, player: Player) {
        MovementTrailType.entries.forEach {
            inventory.setItem(
                it.menuSlot,
                cosmeticsMenuContentFactory.movementTrailItem(
                    player = player,
                    trail = it
                )
            )
        }
    }
}