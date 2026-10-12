package me.gblrod.havenmine.features.cosmetics.gui

import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import me.gblrod.havenmine.features.particles.movement.service.MovementTrailService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class CosmeticsMenuContentFactory(
    private val plugin: JavaPlugin,
    private val movementTrailService: MovementTrailService
) {

    private val miniMessage = MiniMessage.miniMessage()

    fun mainMenuTitle(): Component {
        return getConfiguredComponent(path = "cosmetics.menus.main.title")
    }

    fun movementTrailsTitle(): Component {
        return getConfiguredComponent(path = "cosmetics.menus.movement_trails.title")
    }

    fun movementTrailsItem(player: Player): ItemStack {
        val trails = MovementTrailType.entries
        val total = trails.size
        val unlocked = total
        val percentage = if (total > 0) unlocked * 100 / total else 0

        val selectedTrail = movementTrailService.getActiveTrail(player)

        val selectedComponent = selectedTrail?.let { trail ->
            getConfiguredComponent(
                path = "cosmetics.menus.movement_trails.${trail.configKey}.name"
            )
        } ?: getConfiguredComponent(
            path = "cosmetics.menus.main.no_selection"
        )

        return createItem(
            material = Material.SLIME_BALL,
            name = getConfiguredComponent(path = "cosmetics.menus.main.movement_trails.name"),
            lore = getConfiguredLore(
                path = "cosmetics.menus.main.movement_trails.lore",
                Placeholder.component("selected", selectedComponent),
                Placeholder.unparsed("qtd_unlocked", unlocked.toString()),
                Placeholder.unparsed("total", total.toString()),
                Placeholder.unparsed("percent_unlocked", percentage.toString())
            )
        )
    }

    fun movementTrailItem(player: Player, trail: MovementTrailType): ItemStack {
        val isSelected = movementTrailService.getActiveTrail(player) == trail

        val trailsPath = "cosmetics.menus.movement_trails"
        val trailPath = "$trailsPath.${trail.configKey}"

        val actionPath = if (isSelected) {
            "$trailsPath.action_selected"
        } else {
            "$trailsPath.action_unselected"
        }

        return createItem(
            material = trail.icon,
            name = getConfiguredComponent("$trailPath.name"),
            lore = getConfiguredLore(
                path = "$trailsPath.lore",
                Placeholder.component("trail_description", getConfiguredComponent("$trailPath.description")),
                Placeholder.component("action", getConfiguredComponent(actionPath))
            )
        )
    }

    fun closeMenuItem(): ItemStack {
        return createItem(
            material = Material.RED_DYE,
            name = getConfiguredComponent(path = "cosmetics.menus.close_menu.name"),
            lore = getConfiguredLore(path = "cosmetics.menus.close_menu.lore")
        )
    }

    private fun getConfiguredComponent(path: String, vararg resolvers: TagResolver): Component {
        val text = plugin.config.getString(path)

        if (text == null) {
            plugin.logger.warning("Missing configuration key: $path")
            return Component.empty()
        }

        return miniMessage.deserialize(
            text,
            *resolvers
        ).decoration(
            TextDecoration.ITALIC,
            false
        )
    }

    private fun getConfiguredLore(path: String, vararg resolvers: TagResolver): List<Component> {
        if (!plugin.config.isList(path)) {
            plugin.logger.warning("Missing or invalid configuration list: $path")
            return emptyList()
        }

        return plugin.config.getStringList(path).map { line ->
            miniMessage.deserialize(
                line,
                *resolvers
            ).decoration(
                TextDecoration.ITALIC,
                false
            )
        }
    }

    private fun createItem(material: Material, name: Component, lore: List<Component>): ItemStack {
        return ItemStack(material).also { item ->
            item.editMeta { meta ->
                meta.displayName(name)
                meta.lore(lore)
            }
        }
    }
}