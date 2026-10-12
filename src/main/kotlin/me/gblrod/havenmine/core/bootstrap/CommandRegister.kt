package me.gblrod.havenmine.core.bootstrap

import me.gblrod.havenmine.core.command.CommandsName
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.cosmetics.command.CosmeticsCommand
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuService
import me.gblrod.havenmine.features.craft.command.CraftCommand
import me.gblrod.havenmine.features.food.command.FoodCommand
import me.gblrod.havenmine.features.health.command.HealthCommand
import me.gblrod.havenmine.features.light.command.LightCommand
import me.gblrod.havenmine.features.social.command.SocialProfileCommand
import me.gblrod.havenmine.features.social.keys.SocialMessageKeys
import org.bukkit.plugin.java.JavaPlugin

class CommandRegister(
    private val plugin: JavaPlugin,
    private val playerFeedbackService: PlayerFeedbackService,
    private val cosmeticsMenuService: CosmeticsMenuService,
    private val cooldownSeconds: Long
) {
    fun register(): Boolean {
        val commandNames = listOf(
            CommandsName.FOOD_COMMAND,
            CommandsName.HEALTH_COMMAND,
            CommandsName.CRAFT_COMMAND,
            CommandsName.LIGHT_COMMAND,
            CommandsName.LINKEDIN_COMMAND,
            CommandsName.GITHUB_COMMAND,
            CommandsName.COSMETICS_COMMAND
        )

        val commands = commandNames.mapNotNull { name ->
            plugin.getCommand(name)?.let { name to it }
        }.toMap()

        val missingCommands = commandNames - commands.keys

        if (missingCommands.isNotEmpty()) {
            plugin.logger.severe("Commands missing from plugin.yml: ${missingCommands.joinToString()}.")
            return false
        }

        commands.getValue(CommandsName.FOOD_COMMAND).setExecutor(
            FoodCommand(
                playerFeedbackService = playerFeedbackService,
                cooldownSeconds = cooldownSeconds
            )
        )

        commands.getValue(CommandsName.HEALTH_COMMAND).setExecutor(
            HealthCommand(
                playerFeedbackService = playerFeedbackService,
                cooldownSeconds = cooldownSeconds
            )
        )

        commands.getValue(CommandsName.CRAFT_COMMAND).setExecutor(
            CraftCommand(playerFeedbackService = playerFeedbackService)
        )

        commands.getValue(CommandsName.LIGHT_COMMAND).setExecutor(
            LightCommand(
                playerFeedbackService = playerFeedbackService,
                cooldownSeconds = cooldownSeconds
            )
        )

        commands.getValue(CommandsName.LINKEDIN_COMMAND).setExecutor(
            SocialProfileCommand(
                playerFeedbackService = playerFeedbackService,
                messageKey = SocialMessageKeys.LINKEDIN
            )
        )

        commands.getValue(CommandsName.GITHUB_COMMAND).setExecutor(
            SocialProfileCommand(
                playerFeedbackService = playerFeedbackService,
                messageKey = SocialMessageKeys.GITHUB
            )
        )

        commands.getValue(CommandsName.COSMETICS_COMMAND).setExecutor(
            CosmeticsCommand(
                cosmeticsMenuService = cosmeticsMenuService,
                playerFeedbackService = playerFeedbackService
            )
        )

        return true
    }
}