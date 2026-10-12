package me.gblrod.havenmine

import me.gblrod.havenmine.core.command.CommandsName
import me.gblrod.havenmine.core.service.MessageService
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.cosmetics.command.CosmeticsCommand
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuContentFactory
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuService
import me.gblrod.havenmine.features.cosmetics.listener.CosmeticsMenuListener
import me.gblrod.havenmine.features.craft.command.CraftCommand
import me.gblrod.havenmine.features.food.command.FoodCommand
import me.gblrod.havenmine.features.health.command.HealthCommand
import me.gblrod.havenmine.features.light.command.LightCommand
import me.gblrod.havenmine.features.particles.movement.listener.MovementParticleListener
import me.gblrod.havenmine.features.particles.movement.service.MovementTrailService
import me.gblrod.havenmine.features.social.command.SocialProfileCommand
import me.gblrod.havenmine.features.social.keys.SocialMessageKeys
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

        val movementTrailService = MovementTrailService()
        val cosmeticsMenuContentFactory =
            CosmeticsMenuContentFactory(plugin = this, movementTrailService = movementTrailService)
        val cosmeticsMenuService =
            CosmeticsMenuService(
                plugin = this,
                cosmeticsMenuContentFactory = cosmeticsMenuContentFactory
            )

        val foodCommand = getRequiredCommand(CommandsName.FOOD_COMMAND) ?: return
        val healthCommand = getRequiredCommand(CommandsName.HEALTH_COMMAND) ?: return
        val craftCommand = getRequiredCommand(CommandsName.CRAFT_COMMAND) ?: return
        val lightCommand = getRequiredCommand(CommandsName.LIGHT_COMMAND) ?: return
        val linkedInCommand = getRequiredCommand(CommandsName.LINKEDIN_COMMAND) ?: return
        val gitHubCommand = getRequiredCommand(CommandsName.GITHUB_COMMAND) ?: return
        val cosmeticsCommand = getRequiredCommand(CommandsName.COSMETICS_COMMAND) ?: return

        registerCommandExecutors(
            foodCommand = foodCommand,
            healthCommand = healthCommand,
            craftCommand = craftCommand,
            lightCommand = lightCommand,
            linkedInCommand = linkedInCommand,
            gitHubCommand = gitHubCommand,
            cosmeticsCommand = cosmeticsCommand,
            cosmeticsMenuService = cosmeticsMenuService,
            playerFeedbackService = playerFeedbackService,
            cooldownSeconds = defaultCooldownSeconds
        )

        server.pluginManager.registerEvents(
            CosmeticsMenuListener(
                plugin = this,
                cosmeticsMenuService = cosmeticsMenuService,
                movementTrailService = movementTrailService,
                playerFeedbackService = playerFeedbackService
            ),
            this
        )

        server.pluginManager.registerEvents(
            MovementParticleListener(movementTrailService = movementTrailService),
            this
        )

        logger.info("HavenMine enabled!")
    }

    override fun onDisable() {
        logger.info("HavenMine disabled!")
    }

    private fun registerCommandExecutors(
        foodCommand: PluginCommand,
        healthCommand: PluginCommand,
        craftCommand: PluginCommand,
        lightCommand: PluginCommand,
        linkedInCommand: PluginCommand,
        gitHubCommand: PluginCommand,
        cosmeticsCommand: PluginCommand,
        cosmeticsMenuService: CosmeticsMenuService,
        playerFeedbackService: PlayerFeedbackService,
        cooldownSeconds: Long
    ) {
        craftCommand.setExecutor(CraftCommand(playerFeedbackService = playerFeedbackService))
        linkedInCommand.setExecutor(
            SocialProfileCommand(
                playerFeedbackService = playerFeedbackService,
                messageKey = SocialMessageKeys.LINKEDIN
            )
        )
        gitHubCommand.setExecutor(
            SocialProfileCommand(
                playerFeedbackService = playerFeedbackService,
                messageKey = SocialMessageKeys.GITHUB
            )
        )
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
        cosmeticsCommand.setExecutor(
            CosmeticsCommand(
                cosmeticsMenuService = cosmeticsMenuService,
                playerFeedbackService = playerFeedbackService
            )
        )
    }

    private fun getRequiredCommand(name: String): PluginCommand? {
        val command = getCommand(name)

        if (command != null) return command

        logger.severe("Command '$name' is missing from plugin.yml.")
        server.pluginManager.disablePlugin(this)

        return null
    }
}