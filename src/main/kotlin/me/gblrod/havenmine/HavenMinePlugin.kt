package me.gblrod.havenmine

import me.gblrod.havenmine.core.command.CommandsName
import me.gblrod.havenmine.core.service.MessageService
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.craft.command.CraftCommand
import me.gblrod.havenmine.features.food.command.FoodCommand
import me.gblrod.havenmine.features.health.command.HealthCommand
import me.gblrod.havenmine.features.light.command.LightCommand
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

class HavenMinePlugin : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        val defaultCooldownSeconds = config
            .getLong("cooldowns.default_seconds", 300L)
            .coerceAtLeast(minimumValue = 0L)

        val messageService = MessageService(config = config)
        val playerFeedbackService = PlayerFeedbackService(messageService = messageService)

        val foodCommand = getCommand(CommandsName.FOOD_COMMAND)
        val healthCommand = getCommand(CommandsName.HEALTH_COMMAND)
        val craftCommand = getCommand(CommandsName.CRAFT_COMMAND)
        val lightCommand = getCommand(CommandsName.LIGHT_COMMAND)

        when {
            foodCommand == null -> {
                pluginNotFound(name = CommandsName.FOOD_COMMAND)
                return
            }

            healthCommand == null -> {
                pluginNotFound(name = CommandsName.HEALTH_COMMAND)
                return
            }

            craftCommand == null -> {
                pluginNotFound(name = CommandsName.CRAFT_COMMAND)
                return
            }

            lightCommand == null -> {
                pluginNotFound(name = CommandsName.LIGHT_COMMAND)
                return
            }
        }

        commandSetExecutor(
            foodCommand = foodCommand,
            healthCommand = healthCommand,
            craftCommand = craftCommand,
            lightCommand = lightCommand,
            playerFeedbackService = playerFeedbackService,
            cooldownSeconds = defaultCooldownSeconds
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
        foodCommand: PluginCommand,
        healthCommand: PluginCommand,
        craftCommand: PluginCommand,
        lightCommand: PluginCommand,
        playerFeedbackService: PlayerFeedbackService,
        cooldownSeconds: Long
    ) {
        craftCommand.setExecutor(CraftCommand(playerFeedbackService = playerFeedbackService))
        foodCommand.setExecutor(
            FoodCommand(
                playerFeedbackService = playerFeedbackService,
                cooldownSeconds = cooldownSeconds
            )
        )
        healthCommand.setExecutor(
            HealthCommand(
                playerFeedbackService = playerFeedbackService,
                cooldownSeconds = cooldownSeconds
            )
        )
        lightCommand.setExecutor(
            LightCommand(
                playerFeedbackService = playerFeedbackService,
                cooldownSeconds = cooldownSeconds
            )
        )
    }
}