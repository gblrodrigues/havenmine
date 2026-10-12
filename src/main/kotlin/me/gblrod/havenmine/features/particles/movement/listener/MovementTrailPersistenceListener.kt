package me.gblrod.havenmine.features.particles.movement.listener

import me.gblrod.havenmine.features.particles.movement.repository.MovementTrailRepository
import me.gblrod.havenmine.features.particles.movement.service.MovementTrailService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class MovementTrailPersistenceListener(
    private val plugin: JavaPlugin,
    private val movementTrailService: MovementTrailService,
    private val movementTrailRepository: MovementTrailRepository
) : Listener {
    private val pendingLoads = mutableMapOf<UUID, Any>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerId = player.uniqueId
        val loadToken = Any()
        val expectedRevision = movementTrailService.getStateRevision(playerId)

        pendingLoads[playerId] = loadToken

        movementTrailRepository.load(playerId).whenComplete { trail, error ->
            if (!plugin.isEnabled) return@whenComplete

            plugin.server.scheduler.runTask(plugin, Runnable {
                if (pendingLoads[playerId] !== loadToken) return@Runnable
                pendingLoads.remove(playerId)

                val onlinePlayer = plugin.server.getPlayer(playerId) ?: return@Runnable
                if (error != null) return@Runnable

                movementTrailService.restoreTrail(
                    player = onlinePlayer,
                    trail = trail,
                    expectedRevision = expectedRevision
                )
            })
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        pendingLoads.remove(event.player.uniqueId)
        movementTrailService.clear(event.player)
    }
}