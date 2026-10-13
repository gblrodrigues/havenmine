package me.gblrod.havenmine.features.particles.movement.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import me.gblrod.havenmine.features.particles.movement.repository.MovementTrailRepository
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.CompletableFuture

class MovementTrailServiceTest {
    private val playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc")
    private lateinit var player: Player
    private lateinit var repository: MovementTrailRepository
    private lateinit var service: MovementTrailService

    @BeforeEach
    fun setUp() {
        player = mockk()
        repository = mockk()

        every { player.uniqueId } returns playerId

        service = MovementTrailService(movementTrailRepository = repository)
    }

    @Test
    fun equippingATrailActivatesItAndRequestsPersistence() {
        every {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.FLAME
            )
        } returns CompletableFuture.completedFuture(null)

        val result = service.toggleTrail(
            player = player,
            trail = MovementTrailType.FLAME
        )

        assertTrue(result)
        assertEquals(MovementTrailType.FLAME, service.getActiveTrail(player))

        verify(exactly = 1) {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.FLAME
            )
        }
    }

    @Test
    fun unequippingActiveTrailRemovesItAndRequestsDeletion() {
        every {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.FLAME
            )
        } returns CompletableFuture.completedFuture(null)

        every {
            repository.save(
                playerId = playerId,
                trail = null
            )
        } returns CompletableFuture.completedFuture(null)

        val equipResult = service.toggleTrail(
            player = player,
            trail = MovementTrailType.FLAME
        )

        assertTrue(equipResult)
        assertEquals(MovementTrailType.FLAME, service.getActiveTrail(player))

        val unequipResult = service.toggleTrail(
            player = player,
            trail = MovementTrailType.FLAME
        )

        assertFalse(unequipResult)
        assertNull(service.getActiveTrail(player))

        verify(exactly = 1) {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.FLAME
            )
        }

        verify(exactly = 1) {
            repository.save(
                playerId = playerId,
                trail = null
            )
        }
    }

    @Test
    fun switchingTrailReplacesCurrentTrailAndPersistsNewSelection() {
        every {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.FLAME
            )
        } returns CompletableFuture.completedFuture(null)

        every {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.STARDUST
            )
        } returns CompletableFuture.completedFuture(null)

        val result = service.toggleTrail(
            player = player,
            trail = MovementTrailType.FLAME
        )

        assertTrue(result)
        assertEquals(MovementTrailType.FLAME, service.getActiveTrail(player))

        val switchingTrail = service.toggleTrail(
            player = player,
            trail = MovementTrailType.STARDUST
        )

        verify(exactly = 1) {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.FLAME
            )
        }

        assertTrue(switchingTrail)
        assertEquals(MovementTrailType.STARDUST, service.getActiveTrail(player))

        verify(exactly = 1) {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.STARDUST
            )
        }
    }

    @Test
    fun restoreTrailAppliesSelectionWhenRevisionMatches() {
        val result = service.restoreTrail(
            player = player,
            trail = MovementTrailType.FROST,
            expectedRevision = 0L
        )

        assertTrue(result)
        assertEquals(MovementTrailType.FROST, service.getActiveTrail(player))
    }

    @Test
    fun restoreTrailRejectsStaleRevision() {
        every {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.NATURE
            )
        } returns CompletableFuture.completedFuture(null)

        val switchingTrail = service.toggleTrail(
            player = player,
            trail = MovementTrailType.NATURE
        )

        val result = service.restoreTrail(
            player = player,
            trail = MovementTrailType.FLAME,
            expectedRevision = 0L
        )

        assertFalse(result)
        assertTrue(switchingTrail)
        assertEquals(MovementTrailType.NATURE, service.getActiveTrail(player))

        verify(exactly = 1) {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.NATURE
            )
        }
    }

    @Test
    fun clearRemovesActiveTrailAndResetsRevision() {
        every {
            repository.save(
                playerId = playerId,
                trail = MovementTrailType.WITCH
            )
        } returns CompletableFuture.completedFuture(null)

        val result = service.toggleTrail(
            player = player,
            trail = MovementTrailType.WITCH
        )

        assertTrue(result)

        val checkRevision = service.getStateRevision(playerId)
        assertEquals(1L, checkRevision)

        val confirmReturn = service.getActiveTrail(player)
        assertEquals(MovementTrailType.WITCH, confirmReturn)

        service.clear(player)
        assertNull(service.getActiveTrail(player))

        verify(exactly = 0) {
            repository.save(
                playerId = playerId,
                trail = null
            )
        }

        val revision = service.getStateRevision(playerId)
        assertEquals(0L, revision)
    }
}