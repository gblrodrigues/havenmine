package me.gblrod.havenmine.features.particles.movement.repository

import io.mockk.every
import io.mockk.mockk
import me.gblrod.havenmine.core.database.SqliteDatabase
import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class SqliteMovementTrailRepositoryTest {
    @TempDir
    lateinit var tempDirectory: Path
    private lateinit var database: SqliteDatabase
    private lateinit var repository: SqliteMovementTrailRepository

    @BeforeEach
    fun setUp() {
        val plugin = mockk<JavaPlugin>()

        every {
            plugin.dataFolder
        } returns tempDirectory.toFile()

        every {
            plugin.logger
        } returns Logger.getLogger("HavenMineTest")

        database = SqliteDatabase(plugin)
        database.initialize()

        repository = SqliteMovementTrailRepository(
            plugin = plugin,
            database = database
        )
    }

    @AfterEach
    fun tearDown() {
        repository.close()
    }

    @Test
    fun saveAndLoadReturnsSavedTrail() {
        val playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc")

        repository.save(
            playerId = playerId,
            trail = MovementTrailType.FLAME
        ).get(5, TimeUnit.SECONDS)

        val loadedTrail = repository.load(playerId = playerId).get(5, TimeUnit.SECONDS)

        assertEquals(MovementTrailType.FLAME, loadedTrail)
    }

    @Test
    fun savingAnotherTrailForSamePlayerUpdatesExistingRecord() {
        val playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc")

        repository.save(
            playerId = playerId,
            trail = MovementTrailType.FLAME
        ).get(5, TimeUnit.SECONDS)

        repository.save(
            playerId = playerId,
            trail = MovementTrailType.NATURE
        ).get(5, TimeUnit.SECONDS)

        val loadedTrail = repository.load(playerId).get(5, TimeUnit.SECONDS)
        assertEquals(MovementTrailType.NATURE, loadedTrail)

        val count = database.openConnection().use { connection ->
            connection.prepareStatement(
                """ 
                        SELECT COUNT(*)
                        FROM movement_trail_selections
                        WHERE player_uuid = ?
                        """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())

                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    resultSet.getInt(1)
                }
            }
        }

        assertEquals(1, count)
    }

    @Test
    fun savingNullDeletesSavedTrail() {
        val playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc")

        repository.save(
            playerId = playerId,
            trail = MovementTrailType.CHERRY
        ).get(5, TimeUnit.SECONDS)

        val loadedTrailCherry = repository.load(playerId).get(5, TimeUnit.SECONDS)
        assertEquals(MovementTrailType.CHERRY, loadedTrailCherry)

        repository.save(
            playerId = playerId,
            trail = null
        ).get(5, TimeUnit.SECONDS)

        val loadedTrailNull = repository.load(playerId).get(5, TimeUnit.SECONDS)
        assertNull(loadedTrailNull)

        val count = database.openConnection().use { connection ->
            connection.prepareStatement(
                """ 
                        SELECT COUNT(*)
                        FROM movement_trail_selections
                        WHERE player_uuid = ?
                        """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())

                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    resultSet.getInt(1)
                }
            }
        }

        assertEquals(0, count)
    }

    @Test
    fun loadReturnsNullWhenPlayerHasNoSelection() {
        val playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc")
        val loadedTrail = repository.load(playerId = playerId).get(5, TimeUnit.SECONDS)

        assertNull(loadedTrail)
    }

    @Test
    fun loadRemovesUnknownStoredTrailKey() {
        val playerId = UUID.fromString("12345678-1234-1234-1234-123456789abc")

        database.openConnection().use { connection ->
            connection.prepareStatement(
                """ 
                        INSERT INTO movement_trail_selections (player_uuid, trail_key)
                        VALUES (?, ?) 
                        """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())
                statement.setString(2, "old_fire")
                statement.executeUpdate()
            }
        }

        val loadedTrail = repository.load(playerId).get(5, TimeUnit.SECONDS)
        assertNull(loadedTrail)

        val count = database.openConnection().use { connection ->
            connection.prepareStatement(
                """ 
                        SELECT COUNT(*)
                        FROM movement_trail_selections
                        WHERE player_uuid = ?
                        """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())

                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    resultSet.getInt(1)
                }
            }
        }

        assertEquals(0, count)
    }

    @Test
    fun savesSelectionsIndependentlyForDifferentPlayers() {
        val playerOneId = UUID.fromString("12345678-1234-1234-1234-123456789abc")
        val playerTwoId = UUID.fromString("12345678-1234-1234-1234-123456789def")

        repository.save(
            playerId = playerOneId,
            trail = MovementTrailType.FLAME
        ).get(5, TimeUnit.SECONDS)

        repository.save(
            playerId = playerTwoId,
            trail = MovementTrailType.CHERRY
        ).get(5, TimeUnit.SECONDS)

        val loadedPlayerOneTrail = repository.load(playerId = playerOneId).get(5, TimeUnit.SECONDS)
        assertEquals(MovementTrailType.FLAME, loadedPlayerOneTrail)

        val loadedPlayerTwoTrail = repository.load(playerId = playerTwoId).get(5, TimeUnit.SECONDS)
        assertEquals(MovementTrailType.CHERRY, loadedPlayerTwoTrail)

        val count = database.openConnection().use { connection ->
            connection.prepareStatement(
                """ 
                        SELECT COUNT(*)
                        FROM movement_trail_selections
                        """.trimIndent()
            ).use { statement ->
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    resultSet.getInt(1)
                }
            }
        }

        assertEquals(2, count)
    }
}