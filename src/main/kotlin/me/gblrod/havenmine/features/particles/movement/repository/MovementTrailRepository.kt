package me.gblrod.havenmine.features.particles.movement.repository

import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import java.util.UUID
import java.util.concurrent.CompletableFuture

interface MovementTrailRepository : AutoCloseable {
    fun load(playerId: UUID): CompletableFuture<MovementTrailType?>
    fun save(playerId: UUID, trail: MovementTrailType?): CompletableFuture<Void>
    override fun close()
}