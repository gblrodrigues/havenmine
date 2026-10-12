package me.gblrod.havenmine.features.particles.movement.service

import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import org.bukkit.entity.Player
import java.util.UUID

class MovementTrailService {
    private val activeTrails = mutableMapOf<UUID, MovementTrailType>()

    fun getActiveTrail(player: Player): MovementTrailType? {
        return activeTrails[player.uniqueId]
    }

    fun toggleTrail(player: Player, trail: MovementTrailType): Boolean {
        val playerId = player.uniqueId
        val currentTrail = activeTrails[playerId]

        return if (currentTrail == trail) {
            activeTrails.remove(playerId)
            false
        } else {
            activeTrails[playerId] = trail
            true
        }
    }

    fun clear(player: Player) {
        activeTrails.remove(player.uniqueId)
    }
}