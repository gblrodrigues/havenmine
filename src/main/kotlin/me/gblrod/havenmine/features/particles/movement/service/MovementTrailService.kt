package me.gblrod.havenmine.features.particles.movement.service

import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import me.gblrod.havenmine.features.particles.movement.repository.MovementTrailRepository
import org.bukkit.entity.Player
import java.util.UUID

class MovementTrailService(
    private val movementTrailRepository: MovementTrailRepository
) {
    private val activeTrails = mutableMapOf<UUID, MovementTrailType>()
    private val stateRevisions = mutableMapOf<UUID, Long>()

    fun getActiveTrail(player: Player): MovementTrailType? {
        return activeTrails[player.uniqueId]
    }

    fun toggleTrail(player: Player, trail: MovementTrailType): Boolean {
        val playerId = player.uniqueId
        val currentTrail = activeTrails[playerId]
        val updatedTrail = if (currentTrail == trail) null else trail

        if (updatedTrail == null) activeTrails.remove(playerId) else activeTrails[playerId] = updatedTrail

        stateRevisions[playerId] = getStateRevision(playerId) + 1
        movementTrailRepository.save(playerId = playerId, trail = updatedTrail)

        return updatedTrail != null
    }

    fun getStateRevision(playerId: UUID): Long {
        return stateRevisions[playerId] ?: 0L
    }

    fun restoreTrail(player: Player, trail: MovementTrailType?, expectedRevision: Long): Boolean {
        val playerId = player.uniqueId

        if (getStateRevision(playerId) != expectedRevision) return false
        if (trail == null) activeTrails.remove(playerId) else activeTrails[playerId] = trail

        return true
    }

    fun clear(player: Player) {
        activeTrails.remove(player.uniqueId)
        stateRevisions.remove(player.uniqueId)
    }
}