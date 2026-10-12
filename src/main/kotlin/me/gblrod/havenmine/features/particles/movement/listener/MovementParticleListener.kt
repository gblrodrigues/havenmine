package me.gblrod.havenmine.features.particles.movement.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.gblrod.havenmine.features.particles.movement.constants.MovementParticleConstants
import me.gblrod.havenmine.features.particles.movement.model.JumpState
import me.gblrod.havenmine.features.particles.movement.service.MovementTrailService
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*
import kotlin.math.abs

class MovementParticleListener(
    private val movementTrailService: MovementTrailService
) : Listener {
    private val jumpStates = mutableMapOf<UUID, JumpState>()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player

        if (movementTrailService.getActiveTrail(player) == null) return
        if (player.isFlying || player.isGliding || player.isInsideVehicle) return

        jumpStates[player.uniqueId] = JumpState()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val playerId = player.uniqueId
        val onGround = (player as Entity).isOnGround

        if (event is PlayerTeleportEvent) {
            jumpStates.remove(playerId)
            return
        }

        val trail = movementTrailService.getActiveTrail(player)

        if (trail == null) {
            jumpStates.remove(playerId)
            return
        }

        val from = event.from
        val to = event.to

        if (from.world != to.world) {
            jumpStates.remove(playerId)
            return
        }

        if (player.isFlying || player.isGliding || player.isInsideVehicle) {
            jumpStates.remove(playerId)
            return
        }

        val deltaX = to.x - from.x
        val deltaY = to.y - from.y
        val deltaZ = to.z - from.z

        val horizontalDistanceSquared = deltaX * deltaX + deltaZ * deltaZ
        val horizontalMovement = horizontalDistanceSquared >= MovementParticleConstants.MIN_MOVEMENT_SQUARED

        val verticalMovement = abs(deltaY) >= MovementParticleConstants.MIN_VERTICAL_MOVEMENT

        val jumpState = jumpStates[playerId]

        if (jumpState != null && deltaY > MovementParticleConstants.MIN_VERTICAL_MOVEMENT) {
            jumpState.hasAscended = true
        }

        if (jumpState?.hasAscended == true && onGround && deltaY <= 0.0) {
            jumpStates.remove(playerId)
        }

        val isJumping = jumpStates[playerId]?.hasAscended == true
        val isWalkingOnGround = onGround && horizontalMovement

        if (!isJumping && !isWalkingOnGround) return
        if (!horizontalMovement && !verticalMovement) return

        val particleLocation = to.clone().add(0.0, MovementParticleConstants.FEET_OFFSET, 0.0)

        to.world.spawnParticle(
            trail.particle,
            particleLocation,
            MovementParticleConstants.PARTICLE_COUNT,
            MovementParticleConstants.OFFSET_X,
            MovementParticleConstants.OFFSET_Y,
            MovementParticleConstants.OFFSET_Z,
            MovementParticleConstants.PARTICLE_EXTRA
        )
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        jumpStates.remove(event.player.uniqueId)
        movementTrailService.clear(event.player)
    }
}