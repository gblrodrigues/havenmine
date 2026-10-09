package me.gblrod.havenmine

import me.gblrod.havenmine.core.command.CommandsConfig
import me.gblrod.havenmine.features.craft.command.CraftCommand
import me.gblrod.havenmine.features.food.command.FoodCommand
import me.gblrod.havenmine.features.health.command.HealthCommand
import me.gblrod.havenmine.features.light.command.LightCommand
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

class HavenMinePlugin : JavaPlugin() {
    override fun onEnable() {
        val foodCommand = getCommand(CommandsConfig.FOOD_COMMAND)
        val healthCommand = getCommand(CommandsConfig.HEALTH_COMMAND)
        val craftCommand = getCommand(CommandsConfig.CRAFT_COMMAND)
        val lightCommand = getCommand(CommandsConfig.LIGHT_COMMAND)

        when {
            foodCommand == null -> {
                pluginNotFound(name = CommandsConfig.FOOD_COMMAND)
                return
            }

            healthCommand == null -> {
                pluginNotFound(name = CommandsConfig.HEALTH_COMMAND)
                return
            }

            craftCommand == null -> {
                pluginNotFound(name = CommandsConfig.CRAFT_COMMAND)
                return
            }

            lightCommand == null -> {
                pluginNotFound(name = CommandsConfig.LIGHT_COMMAND)
                return
            }
        }

        commandSetExecutor(
            foodCommand = foodCommand,
            healthCommand = healthCommand,
            craftCommand = craftCommand,
            lightCommand = lightCommand
        )

        logger.info("HavenMine enabled!")
    }

    override fun onDisable() {
        logger.info("HavenMine disabled!")
    }

    private fun pluginNotFound(name: String) {
        logger.severe("Command '$name' is missing from plugin.yml.")
        server.pluginManager.disablePlugin(this)
        return
    }

    private fun commandSetExecutor(
        foodCommand: PluginCommand?,
        healthCommand: PluginCommand?,
        craftCommand: PluginCommand?,
        lightCommand: PluginCommand?
    ) {
        foodCommand?.setExecutor(FoodCommand())
        healthCommand?.setExecutor(HealthCommand())
        craftCommand?.setExecutor(CraftCommand())
        lightCommand?.setExecutor(LightCommand())
    }
}