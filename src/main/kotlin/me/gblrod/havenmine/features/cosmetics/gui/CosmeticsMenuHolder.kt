package me.gblrod.havenmine.features.cosmetics.gui

import me.gblrod.havenmine.features.cosmetics.config.InventoryConfig
import me.gblrod.havenmine.features.cosmetics.model.CosmeticsMenuType
import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.java.JavaPlugin

class CosmeticsMenuHolder(
    plugin: JavaPlugin,
    val type: CosmeticsMenuType,
    title: Component
) : InventoryHolder {
    private val backingInventory: Inventory =
        plugin.server.createInventory(
            this,
            InventoryConfig.INVENTORY_SIZE,
            title
        )

    val menu: Inventory get() = backingInventory

    override fun getInventory(): Inventory = backingInventory
}