package me.gblrod.havenmine

import me.gblrod.havenmine.core.bootstrap.CommandRegister
import me.gblrod.havenmine.core.service.MessageService
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuContentFactory
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuService
import me.gblrod.havenmine.features.cosmetics.listener.CosmeticsMenuListener
import me.gblrod.havenmine.features.particles.movement.listener.MovementParticleListener
import me.gblrod.havenmine.features.particles.movement.listener.MovementTrailPersistenceListener
import me.gblrod.havenmine.features.particles.movement.repository.MovementTrailStorage
import me.gblrod.havenmine.features.particles.movement.service.MovementTrailService
import org.bukkit.plugin.java.JavaPlugin

class HavenMinePlugin : JavaPlugin() {
    private var movementTrailStorage: MovementTrailStorage? = null

    override fun onEnable() {
        saveDefaultConfig()

        val defaultCooldownSeconds = config
            .getLong("cooldowns.default_seconds", 300L)
            .coerceAtLeast(minimumValue = 0L)

        val messageService = MessageService(config = config)
        val playerFeedbackService = PlayerFeedbackService(messageService = messageService)

        val storage = MovementTrailStorage(plugin = this)
        movementTrailStorage = storage

        val movementTrailRepository = storage.initialize() ?: run {
            server.pluginManager.disablePlugin(this)
            return
        }

        val movementTrailService = MovementTrailService(movementTrailRepository = movementTrailRepository)

        val cosmeticsMenuContentFactory =
            CosmeticsMenuContentFactory(plugin = this, movementTrailService = movementTrailService)
        val cosmeticsMenuService =
            CosmeticsMenuService(plugin = this, cosmeticsMenuContentFactory = cosmeticsMenuContentFactory)

        val commandRegistrar = CommandRegister(
            plugin = this,
            playerFeedbackService = playerFeedbackService,
            cosmeticsMenuService = cosmeticsMenuService,
            cooldownSeconds = defaultCooldownSeconds
        )

        if (!commandRegistrar.register()) {
            server.pluginManager.disablePlugin(this)
            return
        }

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

        server.pluginManager.registerEvents(
            MovementTrailPersistenceListener(
                plugin = this,
                movementTrailService = movementTrailService,
                movementTrailRepository = movementTrailRepository
            ),
            this
        )

        logger.info("HavenMine enabled!")
    }

    override fun onDisable() {
        movementTrailStorage?.close()
        movementTrailStorage = null

        logger.info("HavenMine disabled!")
    }
}